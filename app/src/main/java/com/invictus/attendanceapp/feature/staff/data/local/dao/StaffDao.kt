package com.invictus.attendanceapp.feature.staff.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.invictus.attendanceapp.feature.staff.data.local.entity.StaffEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StaffDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffList(staffList: List<StaffEntity>)

    @Query("DELETE FROM staff")
    suspend fun clearAllStaff()

    @Transaction
    suspend fun replaceAllStaff(staffList: List<StaffEntity>) {
        clearAllStaff()
        insertStaffList(staffList)
    }

    @Query("SELECT * FROM staff WHERE id = :id")
    suspend fun getStaffById(id: String): StaffEntity?

    @Query("SELECT * FROM staff WHERE employeeId = :employeeId")
    suspend fun getStaffByEmployeeId(employeeId: String): StaffEntity?

    @Query("SELECT * FROM staff ORDER BY name ASC")
    fun getAllStaff(): Flow<List<StaffEntity>>

    @Query("UPDATE staff SET faceEmbedding = :embedding, faceImagePath = :imagePath WHERE id = :staffId")
    suspend fun updateFaceEmbedding(staffId: String, embedding: List<Float>?, imagePath: String?)
}
