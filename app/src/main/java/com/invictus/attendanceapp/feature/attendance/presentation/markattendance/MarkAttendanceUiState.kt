package com.invictus.attendanceapp.feature.attendance.presentation.markattendance

import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance
import com.invictus.attendanceapp.feature.staff.domain.model.Staff

data class MarkAttendanceUiState(
    val staff: Staff? = null,
    val isCameraOpen: Boolean = false,
    val isProcessing: Boolean = false,
    val recordedAttendance: Attendance? = null,
    val error: String? = null
)
