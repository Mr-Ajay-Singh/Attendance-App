package com.invictus.attendanceapp.feature.staff.data.repository

import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.staff.data.local.dao.StaffDao
import com.invictus.attendanceapp.feature.staff.data.mapper.toDomain
import com.invictus.attendanceapp.feature.staff.data.mapper.toEntity
import com.invictus.attendanceapp.feature.staff.domain.model.Staff
import com.invictus.attendanceapp.feature.staff.domain.repository.StaffRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepositoryImpl @Inject constructor(
    private val staffDao: StaffDao
) : StaffRepository {

    override suspend fun addStaff(staff: Staff): AppResult<Unit> {
        return try {
            staffDao.insertStaff(staff.toEntity())
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(AppError.Custom(e.message ?: "Failed to add staff"))
        }
    }

    override suspend fun getStaff(id: String): Staff? {
        return staffDao.getStaffById(id)?.toDomain()
    }

    override suspend fun getStaffByEmployeeId(employeeId: String): Staff? {
        return staffDao.getStaffByEmployeeId(employeeId)?.toDomain()
    }

    override fun getAllStaff(): Flow<List<Staff>> {
        return staffDao.getAllStaff().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateFace(staffId: String, embedding: List<Float>, imagePath: String): AppResult<Unit> {
        return try {
            staffDao.updateFaceEmbedding(staffId, embedding, imagePath)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(AppError.Custom(e.message ?: "Failed to update face enrollment"))
        }
    }
}
