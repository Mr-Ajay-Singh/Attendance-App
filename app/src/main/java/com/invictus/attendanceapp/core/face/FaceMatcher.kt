package com.invictus.attendanceapp.core.face

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

@Singleton
class FaceMatcher @Inject constructor() {

    fun calculateCosineSimilarity(
        embedding1: List<Float>,
        embedding2: List<Float>
    ): Float {
        if (embedding1.isEmpty() || embedding2.isEmpty() || embedding1.size != embedding2.size) {
            return 0.0f
        }

        var dotProduct = 0.0f
        var normA = 0.0f
        var normB = 0.0f

        for (i in embedding1.indices) {
            val a = embedding1[i]
            val b = embedding2[i]
            dotProduct += a * b
            normA += a * a
            normB += b * b
        }

        val denominator = (sqrt(normA.toDouble()) * sqrt(normB.toDouble())).toFloat()
        return if (denominator > 1e-6f) {
            dotProduct / denominator
        } else {
            0.0f
        }
    }

    fun isMatch(
        embedding1: List<Float>,
        embedding2: List<Float>,
        threshold: Float = FaceRecognitionConfig.MATCH_THRESHOLD
    ): Boolean {
        val similarity = calculateCosineSimilarity(embedding1, embedding2)
        return similarity >= threshold
    }
}
