package com.invictus.attendanceapp.core.image

import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

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
        val fileName = "${prefix}_${UUID.randomUUID()}.jpg"
        val file = File(selfiesDir, fileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        file.absolutePath
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
