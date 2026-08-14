package com.invictus.attendanceapp.feature.auth.domain.usecase

import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.auth.domain.model.User
import com.invictus.attendanceapp.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SetupInitialAdminUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        employeeId: String,
        username: String,
        password: String
    ): AppResult<User> {
        return repository.setupInitialAdmin(name, employeeId, username, password)
    }
}
