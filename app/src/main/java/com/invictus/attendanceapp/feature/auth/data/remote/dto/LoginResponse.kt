package com.invictus.attendanceapp.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String = "",
    val username: String = "",
    val role: String? = null,
    val staffId: String? = null
)

@Serializable
data class LoginResponse(
    val token: String? = null,
    val role: String? = null,
    val user: UserDto? = null
)
