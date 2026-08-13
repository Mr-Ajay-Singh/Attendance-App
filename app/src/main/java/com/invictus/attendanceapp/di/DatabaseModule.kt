package com.invictus.attendanceapp.di

import android.content.Context
import androidx.room.Room
import com.invictus.attendanceapp.core.database.AttendanceDatabase
import com.invictus.attendanceapp.feature.attendance.data.local.dao.AttendanceDao
import com.invictus.attendanceapp.feature.staff.data.local.dao.StaffDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAttendanceDatabase(
        @ApplicationContext context: Context
    ): AttendanceDatabase {
        return Room.databaseBuilder(
            context,
            AttendanceDatabase::class.java,
            "attendance_app.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideStaffDao(database: AttendanceDatabase): StaffDao {
        return database.staffDao()
    }

    @Provides
    fun provideAttendanceDao(database: AttendanceDatabase): AttendanceDao {
        return database.attendanceDao()
    }
}
