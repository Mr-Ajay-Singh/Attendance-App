package com.invictus.attendanceapp.feature.auth.presentation

import com.invictus.attendanceapp.feature.auth.domain.model.User

data class LoginUiState(
    val usernameInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedInUser: User? = null
)
