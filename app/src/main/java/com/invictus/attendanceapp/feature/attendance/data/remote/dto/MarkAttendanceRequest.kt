package com.invictus.attendanceapp.feature.attendance.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MarkAttendanceRequest(
    val embedding: List<Float>,
    val selfie: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)
