package com.invictus.attendanceapp.feature.auth.domain.usecase

import com.invictus.attendanceapp.core.database.AttendanceDatabase
import com.invictus.attendanceapp.core.network.AuthTokenProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val tokenProvider: AuthTokenProvider,
    private val database: AttendanceDatabase
) {
    suspend operator fun invoke() {
        withContext(Dispatchers.IO) {
            tokenProvider.clearSession()
            database.clearAllTables()
        }
    }
}
