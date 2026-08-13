package com.invictus.attendanceapp.feature.staff.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "staff")
data class StaffEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val employeeId: String,
    val faceEmbedding: List<Float>?,
    val faceImagePath: String?
)
