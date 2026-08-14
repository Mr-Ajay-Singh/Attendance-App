package com.invictus.attendanceapp.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateAdminRequest(
    val name: String,
    val employeeId: String,
    val username: String,
    val password: String
)
