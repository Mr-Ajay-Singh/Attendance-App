package com.invictus.attendanceapp.feature.staff.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class StaffDto(
    val id: String = "",
    val name: String = "",
    val employeeId: String = "",
    val faceEnrolled: Boolean = false,
    val faceImageUrl: String? = null,
    val faceEmbedding: List<Float>? = null
)
