package com.invictus.attendanceapp.feature.staff.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EnrollFaceRequest(
    val embedding: List<Float>,
    val faceImageUrl: String? = null
)
