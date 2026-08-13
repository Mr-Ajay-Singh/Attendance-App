package com.invictus.attendanceapp.feature.staff.data.mapper

import com.invictus.attendanceapp.feature.staff.data.local.entity.StaffEntity
import com.invictus.attendanceapp.feature.staff.domain.model.Staff

fun StaffEntity.toDomain(): Staff {
    return Staff(
        id = id,
        name = name,
        employeeId = employeeId,
        faceEmbedding = faceEmbedding,
        faceImagePath = faceImagePath
    )
}

fun Staff.toEntity(): StaffEntity {
    return StaffEntity(
        id = id,
        name = name,
        employeeId = employeeId,
        faceEmbedding = faceEmbedding,
        faceImagePath = faceImagePath
    )
}
