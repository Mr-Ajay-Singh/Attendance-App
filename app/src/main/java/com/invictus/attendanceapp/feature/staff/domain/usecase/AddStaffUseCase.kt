package com.invictus.attendanceapp.feature.staff.domain.usecase

import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.staff.domain.model.Staff
import com.invictus.attendanceapp.feature.staff.domain.repository.StaffRepository
import javax.inject.Inject

class AddStaffUseCase @Inject constructor(
    private val staffRepository: StaffRepository
) {
    suspend operator fun invoke(
        name: String,
        employeeId: String,
        username: String? = null,
        password: String? = null
    ): AppResult<Staff> {
        val trimmedName = name.trim()
        val trimmedEmployeeId = employeeId.trim()
        val trimmedUsername = username?.trim()?.ifBlank { null } ?: trimmedEmployeeId.lowercase()
        val trimmedPassword = password?.trim()?.ifBlank { null } ?: "password123"

        if (trimmedName.isBlank()) {
            return AppResult.Error(AppError.Custom("Staff name is required"))
        }

        if (trimmedEmployeeId.isBlank()) {
            return AppResult.Error(AppError.Custom("Employee ID is required"))
        }

        val existingStaff = staffRepository.getStaffByEmployeeId(trimmedEmployeeId)
        if (existingStaff != null) {
            return AppResult.Error(AppError.DuplicateEmployeeId)
        }

        val staffToCreate = Staff(
            id = "",
            name = trimmedName,
            employeeId = trimmedEmployeeId,
            faceEnrolled = false,
            faceImageUrl = null,
            faceEmbedding = null,
            faceImagePath = null
        )

        return staffRepository.addStaff(staffToCreate, trimmedUsername, trimmedPassword)
    }
}
