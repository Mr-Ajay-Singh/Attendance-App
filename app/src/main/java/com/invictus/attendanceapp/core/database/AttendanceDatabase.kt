package com.invictus.attendanceapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.invictus.attendanceapp.feature.attendance.data.local.dao.AttendanceDao
import com.invictus.attendanceapp.feature.attendance.data.local.entity.AttendanceEntity
import com.invictus.attendanceapp.feature.staff.data.local.EmbeddingConverters
import com.invictus.attendanceapp.feature.staff.data.local.dao.StaffDao
import com.invictus.attendanceapp.feature.staff.data.local.entity.StaffEntity

@Database(
    entities = [StaffEntity::class, AttendanceEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(EmbeddingConverters::class)
abstract class AttendanceDatabase : RoomDatabase() {
    abstract fun staffDao(): StaffDao
    abstract fun attendanceDao(): AttendanceDao
}
