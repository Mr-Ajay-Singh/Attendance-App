package com.invictus.attendanceapp.feature.attendance.domain.usecase

import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance
import com.invictus.attendanceapp.feature.attendance.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAttendanceHistoryUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) {
    operator fun invoke(staffId: String): Flow<List<Attendance>> {
        return attendanceRepository.getAttendanceForStaff(staffId)
    }
}
