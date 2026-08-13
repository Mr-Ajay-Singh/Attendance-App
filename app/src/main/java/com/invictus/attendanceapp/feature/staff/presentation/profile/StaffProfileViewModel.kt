package com.invictus.attendanceapp.feature.staff.presentation.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invictus.attendanceapp.feature.attendance.domain.usecase.GetAttendanceHistoryUseCase
import com.invictus.attendanceapp.feature.staff.domain.usecase.GetStaffUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffProfileViewModel @Inject constructor(
    private val getStaffUseCase: GetStaffUseCase,
    private val getAttendanceHistoryUseCase: GetAttendanceHistoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val staffId: String = checkNotNull(savedStateHandle["staffId"])

    private val _uiState = MutableStateFlow(StaffProfileUiState(isLoading = true))
    val uiState: StateFlow<StaffProfileUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val staff = getStaffUseCase(staffId)
            _uiState.update { it.copy(staff = staff) }

            getAttendanceHistoryUseCase(staffId).collect { history ->
                _uiState.update { it.copy(attendanceHistory = history, isLoading = false) }
            }
        }
    }
}
