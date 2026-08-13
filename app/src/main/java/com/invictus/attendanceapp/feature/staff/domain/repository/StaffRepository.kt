package com.invictus.attendanceapp.feature.staff.domain.repository

import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.staff.domain.model.Staff
import kotlinx.coroutines.flow.Flow

interface StaffRepository {
    suspend fun addStaff(staff: Staff): AppResult<Unit>
    suspend fun getStaff(id: String): Staff?
    suspend fun getStaffByEmployeeId(employeeId: String): Staff?
    fun getAllStaff(): Flow<List<Staff>>
    suspend fun updateFace(staffId: String, embedding: List<Float>, imagePath: String): AppResult<Unit>
}
