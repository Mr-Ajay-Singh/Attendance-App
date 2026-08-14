package com.invictus.attendanceapp.feature.attendance.data.repository

import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.attendance.data.local.dao.AttendanceDao
import com.invictus.attendanceapp.feature.attendance.data.mapper.toDomain
import com.invictus.attendanceapp.feature.attendance.data.mapper.toEntity
import com.invictus.attendanceapp.feature.attendance.data.remote.AttendanceRemoteDataSource
import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance
import com.invictus.attendanceapp.feature.attendance.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
        return when (val remoteResult = remoteDataSource.markAttendanceMultipart(
            embedding = embedding,
            selfiePath = selfiePath,
            latitude = latitude,
            longitude = longitude,
            timestamp = timestamp
        )) {
            is AppResult.Success -> {
                val remoteAttendance = remoteResult.data.toDomain().copy(selfiePath = selfiePath)
                attendanceDao.insertAttendance(remoteAttendance.toEntity())
                AppResult.Success(remoteAttendance)
            }
            is AppResult.Error -> {
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
}
