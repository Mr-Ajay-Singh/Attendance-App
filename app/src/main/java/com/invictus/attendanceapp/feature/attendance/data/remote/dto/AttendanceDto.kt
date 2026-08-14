package com.invictus.attendanceapp.feature.attendance.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceDto(
    val id: String = "",
    val staffId: String = "",
    val timestamp: Long = 0L,
    val selfieUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

@Serializable
data class AttendanceHistoryResponse(
    val data: List<AttendanceDto> = emptyList()
)
