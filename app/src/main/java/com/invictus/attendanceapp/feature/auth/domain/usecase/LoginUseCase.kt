package com.invictus.attendanceapp.feature.auth.domain.usecase

import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.auth.domain.model.User
import com.invictus.attendanceapp.feature.auth.domain.model.UserRole
import com.invictus.attendanceapp.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        username: String,
        password: String,
        expectedRole: UserRole? = null
    ): AppResult<User> {
        if (username.isBlank() || password.isBlank()) {
            return AppResult.Error(AppError.InvalidCredentials)
        }
        return authRepository.login(username, password, expectedRole)
    }
}
