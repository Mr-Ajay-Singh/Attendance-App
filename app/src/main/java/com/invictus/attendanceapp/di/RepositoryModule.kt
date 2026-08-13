package com.invictus.attendanceapp.di

import com.invictus.attendanceapp.feature.attendance.data.repository.AttendanceRepositoryImpl
import com.invictus.attendanceapp.feature.attendance.domain.repository.AttendanceRepository
import com.invictus.attendanceapp.feature.auth.data.repository.AuthRepositoryImpl
import com.invictus.attendanceapp.feature.auth.domain.repository.AuthRepository
import com.invictus.attendanceapp.feature.staff.data.repository.StaffRepositoryImpl
import com.invictus.attendanceapp.feature.staff.domain.repository.StaffRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindStaffRepository(
        impl: StaffRepositoryImpl
    ): StaffRepository

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(
        impl: AttendanceRepositoryImpl
    ): AttendanceRepository
}
