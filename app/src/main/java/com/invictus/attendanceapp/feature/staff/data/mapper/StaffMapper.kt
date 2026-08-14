package com.invictus.attendanceapp.feature.staff.data.mapper

import com.invictus.attendanceapp.feature.staff.data.local.entity.StaffEntity
import com.invictus.attendanceapp.feature.staff.data.remote.dto.StaffDto
import com.invictus.attendanceapp.feature.staff.domain.model.Staff

fun StaffEntity.toDomain(): Staff {
    return Staff(
        id = id,
        name = name,
        employeeId = employeeId,
        faceEnrolled = faceEnrolled,
        faceImageUrl = faceImageUrl,
        faceEmbedding = faceEmbedding,
        faceImagePath = faceImagePath
    )
}

fun Staff.toEntity(): StaffEntity {
    return StaffEntity(
        id = id,
        name = name,
        employeeId = employeeId,
        faceEnrolled = faceEnrolled,
        faceImageUrl = faceImageUrl,
        faceEmbedding = faceEmbedding,
        faceImagePath = faceImagePath
    )
}

fun StaffDto.toDomain(): Staff {
    return Staff(
        id = id,
        name = name,
        employeeId = employeeId,
        faceEnrolled = faceEnrolled,
        faceImageUrl = faceImageUrl,
        faceEmbedding = faceEmbedding,
        faceImagePath = null
    )
}

fun StaffDto.toEntity(): StaffEntity {
    return StaffEntity(
        id = id,
        name = name,
        employeeId = employeeId,
        faceEnrolled = faceEnrolled,
        faceImageUrl = faceImageUrl,
        faceEmbedding = faceEmbedding,
        faceImagePath = null
    )
}
