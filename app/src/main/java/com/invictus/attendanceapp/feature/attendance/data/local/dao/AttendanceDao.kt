package com.invictus.attendanceapp.feature.attendance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.invictus.attendanceapp.feature.attendance.data.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAttendance(attendanceList: List<AttendanceEntity>)

    @Query("SELECT * FROM attendance WHERE staffId = :staffId ORDER BY timestamp DESC")
    fun getAttendanceForStaff(staffId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE staffId = :staffId ORDER BY timestamp DESC")
    suspend fun getAttendanceByStaffId(staffId: String): List<AttendanceEntity>
}
