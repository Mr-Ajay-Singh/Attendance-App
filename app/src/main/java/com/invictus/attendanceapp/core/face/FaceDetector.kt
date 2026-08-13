package com.invictus.attendanceapp.core.face

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class FaceDetector @Inject constructor() {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(0.15f)
        .build()

    private val detector = FaceDetection.getClient(options)

    suspend fun detectAndCropFace(bitmap: Bitmap): AppResult<Bitmap> {
        return suspendCancellableCoroutine { continuation ->
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            detector.process(inputImage)
                .addOnSuccessListener { faces: List<Face> ->
                    when {
                        faces.isEmpty() -> {
                            continuation.resume(AppResult.Error(AppError.FaceNotFound))
                        }
                        faces.size > 1 -> {
                            continuation.resume(AppResult.Error(AppError.MultipleFacesDetected))
                        }
                        else -> {
                            val face = faces[0]
                            val cropped = cropFace(bitmap, face.boundingBox)
                            if (cropped != null) {
                                continuation.resume(AppResult.Success(cropped))
                            } else {
                                continuation.resume(AppResult.Error(AppError.FaceNotFound))
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resume(AppResult.Error(AppError.Custom(e.message ?: "Face detection failed")))
                }
        }
    }

    private fun cropFace(original: Bitmap, boundingBox: Rect): Bitmap? {
        return try {
            val left = boundingBox.left.coerceAtLeast(0)
            val top = boundingBox.top.coerceAtLeast(0)
            val right = boundingBox.right.coerceAtMost(original.width)
            val bottom = boundingBox.bottom.coerceAtMost(original.height)

            val width = right - left
            val height = bottom - top

            if (width > 0 && height > 0) {
                Bitmap.createBitmap(original, left, top, width, height)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
