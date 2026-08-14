package com.invictus.attendanceapp.core.face

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.scale
import com.google.android.gms.tflite.java.TfLite
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.tensorflow.lite.InterpreterApi
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

@Singleton
class FaceEmbedder @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @Volatile
    private var interpreter: InterpreterApi? = null
    private val initMutex = Mutex()

    init {
        try {
            TfLite.initialize(context).addOnSuccessListener {
                try {
                    initInterpreterInternal()
                } catch (e: Exception) {
                    Log.e("FaceEmbedder", "Error creating Interpreter on init", e)
                }
            }.addOnFailureListener { e ->
                Log.e("FaceEmbedder", "TfLite.initialize failed", e)
            }
        } catch (e: Exception) {
            Log.e("FaceEmbedder", "Error starting TfLite initialization", e)
        }
    }

    private fun initInterpreterInternal(): InterpreterApi {
        val fileDescriptor = context.assets.openFd(FaceRecognitionConfig.MODEL_FILE_NAME)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        val options = InterpreterApi.Options().setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY)
        val newInterpreter = InterpreterApi.create(mappedByteBuffer, options)
        interpreter = newInterpreter
        return newInterpreter
    }

    suspend fun getOrInitInterpreter(): InterpreterApi? {
        interpreter?.let { return it }
        return initMutex.withLock {
            interpreter?.let { return it }
            try {
                TfLite.initialize(context).await()
                initInterpreterInternal()
            } catch (e: Exception) {
                Log.e("FaceEmbedder", "Failed to initialize TfLite Interpreter", e)
                null
            }
        }
    }

    suspend fun generateEmbedding(croppedFace: Bitmap): List<Float> = withContext(Dispatchers.Default) {
        val resized = croppedFace.scale(
            FaceRecognitionConfig.INPUT_IMAGE_SIZE,
            FaceRecognitionConfig.INPUT_IMAGE_SIZE
        )

        val activeInterpreter = getOrInitInterpreter()
        if (activeInterpreter != null) {
            Log.e("FaceEmbedder", "Got Active One")

            return@withContext runTFLiteInference(activeInterpreter, resized)
        }

        return@withContext fallbackFeatureExtraction(resized)
    }

    private fun runTFLiteInference(interpreter: InterpreterApi, bitmap: Bitmap): List<Float> {
        val inputBuffer = ByteBuffer.allocateDirect(4 * FaceRecognitionConfig.INPUT_IMAGE_SIZE * FaceRecognitionConfig.INPUT_IMAGE_SIZE * 3)
        inputBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(FaceRecognitionConfig.INPUT_IMAGE_SIZE * FaceRecognitionConfig.INPUT_IMAGE_SIZE)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixel in intValues) {
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            inputBuffer.putFloat((r - 0.5f) * 2.0f)
            inputBuffer.putFloat((g - 0.5f) * 2.0f)
            inputBuffer.putFloat((b - 0.5f) * 2.0f)
        }

        val outputArray = Array(1) { FloatArray(FaceRecognitionConfig.EMBEDDING_DIM) }
        interpreter.run(inputBuffer, outputArray)

        return l2Normalize(outputArray[0])
    }

    private fun fallbackFeatureExtraction(bitmap: Bitmap): List<Float> {
        // Deterministic image hash/histogram embedding when TFLite asset is omitted
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val embedding = FloatArray(FaceRecognitionConfig.EMBEDDING_DIM)
        for (i in pixels.indices) {
            val pixel = pixels[i]
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            val idx = i % FaceRecognitionConfig.EMBEDDING_DIM
            embedding[idx] += (r * 0.299f + g * 0.587f + b * 0.114f)
        }

        return l2Normalize(embedding)
    }

    private fun l2Normalize(vector: FloatArray): List<Float> {
        var sumSquares = 0.0f
        for (v in vector) {
            sumSquares += v * v
        }
        val norm = sqrt(sumSquares.toDouble()).toFloat().coerceAtLeast(1e-6f)
        return vector.map { it / norm }
    }
}
