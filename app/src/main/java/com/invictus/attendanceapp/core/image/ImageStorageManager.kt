package com.invictus.attendanceapp.core.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class ImageStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val selfiesDir: File
        get() = File(context.filesDir, "selfies").apply {
            if (!exists()) {
                mkdirs()
            }
        }

    suspend fun saveBitmap(bitmap: Bitmap, prefix: String = "selfie"): String = withContext(Dispatchers.IO) {
        val scaledBitmap = scaleBitmapDown(bitmap, maxDimension = 800)
        val fileName = "${prefix}_${UUID.randomUUID()}.jpg"
        val file = File(selfiesDir, fileName)
        FileOutputStream(file).use { out ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
        file.absolutePath
    }

    private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val maxOriginal = max(originalWidth, originalHeight)
        if (maxOriginal <= maxDimension) return bitmap

        val scale = maxDimension.toFloat() / maxOriginal
        val matrix = Matrix().apply { postScale(scale, scale) }
        return Bitmap.createBitmap(bitmap, 0, 0, originalWidth, originalHeight, matrix, true)
    }

    suspend fun deleteImage(path: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            if (file.exists()) file.delete() else false
        } catch (e: Exception) {
            false
        }
    }
}
