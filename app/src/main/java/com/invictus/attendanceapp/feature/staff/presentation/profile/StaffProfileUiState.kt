package com.invictus.attendanceapp.feature.staff.presentation.profile

import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance
import com.invictus.attendanceapp.feature.staff.domain.model.Staff

data class StaffProfileUiState(
    val staff: Staff? = null,
    val attendanceHistory: List<Attendance> = emptyList(),
    val isLoading: Boolean = false
)
