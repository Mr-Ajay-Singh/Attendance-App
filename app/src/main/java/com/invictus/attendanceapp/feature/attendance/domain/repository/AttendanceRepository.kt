package com.invictus.attendanceapp.feature.attendance.domain.repository

import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    suspend fun recordAttendance(
        embedding: List<Float>,
        selfiePath: String,
        latitude: Double,
        longitude: Double,
        timestamp: Long = System.currentTimeMillis()
    ): AppResult<Attendance>

    fun getAttendanceForStaff(staffId: String): Flow<List<Attendance>>
    suspend fun refreshAttendanceHistory(staffId: String): AppResult<Unit>
}
