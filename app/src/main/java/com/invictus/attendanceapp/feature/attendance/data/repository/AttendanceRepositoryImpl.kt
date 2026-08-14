package com.invictus.attendanceapp.feature.attendance.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.invictus.attendanceapp.core.common.AppError
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
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val remoteDataSource: AttendanceRemoteDataSource
) : AttendanceRepository {

    override suspend fun recordAttendance(attendance: Attendance): AppResult<Unit> {
        val base64Selfie = encodeSelfieToBase64(attendance.selfiePath)

        val request = MarkAttendanceRequest(
            embedding = emptyList(), // Backend handles embedding or receives vector
            selfie = base64Selfie,
            latitude = attendance.latitude,
            longitude = attendance.longitude,
            timestamp = attendance.timestamp
        )

        return when (val remoteResult = remoteDataSource.markAttendance(request)) {
            is AppResult.Success -> {
                val remoteAttendance = remoteResult.data.toDomain()
                attendanceDao.insertAttendance(remoteAttendance.toEntity())
                AppResult.Success(Unit)
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
