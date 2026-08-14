package com.invictus.attendanceapp.feature.auth.presentation.setupadmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.auth.domain.model.User
import com.invictus.attendanceapp.feature.auth.domain.usecase.SetupInitialAdminUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SetupAdminUiState(
    val nameInput: String = "",
    val employeeIdInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val createdUser: User? = null
)

@HiltViewModel
class SetupAdminViewModel @Inject constructor(
    private val setupInitialAdminUseCase: SetupInitialAdminUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupAdminUiState())
    val uiState: StateFlow<SetupAdminUiState> = _uiState.asStateFlow()

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(nameInput = value, error = null) }
    }

    fun onEmployeeIdChanged(value: String) {
        _uiState.update { it.copy(employeeIdInput = value, error = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(passwordInput = value, error = null) }
    }

    fun createAdmin() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val employeeId = state.employeeIdInput.trim()
            val result = setupInitialAdminUseCase(
                name = state.nameInput.trim(),
                employeeId = employeeId,
                username = employeeId,
                password = state.passwordInput
            )
            when (result) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, createdUser = result.data) }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.error.message) }
                }
            }
        }
    }

    fun consumeCreatedUser() {
        _uiState.update { it.copy(createdUser = null) }
    }
}
