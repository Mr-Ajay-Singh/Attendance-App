package com.invictus.attendanceapp.core.network

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

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
        assertEquals("Something Went Wrong. Please try again later.", error.message)
    }
}
