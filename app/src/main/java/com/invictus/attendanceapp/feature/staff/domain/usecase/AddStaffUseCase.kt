package com.invictus.attendanceapp.feature.staff.domain.usecase

import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.staff.domain.model.Staff
import com.invictus.attendanceapp.feature.staff.domain.repository.StaffRepository
import java.util.UUID
import javax.inject.Inject

class AddStaffUseCase @Inject constructor(
    private val staffRepository: StaffRepository
) {
    suspend operator fun invoke(name: String, employeeId: String): AppResult<Staff> {
        val trimmedName = name.trim()
        val trimmedEmployeeId = employeeId.trim()

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

        val newStaff = Staff(
            id = UUID.randomUUID().toString(),
            name = trimmedName,
            employeeId = trimmedEmployeeId,
            faceEmbedding = null,
            faceImagePath = null
        )

        val addResult = staffRepository.addStaff(newStaff)
        return when (addResult) {
            is AppResult.Success -> AppResult.Success(newStaff)
            is AppResult.Error -> addResult
        }
    }
}
