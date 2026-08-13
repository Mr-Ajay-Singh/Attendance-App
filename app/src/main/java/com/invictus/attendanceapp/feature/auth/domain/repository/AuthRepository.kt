package com.invictus.attendanceapp.feature.auth.domain.repository

import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.auth.domain.model.User

interface AuthRepository {
    suspend fun login(username: String, password: String): AppResult<User>
}
