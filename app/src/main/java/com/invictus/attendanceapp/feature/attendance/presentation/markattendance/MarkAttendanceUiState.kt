package com.invictus.attendanceapp.feature.attendance.presentation.markattendance

import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance
import com.invictus.attendanceapp.feature.staff.domain.model.Staff

data class MarkAttendanceUiState(
    val staff: Staff? = null,
    val isLoadingStaff: Boolean = false,
    val isCameraOpen: Boolean = false,
    val isProcessing: Boolean = false,
    val recordedAttendance: Attendance? = null,
    val todayAttendanceList: List<Attendance> = emptyList(),
    val error: String? = null
) {
    val isMarkedToday: Boolean
        get() = todayAttendanceList.isNotEmpty()

    val latestTodayAttendance: Attendance?
        get() = todayAttendanceList.maxByOrNull { it.timestamp }

    val todayPunchCount: Int
        get() = todayAttendanceList.size
}
