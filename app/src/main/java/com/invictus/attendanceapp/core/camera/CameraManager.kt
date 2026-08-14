package com.invictus.attendanceapp.core.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor
import kotlin.coroutines.resume

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    isProcessing: Boolean = false,
    onFaceDetectionChanged: (Boolean) -> Unit = {},
    onImageCaptureCreated: (ImageCapture) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    val fastFaceDetector = remember {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setMinFaceSize(0.20f)
            .build()
        FaceDetection.getClient(options)
    }

    AndroidView(
        factory = {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                onImageCaptureCreated(imageCapture)

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                    if (isProcessing) {
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val inputImage = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )
                        fastFaceDetector.process(inputImage)
                            .addOnSuccessListener { faces ->
                                val hasSingleFace = faces.size == 1
                                onFaceDetectionChanged(hasSingleFace)
                            }
                            .addOnFailureListener {
                                onFaceDetectionChanged(false)
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build()

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
            previewView
        },
        modifier = modifier
    )
}

suspend fun ImageCapture.takePictureBitmap(context: Context): Bitmap = suspendCancellableCoroutine { continuation ->
    val executor: Executor = ContextCompat.getMainExecutor(context)
    takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            val bitmap = imageProxyToBitmap(image)
            image.close()
            if (bitmap != null) {
                continuation.resume(bitmap)
            } else {
                continuation.cancel(Exception("Failed to decode image from camera capture"))
            }
        }

        override fun onError(exception: ImageCaptureException) {
            continuation.cancel(exception)
        }
    })
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    val planeProxy = image.planes[0]
    val buffer = planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
    val rotationDegrees = image.imageInfo.rotationDegrees

    return if (rotationDegrees != 0) {
        val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
}
