package com.invictus.attendanceapp.feature.staff.presentation.addstaff

import com.invictus.attendanceapp.feature.staff.domain.model.Staff

data class AddStaffUiState(
    val nameInput: String = "",
    val employeeIdInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val createdStaff: Staff? = null,
    val showCredentialsDialog: Boolean = false
)
