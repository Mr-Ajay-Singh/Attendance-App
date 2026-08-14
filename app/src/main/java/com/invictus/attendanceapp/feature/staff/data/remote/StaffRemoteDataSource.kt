package com.invictus.attendanceapp.feature.staff.data.remote

import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.core.network.KtorClient
import com.invictus.attendanceapp.core.network.NetworkErrorHandler
import com.invictus.attendanceapp.feature.staff.data.remote.dto.CreateStaffRequest
import com.invictus.attendanceapp.feature.staff.data.remote.dto.EnrollFaceRequest
import com.invictus.attendanceapp.feature.staff.data.remote.dto.StaffDto
import com.invictus.attendanceapp.feature.staff.data.remote.dto.StaffListResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import javax.inject.Inject
import javax.inject.Singleton

interface StaffRemoteDataSource {
    suspend fun createStaff(request: CreateStaffRequest): AppResult<StaffDto>
    suspend fun getAllStaff(): AppResult<List<StaffDto>>
    suspend fun getStaffById(staffId: String): AppResult<StaffDto>
    suspend fun enrollFace(staffId: String, request: EnrollFaceRequest): AppResult<Unit>
}

@Singleton
class StaffRemoteDataSourceImpl @Inject constructor(
    private val ktorClient: KtorClient,
    private val errorHandler: NetworkErrorHandler
) : StaffRemoteDataSource {

    override suspend fun createStaff(request: CreateStaffRequest): AppResult<StaffDto> {
        return try {
            val response: StaffDto = ktorClient.client.post("${ktorClient.baseUrl}/api/staff") {
                setBody(request)
            }.body()
            AppResult.Success(response)
        } catch (e: Exception) {
            AppResult.Error(errorHandler.handleResponseError(e))
        }
    }

    override suspend fun getAllStaff(): AppResult<List<StaffDto>> {
        return try {
            val response: StaffListResponse = ktorClient.client.get("${ktorClient.baseUrl}/api/staff").body()
            AppResult.Success(response.data)
        } catch (e: Exception) {
            AppResult.Error(errorHandler.handleResponseError(e))
        }
    }

    override suspend fun getStaffById(staffId: String): AppResult<StaffDto> {
        return try {
            val response: StaffDto = ktorClient.client.get("${ktorClient.baseUrl}/api/staff/$staffId").body()
            AppResult.Success(response)
        } catch (e: Exception) {
            AppResult.Error(errorHandler.handleResponseError(e))
        }
    }

    override suspend fun enrollFace(staffId: String, request: EnrollFaceRequest): AppResult<Unit> {
        return try {
            ktorClient.client.put("${ktorClient.baseUrl}/api/staff/$staffId/face") {
                setBody(request)
            }
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(errorHandler.handleResponseError(e))
        }
    }
}
