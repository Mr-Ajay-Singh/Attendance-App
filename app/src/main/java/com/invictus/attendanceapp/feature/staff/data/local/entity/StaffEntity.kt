package com.invictus.attendanceapp.feature.staff.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "staff")
data class StaffEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val employeeId: String,
    val faceEnrolled: Boolean = false,
    val faceImageUrl: String? = null,
    val faceEmbedding: List<Float>? = null,
    val faceImagePath: String? = null
)
