package com.invictus.attendanceapp.feature.staff.domain.model

data class Staff(
    val id: String,
    val name: String,
    val employeeId: String,
    val faceEmbedding: List<Float>?,
    val faceImagePath: String?
) {
    val isFaceEnrolled: Boolean get() = faceEmbedding != null && faceEmbedding.isNotEmpty()
}
