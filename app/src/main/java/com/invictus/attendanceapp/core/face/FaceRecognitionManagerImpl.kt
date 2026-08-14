package com.invictus.attendanceapp.core.face

import android.graphics.Bitmap
import com.invictus.attendanceapp.core.common.AppResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaceRecognitionManagerImpl @Inject constructor(
    private val faceDetector: FaceDetector,
    private val faceEmbedder: FaceEmbedder,
    private val faceMatcher: FaceMatcher
) : FaceRecognitionManager {

    override suspend fun generateEmbedding(image: Bitmap): AppResult<List<Float>> {
        return when (val cropResult = faceDetector.detectAndCropFace(image)) {
            is AppResult.Success -> {
                val embedding = faceEmbedder.generateEmbedding(cropResult.data)
                AppResult.Success(embedding)
            }
            is AppResult.Error -> {
                cropResult
            }
        }
    }

    override suspend fun compare(source: List<Float>, target: List<Float>): Float {
        return faceMatcher.calculateCosineSimilarity(source, target)
    }
}
