package com.invictus.attendanceapp.feature.attendance.data.repository

import android.util.Base64
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.attendance.data.local.dao.AttendanceDao
import com.invictus.attendanceapp.feature.attendance.data.mapper.toDomain
import com.invictus.attendanceapp.feature.attendance.data.mapper.toEntity
import com.invictus.attendanceapp.feature.attendance.data.remote.AttendanceRemoteDataSource
import com.invictus.attendanceapp.feature.attendance.data.remote.dto.MarkAttendanceRequest
import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance
import com.invictus.attendanceapp.feature.attendance.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val remoteDataSource: AttendanceRemoteDataSource
) : AttendanceRepository {

    override suspend fun recordAttendance(
        embedding: List<Float>,
        selfiePath: String,
        latitude: Double,
        longitude: Double,
        timestamp: Long
    ): AppResult<Attendance> {
        val base64Selfie = encodeSelfieToBase64(selfiePath)

        val request = MarkAttendanceRequest(
            embedding = embedding,
            selfie = base64Selfie,
            latitude = latitude,
            longitude = longitude,
            timestamp = timestamp
        )

        return when (val remoteResult = remoteDataSource.markAttendance(request)) {
            is AppResult.Success -> {
                val remoteAttendance = remoteResult.data.toDomain().copy(selfiePath = selfiePath)
                attendanceDao.insertAttendance(remoteAttendance.toEntity())
                AppResult.Success(remoteAttendance)
            }
            is AppResult.Error -> {
                // STRICT GUARANTEE: If face mismatch or remote failure, DO NOT CREATE LOCAL RECORD
                remoteResult
            }
        }
    }

    override fun getAttendanceForStaff(staffId: String): Flow<List<Attendance>> {
        return attendanceDao.getAttendanceForStaff(staffId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshAttendanceHistory(staffId: String): AppResult<Unit> {
        return when (val remoteResult = remoteDataSource.getAttendanceHistory(staffId)) {
            is AppResult.Success -> {
                val entities = remoteResult.data.map { it.toEntity() }
                attendanceDao.insertAllAttendance(entities)
                AppResult.Success(Unit)
            }
            is AppResult.Error -> remoteResult
        }
    }

    private fun encodeSelfieToBase64(selfiePath: String): String {
        return try {
            val file = File(selfiePath)
            if (file.exists()) {
                val bytes = file.readBytes()
                "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else {
                selfiePath
            }
        } catch (e: Exception) {
            selfiePath
        }
    }
}
