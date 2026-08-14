package com.invictus.attendanceapp.feature.staff.presentation.addstaff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.staff.domain.usecase.AddStaffUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddStaffViewModel @Inject constructor(
    private val addStaffUseCase: AddStaffUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddStaffUiState())
    val uiState: StateFlow<AddStaffUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(nameInput = name, error = null) }
    }

    fun onEmployeeIdChanged(employeeId: String) {
        _uiState.update { it.copy(employeeIdInput = employeeId, error = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(passwordInput = password, error = null) }
    }

    fun generateRandomPassword() {
        val randomPass = UUID.randomUUID().toString().take(8)
        _uiState.update { it.copy(passwordInput = randomPass, error = null) }
    }

    fun addStaff() {
        val currentState = _uiState.value
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val employeeId = currentState.employeeIdInput.trim()
            val password = currentState.passwordInput.ifBlank { "password123" }

            val result = addStaffUseCase(
                name = currentState.nameInput.trim(),
                employeeId = employeeId,
                username = employeeId,
                password = password
            )
            when (result) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            createdStaff = result.data,
                            showCredentialsDialog = true,
                            passwordInput = password
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.error.message) }
                }
            }
        }
    }

    fun dismissCredentialsDialog() {
        _uiState.update { it.copy(showCredentialsDialog = false) }
    }

    fun consumeCreatedStaff() {
        _uiState.update { it.copy(createdStaff = null) }
    }
}
