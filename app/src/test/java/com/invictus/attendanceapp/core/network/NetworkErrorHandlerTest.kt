package com.invictus.attendanceapp.core.network

import com.invictus.attendanceapp.core.common.AppError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class NetworkErrorHandlerTest {

    private lateinit var errorHandler: NetworkErrorHandler

    @Before
    fun setUp() {
        errorHandler = NetworkErrorHandler()
    }

    @Test
    fun handleResponseError_genericException_returnsNetworkError() = runTest {
        val exception = Exception("Failed to connect")
        val error = errorHandler.handleResponseError(exception)
        assertEquals("Network error: Please check your internet connection.", error.message)
    }
}
