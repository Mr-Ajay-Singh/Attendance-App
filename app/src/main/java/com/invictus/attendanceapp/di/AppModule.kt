package com.invictus.attendanceapp.di

import com.invictus.attendanceapp.core.face.FaceRecognitionManager
import com.invictus.attendanceapp.core.face.FaceRecognitionManagerImpl
import com.invictus.attendanceapp.core.location.LocationProvider
import com.invictus.attendanceapp.core.location.LocationProviderImpl
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
}
