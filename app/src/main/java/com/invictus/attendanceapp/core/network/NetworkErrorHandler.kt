package com.invictus.attendanceapp.core.network

import com.invictus.attendanceapp.core.common.AppError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkErrorHandler @Inject constructor() {

    suspend fun handleResponseError(exception: Exception): AppError {
        return when (exception) {
            is ClientRequestException -> {
                val statusCode = exception.response.status
                val errorBody = try {
                    exception.response.bodyAsText()
                } catch (e: Exception) {
                    ""
                }

                when {
                    statusCode == HttpStatusCode.Unauthorized -> AppError.InvalidCredentials
                    statusCode == HttpStatusCode.Forbidden || errorBody.contains("FACE_MISMATCH", ignoreCase = true) -> {
                        AppError.FaceMismatch
                    }
                    errorBody.contains("FACE_NOT_ENROLLED", ignoreCase = true) -> AppError.FaceNotEnrolled
                    errorBody.contains("INVALID_FACE_DATA", ignoreCase = true) -> AppError.Custom("Invalid face data")
                    statusCode == HttpStatusCode.NotFound -> AppError.StaffNotFound
                    statusCode == HttpStatusCode.Conflict -> AppError.DuplicateEmployeeId
                    else -> AppError.Custom("Request failed: ${statusCode.value}")
                }
            }
            is ServerResponseException -> AppError.Custom("Server error (${exception.response.status.value}). Please try again later.")
            is RedirectResponseException -> AppError.Custom("Unexpected redirect response.")
            else -> AppError.Custom("Network error: Please check your internet connection.")
        }
    }
}
