package com.invictus.attendanceapp.feature.staff.domain.usecase

import com.invictus.attendanceapp.feature.staff.domain.model.Staff
import com.invictus.attendanceapp.feature.staff.domain.repository.StaffRepository
import javax.inject.Inject

class GetStaffUseCase @Inject constructor(
    private val staffRepository: StaffRepository
) {
    suspend operator fun invoke(id: String): Staff? {
        return staffRepository.getStaff(id)
    }
}
