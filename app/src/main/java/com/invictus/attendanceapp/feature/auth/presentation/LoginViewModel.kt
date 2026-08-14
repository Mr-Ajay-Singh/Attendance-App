package com.invictus.attendanceapp.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.auth.domain.model.UserRole
import com.invictus.attendanceapp.feature.auth.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(usernameInput = username, error = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(passwordInput = password, error = null) }
    }

    fun login(expectedRole: UserRole? = null) {
        val currentState = _uiState.value
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = loginUseCase(currentState.usernameInput, currentState.passwordInput, expectedRole)
            when (result) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, loggedInUser = result.data) }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.error.message) }
                }
            }
        }
    }

    fun consumeLoggedInUser() {
        _uiState.update { it.copy(loggedInUser = null) }
    }
}
