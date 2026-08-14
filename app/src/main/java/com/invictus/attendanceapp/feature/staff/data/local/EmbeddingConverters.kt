package com.invictus.attendanceapp.feature.staff.data.local

import androidx.room.TypeConverter

class EmbeddingConverters {

    @TypeConverter
    fun fromEmbeddingList(embedding: List<Float>?): String? {
        return embedding?.joinToString(separator = ",")
    }

    @TypeConverter
    fun toEmbeddingList(embeddingString: String?): List<Float>? {
        if (embeddingString.isNullOrEmpty()) return null
        return try {
            embeddingString.split(",").map { it.toFloat() }
        } catch (e: Exception) {
            null
        }
    }
}
