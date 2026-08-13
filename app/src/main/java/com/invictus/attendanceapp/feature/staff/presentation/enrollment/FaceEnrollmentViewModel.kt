package com.invictus.attendanceapp.feature.staff.presentation.enrollment

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.staff.domain.usecase.EnrollFaceUseCase
import com.invictus.attendanceapp.feature.staff.domain.usecase.GetStaffUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceEnrollmentViewModel @Inject constructor(
    private val getStaffUseCase: GetStaffUseCase,
    private val enrollFaceUseCase: EnrollFaceUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val staffId: String = checkNotNull(savedStateHandle["staffId"])

    private val _uiState = MutableStateFlow(FaceEnrollmentUiState())
    val uiState: StateFlow<FaceEnrollmentUiState> = _uiState.asStateFlow()

    init {
        loadStaff()
    }

    private fun loadStaff() {
        viewModelScope.launch {
            val staff = getStaffUseCase(staffId)
            _uiState.update { it.copy(staff = staff) }
        }
    }

    fun captureAndEnroll(bitmap: Bitmap) {
        _uiState.update { it.copy(isProcessing = true, error = null) }

        viewModelScope.launch {
            val result = enrollFaceUseCase(staffId, bitmap)
            when (result) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isProcessing = false, isSuccess = true) }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isProcessing = false, error = result.error.message) }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
