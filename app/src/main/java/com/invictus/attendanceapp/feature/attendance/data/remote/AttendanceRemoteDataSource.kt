package com.invictus.attendanceapp.feature.attendance.data.remote

import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.core.network.KtorClient
import com.invictus.attendanceapp.core.network.NetworkErrorHandler
import com.invictus.attendanceapp.feature.attendance.data.remote.dto.AttendanceDto
import com.invictus.attendanceapp.feature.attendance.data.remote.dto.AttendanceHistoryResponse
import com.invictus.attendanceapp.feature.attendance.data.remote.dto.MarkAttendanceRequest
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import javax.inject.Inject
import javax.inject.Singleton

interface AttendanceRemoteDataSource {
    suspend fun markAttendance(request: MarkAttendanceRequest): AppResult<AttendanceDto>
    suspend fun getAttendanceHistory(staffId: String): AppResult<List<AttendanceDto>>
}

@Singleton
class AttendanceRemoteDataSourceImpl @Inject constructor(
    private val ktorClient: KtorClient,
    private val errorHandler: NetworkErrorHandler
) : AttendanceRemoteDataSource {

    override suspend fun markAttendance(request: MarkAttendanceRequest): AppResult<AttendanceDto> {
        return try {
            val response: AttendanceDto = ktorClient.client.post("${ktorClient.baseUrl}/api/attendance") {
                setBody(request)
            }.body()
            AppResult.Success(response)
        } catch (e: Exception) {
            AppResult.Error(errorHandler.handleResponseError(e))
        }
    }

    override suspend fun getAttendanceHistory(staffId: String): AppResult<List<AttendanceDto>> {
        return try {
            val response: AttendanceHistoryResponse = ktorClient.client.get("${ktorClient.baseUrl}/api/staff/$staffId/attendance").body()
            AppResult.Success(response.data)
        } catch (e: Exception) {
            AppResult.Error(errorHandler.handleResponseError(e))
        }
    }
}
