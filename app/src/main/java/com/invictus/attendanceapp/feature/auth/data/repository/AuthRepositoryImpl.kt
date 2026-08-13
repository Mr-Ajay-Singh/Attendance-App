package com.invictus.attendanceapp.feature.auth.data.repository

import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.auth.domain.model.User
import com.invictus.attendanceapp.feature.auth.domain.model.UserRole
import com.invictus.attendanceapp.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    override suspend fun login(username: String, password: String): AppResult<User> {
        val trimmedUsername = username.trim()
        val trimmedPassword = password.trim()

        return when {
            trimmedUsername == "admin" && trimmedPassword == "admin123" -> {
                AppResult.Success(User(username = "admin", role = UserRole.ADMIN))
            }
            trimmedUsername == "staff" && trimmedPassword == "staff123" -> {
                AppResult.Success(User(username = "staff", role = UserRole.STAFF, staffId = "staff_default_001"))
            }
            else -> {
                AppResult.Error(AppError.InvalidCredentials)
            }
        }
    }
}
