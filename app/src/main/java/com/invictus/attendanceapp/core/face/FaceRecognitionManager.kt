package com.invictus.attendanceapp.core.face

import android.graphics.Bitmap
import com.invictus.attendanceapp.core.common.AppResult

interface FaceRecognitionManager {
    suspend fun generateEmbedding(image: Bitmap): AppResult<List<Float>>
    suspend fun compare(source: List<Float>, target: List<Float>): Float
}
