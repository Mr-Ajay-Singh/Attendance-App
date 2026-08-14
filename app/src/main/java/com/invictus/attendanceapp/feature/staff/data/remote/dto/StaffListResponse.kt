package com.invictus.attendanceapp.feature.staff.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class StaffListResponse(
    val data: List<StaffDto> = emptyList()
)
