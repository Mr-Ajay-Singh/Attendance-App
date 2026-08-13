package com.invictus.attendanceapp.feature.attendance.domain.repository

import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    suspend fun recordAttendance(attendance: Attendance): AppResult<Unit>
    fun getAttendanceForStaff(staffId: String): Flow<List<Attendance>>
}
