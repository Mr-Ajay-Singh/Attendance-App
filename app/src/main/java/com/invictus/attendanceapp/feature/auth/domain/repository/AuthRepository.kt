package com.invictus.attendanceapp.feature.auth.domain.repository

import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.auth.domain.model.User
import com.invictus.attendanceapp.feature.auth.domain.model.UserRole

interface AuthRepository {
    suspend fun login(username: String, password: String, expectedRole: UserRole? = null): AppResult<User>
    suspend fun setupInitialAdmin(name: String, employeeId: String, username: String, password: String): AppResult<User>
}
