package com.invictus.attendanceapp.feature.attendance.data.mapper

import com.invictus.attendanceapp.feature.attendance.data.local.entity.AttendanceEntity
import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance

fun AttendanceEntity.toDomain(): Attendance {
    return Attendance(
        id = id,
        staffId = staffId,
        timestamp = timestamp,
        selfiePath = selfiePath,
        latitude = latitude,
        longitude = longitude
    )
}

fun Attendance.toEntity(): AttendanceEntity {
    return AttendanceEntity(
        id = id,
        staffId = staffId,
        timestamp = timestamp,
        selfiePath = selfiePath,
        latitude = latitude,
        longitude = longitude
    )
}
