package com.invictus.attendanceapp.feature.staff.data.repository

import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.staff.data.local.dao.StaffDao
import com.invictus.attendanceapp.feature.staff.data.mapper.toDomain
import com.invictus.attendanceapp.feature.staff.data.mapper.toEntity
import com.invictus.attendanceapp.feature.staff.data.remote.StaffRemoteDataSource
import com.invictus.attendanceapp.feature.staff.data.remote.dto.CreateStaffRequest
import com.invictus.attendanceapp.feature.staff.data.remote.dto.EnrollFaceRequest
import com.invictus.attendanceapp.feature.staff.domain.model.Staff
import com.invictus.attendanceapp.feature.staff.domain.repository.StaffRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepositoryImpl @Inject constructor(
    private val staffDao: StaffDao,
    private val remoteDataSource: StaffRemoteDataSource
) : StaffRepository {

    override suspend fun addStaff(staff: Staff): AppResult<Staff> {
        return addStaff(staff, username = staff.employeeId.lowercase(), password = "password123")
    }

    override suspend fun addStaff(staff: Staff, username: String, password: String): AppResult<Staff> {
        val createRequest = CreateStaffRequest(
            name = staff.name,
            employeeId = staff.employeeId,
            username = username.ifBlank { staff.employeeId.lowercase() },
            password = password.ifBlank { "password123" },
            role = "STAFF"
        )
        return when (val remoteResult = remoteDataSource.createStaff(createRequest)) {
            is AppResult.Success -> {
                val createdStaff = remoteResult.data.toDomain()
                staffDao.insertStaff(createdStaff.toEntity())
                AppResult.Success(createdStaff)
            }
            is AppResult.Error -> {
                remoteResult
            }
        }
    }

    override suspend fun getStaff(id: String): Staff? {
        when (val remoteResult = remoteDataSource.getStaffById(id)) {
            is AppResult.Success -> {
                val staff = remoteResult.data.toDomain()
                staffDao.insertStaff(staff.toEntity())
                return staff
            }
            is AppResult.Error -> {
                return staffDao.getStaffById(id)?.toDomain()
            }
        }
    }

    override suspend fun getStaffByEmployeeId(employeeId: String): Staff? {
        return staffDao.getStaffByEmployeeId(employeeId)?.toDomain()
    }

    override fun getAllStaff(): Flow<List<Staff>> {
        return staffDao.getAllStaff().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshStaffList(): AppResult<Unit> {
        return when (val remoteResult = remoteDataSource.getAllStaff()) {
            is AppResult.Success -> {
                val remoteEntities = remoteResult.data.map { it.toDomain().toEntity() }
                staffDao.replaceAllStaff(remoteEntities)
                AppResult.Success(Unit)
            }
            is AppResult.Error -> remoteResult
        }
    }

    override suspend fun updateFace(staffId: String, embedding: List<Float>, imagePath: String): AppResult<Unit> {
        val request = EnrollFaceRequest(
            embedding = embedding,
            faceImageUrl = imagePath
        )
        return when (val remoteResult = remoteDataSource.enrollFace(staffId, request)) {
            is AppResult.Success -> {
                staffDao.updateFaceEmbedding(staffId, embedding, imagePath)
                AppResult.Success(Unit)
            }
            is AppResult.Error -> {
                // STRICT GUARANTEE: If face enrollment fails remotely, return the error directly.
                remoteResult
            }
        }
    }
}
