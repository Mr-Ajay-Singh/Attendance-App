package com.invictus.attendanceapp.di

import com.invictus.attendanceapp.core.face.FaceRecognitionManager
import com.invictus.attendanceapp.core.face.FaceRecognitionManagerImpl
import com.invictus.attendanceapp.core.location.LocationProvider
import com.invictus.attendanceapp.core.location.LocationProviderImpl
import com.invictus.attendanceapp.core.network.AuthTokenProvider
import com.invictus.attendanceapp.core.network.AuthTokenProviderImpl
import com.invictus.attendanceapp.feature.attendance.data.remote.AttendanceRemoteDataSource
import com.invictus.attendanceapp.feature.attendance.data.remote.AttendanceRemoteDataSourceImpl
import com.invictus.attendanceapp.feature.auth.data.remote.AuthRemoteDataSource
import com.invictus.attendanceapp.feature.auth.data.remote.AuthRemoteDataSourceImpl
import com.invictus.attendanceapp.feature.staff.data.remote.StaffRemoteDataSource
import com.invictus.attendanceapp.feature.staff.data.remote.StaffRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindFaceRecognitionManager(
        impl: FaceRecognitionManagerImpl
    ): FaceRecognitionManager

    @Binds
    @Singleton
    abstract fun bindLocationProvider(
        impl: LocationProviderImpl
    ): LocationProvider

    @Binds
    @Singleton
    abstract fun bindAuthTokenProvider(
        impl: AuthTokenProviderImpl
    ): AuthTokenProvider

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        impl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindStaffRemoteDataSource(
        impl: StaffRemoteDataSourceImpl
    ): StaffRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAttendanceRemoteDataSource(
        impl: AttendanceRemoteDataSourceImpl
    ): AttendanceRemoteDataSource
}
