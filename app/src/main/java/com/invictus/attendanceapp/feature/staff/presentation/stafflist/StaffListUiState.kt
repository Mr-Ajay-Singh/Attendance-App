package com.invictus.attendanceapp.feature.staff.presentation.stafflist

import com.invictus.attendanceapp.feature.staff.domain.model.Staff

data class StaffListUiState(
    val staffList: List<Staff> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = ""
)
