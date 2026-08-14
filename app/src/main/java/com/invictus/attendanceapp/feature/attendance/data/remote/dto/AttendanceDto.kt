package com.invictus.attendanceapp.feature.attendance.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceDto(
    val id: String,
    val staffId: String,
    val timestamp: Long,
    val selfieUrl: String,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class AttendanceHistoryResponse(
    val data: List<AttendanceDto> = emptyList()
)
