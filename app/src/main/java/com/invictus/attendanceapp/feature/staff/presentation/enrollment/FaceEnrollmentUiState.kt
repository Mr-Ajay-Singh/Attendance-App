package com.invictus.attendanceapp.feature.staff.presentation.enrollment

import com.invictus.attendanceapp.feature.staff.domain.model.Staff

data class FaceEnrollmentUiState(
    val staff: Staff? = null,
    val isProcessing: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
