package com.invictus.attendanceapp.feature.auth.domain.model

enum class UserRole {
    ADMIN,
    STAFF
}

data class User(
    val username: String,
    val role: UserRole,
    val staffId: String? = null
)
