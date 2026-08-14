package com.invictus.attendanceapp.feature.auth.domain.usecase

import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.auth.domain.model.User
import com.invictus.attendanceapp.feature.auth.domain.model.UserRole
import com.invictus.attendanceapp.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class LoginUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        authRepository = mock()
        loginUseCase = LoginUseCase(authRepository)
    }

    @Test
    fun login_validAdminCredentials_returnsSuccessUser() = runTest {
        val user = User(username = "admin", role = UserRole.ADMIN)
        whenever(authRepository.login("admin", "admin123", UserRole.ADMIN)).thenReturn(AppResult.Success(user))

        val result = loginUseCase("admin", "admin123", UserRole.ADMIN)

        assertTrue(result is AppResult.Success)
        assertEquals(user, (result as AppResult.Success).data)
    }

    @Test
    fun login_roleMismatch_returnsAccessDeniedError() = runTest {
        whenever(authRepository.login("staff1", "pass123", UserRole.ADMIN))
            .thenReturn(AppResult.Error(AppError.Custom("Access Denied: This account does not have Admin privileges.")))

        val result = loginUseCase("staff1", "pass123", UserRole.ADMIN)

        assertTrue(result is AppResult.Error)
        assertEquals("Access Denied: This account does not have Admin privileges.", (result as AppResult.Error).error.message)
    }

    @Test
    fun login_invalidCredentials_returnsError() = runTest {
        whenever(authRepository.login("admin", "wrongpass", null)).thenReturn(AppResult.Error(AppError.InvalidCredentials))

        val result = loginUseCase("admin", "wrongpass")

        assertTrue(result is AppResult.Error)
        assertEquals(AppError.InvalidCredentials, (result as AppResult.Error).error)
    }

    @Test
    fun login_blankInput_returnsInvalidCredentialsError() = runTest {
        val result = loginUseCase("", "")

        assertTrue(result is AppResult.Error)
        assertEquals(AppError.InvalidCredentials, (result as AppResult.Error).error)
    }
}
