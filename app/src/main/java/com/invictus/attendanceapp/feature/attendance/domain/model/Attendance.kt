package com.invictus.attendanceapp.feature.attendance.domain.model

data class Attendance(
    val id: String,
    val staffId: String,
    val timestamp: Long,
    val selfiePath: String,
    val latitude: Double,
    val longitude: Double
)
