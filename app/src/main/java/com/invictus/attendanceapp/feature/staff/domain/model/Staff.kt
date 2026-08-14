package com.invictus.attendanceapp.feature.staff.domain.model

data class Staff(
    val id: String,
    val name: String,
    val employeeId: String,
    val faceEnrolled: Boolean = false,
    val faceImageUrl: String? = null,
    val faceEmbedding: List<Float>? = null,
    val faceImagePath: String? = null
) {
    val isFaceEnrolled: Boolean get() = faceEnrolled || (faceEmbedding != null && faceEmbedding.isNotEmpty()) || !faceImageUrl.isNullOrBlank()
}
