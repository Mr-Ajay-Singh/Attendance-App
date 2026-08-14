package com.invictus.attendanceapp.feature.auth.data.remote

import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.core.network.KtorClient
import com.invictus.attendanceapp.core.network.NetworkErrorHandler
import com.invictus.attendanceapp.feature.auth.data.remote.dto.CreateAdminRequest
import com.invictus.attendanceapp.feature.auth.data.remote.dto.LoginRequest
import com.invictus.attendanceapp.feature.auth.data.remote.dto.LoginResponse
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRemoteDataSource {
    suspend fun login(request: LoginRequest): AppResult<LoginResponse>
    suspend fun setupInitialAdmin(request: CreateAdminRequest): AppResult<LoginResponse>
}

@Singleton
class AuthRemoteDataSourceImpl @Inject constructor(
    private val ktorClient: KtorClient,
    private val errorHandler: NetworkErrorHandler
) : AuthRemoteDataSource {

    override suspend fun login(request: LoginRequest): AppResult<LoginResponse> {
        return try {
            val response: LoginResponse = ktorClient.client.post("${ktorClient.baseUrl}/api/attendance/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
            AppResult.Success(response)
        } catch (e: Exception) {
            AppResult.Error(errorHandler.handleResponseError(e))
        }
    }

    override suspend fun setupInitialAdmin(request: CreateAdminRequest): AppResult<LoginResponse> {
        return try {
            val response: LoginResponse = ktorClient.client.post("${ktorClient.baseUrl}/api/admin/setup") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
            AppResult.Success(response)
        } catch (e: Exception) {
            AppResult.Error(errorHandler.handleResponseError(e))
        }
    }
}
