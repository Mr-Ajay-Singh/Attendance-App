package com.invictus.attendanceapp.feature.auth.data.repository

import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.core.network.AuthTokenProvider
import com.invictus.attendanceapp.feature.auth.data.remote.AuthRemoteDataSource
import com.invictus.attendanceapp.feature.auth.data.remote.dto.CreateAdminRequest
import com.invictus.attendanceapp.feature.auth.data.remote.dto.LoginRequest
import com.invictus.attendanceapp.feature.auth.domain.model.User
import com.invictus.attendanceapp.feature.auth.domain.model.UserRole
import com.invictus.attendanceapp.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val tokenProvider: AuthTokenProvider,
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun login(username: String, password: String): AppResult<User> {
        val trimmedUsername = username.trim()
        val trimmedPassword = password.trim()

        if (trimmedUsername.isBlank() || trimmedPassword.isBlank()) {
            return AppResult.Error(AppError.InvalidCredentials)
        }

        val request = LoginRequest(username = trimmedUsername, password = trimmedPassword)

        return when (val remoteResult = remoteDataSource.login(request)) {
            is AppResult.Success -> {
                val loginResponse = remoteResult.data
                val token = loginResponse.token
                if (token.isNullOrBlank()) {
                    return AppResult.Error(AppError.InvalidCredentials)
                }
                tokenProvider.saveToken(token)

                val userDto = loginResponse.user
                val rawRole = loginResponse.role ?: userDto?.role ?: ""
                val role = if (rawRole.equals("ADMIN", ignoreCase = true) || trimmedUsername.contains("admin", ignoreCase = true)) {
                    UserRole.ADMIN
                } else {
                    UserRole.STAFF
                }

                val staffId = userDto?.staffId ?: userDto?.id ?: ""
                tokenProvider.saveUserRole(role)
                if (staffId.isNotBlank()) {
                    tokenProvider.saveStaffId(staffId)
                }

                val user = User(
                    username = userDto?.username ?: trimmedUsername,
                    role = role,
                    staffId = staffId.ifBlank { null }
                )
                AppResult.Success(user)
            }
            is AppResult.Error -> remoteResult
        }
    }

    override suspend fun setupInitialAdmin(
        name: String,
        employeeId: String,
        username: String,
        password: String
    ): AppResult<User> {
        val trimmedName = name.trim()
        val trimmedEmpId = employeeId.trim()
        val trimmedUsername = username.trim()
        val trimmedPassword = password.trim()

        if (trimmedName.isBlank() || trimmedEmpId.isBlank() || trimmedUsername.isBlank() || trimmedPassword.isBlank()) {
            return AppResult.Error(AppError.Custom("All fields (Name, Employee ID, Username, Password) are required"))
        }

        val request = CreateAdminRequest(
            name = trimmedName,
            employeeId = trimmedEmpId,
            username = trimmedUsername,
            password = trimmedPassword
        )

        return when (val remoteResult = remoteDataSource.setupInitialAdmin(request)) {
            is AppResult.Success -> {
                // Perform login immediately with created admin credentials
                login(trimmedUsername, trimmedPassword)
            }
            is AppResult.Error -> remoteResult
        }
    }
}
