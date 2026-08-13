package com.invictus.attendanceapp.feature.attendance.data.repository

import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.attendance.data.local.dao.AttendanceDao
import com.invictus.attendanceapp.feature.attendance.data.mapper.toDomain
import com.invictus.attendanceapp.feature.attendance.data.mapper.toEntity
import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance
import com.invictus.attendanceapp.feature.attendance.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceDao: AttendanceDao
) : AttendanceRepository {

    override suspend fun recordAttendance(attendance: Attendance): AppResult<Unit> {
        return try {
            attendanceDao.insertAttendance(attendance.toEntity())
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(AppError.Custom(e.message ?: "Failed to record attendance"))
        }
    }

    override fun getAttendanceForStaff(staffId: String): Flow<List<Attendance>> {
        return attendanceDao.getAttendanceForStaff(staffId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
