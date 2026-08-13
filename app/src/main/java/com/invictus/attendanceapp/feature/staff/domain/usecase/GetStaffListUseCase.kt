package com.invictus.attendanceapp.feature.staff.domain.usecase

import com.invictus.attendanceapp.feature.staff.domain.model.Staff
import com.invictus.attendanceapp.feature.staff.domain.repository.StaffRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStaffListUseCase @Inject constructor(
    private val staffRepository: StaffRepository
) {
    operator fun invoke(): Flow<List<Staff>> {
        return staffRepository.getAllStaff()
    }
}
