package com.invictus.attendanceapp.feature.attendance.presentation.markattendance

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.core.network.AuthTokenProvider
import com.invictus.attendanceapp.feature.attendance.domain.usecase.MarkAttendanceUseCase
import com.invictus.attendanceapp.feature.staff.domain.usecase.GetStaffUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarkAttendanceViewModel @Inject constructor(
    private val getStaffUseCase: GetStaffUseCase,
    private val markAttendanceUseCase: MarkAttendanceUseCase,
    private val tokenProvider: AuthTokenProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val staffId: String = savedStateHandle["staffId"] ?: tokenProvider.getStaffId() ?: ""

    private val _uiState = MutableStateFlow(MarkAttendanceUiState())
    val uiState: StateFlow<MarkAttendanceUiState> = _uiState.asStateFlow()

    init {
        if (staffId.isNotBlank()) {
            loadStaff()
        }
    }

    private fun loadStaff() {
        viewModelScope.launch {
            val staff = getStaffUseCase(staffId)
            _uiState.update { it.copy(staff = staff) }
        }
    }

    fun openCamera() {
        _uiState.update { it.copy(isCameraOpen = true, error = null) }
    }

    fun closeCamera() {
        _uiState.update { it.copy(isCameraOpen = false) }
    }

    fun processSelfieAndMarkAttendance(selfieBitmap: Bitmap) {
        _uiState.update { it.copy(isProcessing = true, error = null) }

        viewModelScope.launch {
            val result = markAttendanceUseCase(staffId, selfieBitmap)
            when (result) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            isCameraOpen = false,
                            recordedAttendance = result.data
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            error = result.error.message
                        )
                    }
                }
            }
        }
    }

    fun dismissSuccessDialog() {
        _uiState.update { it.copy(recordedAttendance = null) }
    }
}
