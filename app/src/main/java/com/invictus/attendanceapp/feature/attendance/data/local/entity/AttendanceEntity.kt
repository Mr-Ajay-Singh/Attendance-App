package com.invictus.attendanceapp.feature.attendance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey
    val id: String,
    val staffId: String,
    val timestamp: Long,
    val selfiePath: String,
    val latitude: Double,
    val longitude: Double
)
