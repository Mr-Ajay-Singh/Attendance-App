package com.invictus.attendanceapp.feature.staff.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateStaffRequest(
    val name: String,
    val employeeId: String,
    val username: String? = null,
    val password: String? = null,
    val role: String = "STAFF"
)
