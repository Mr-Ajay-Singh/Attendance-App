package com.invictus.attendanceapp.feature.attendance.domain.usecase

import android.graphics.Bitmap
import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.core.face.FaceRecognitionManager
import com.invictus.attendanceapp.core.image.ImageStorageManager
import com.invictus.attendanceapp.core.location.LocationData
import com.invictus.attendanceapp.core.location.LocationProvider
import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance
import com.invictus.attendanceapp.feature.attendance.domain.repository.AttendanceRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class MarkAttendanceUseCaseTest {

    private lateinit var attendanceRepository: AttendanceRepository
    private lateinit var faceRecognitionManager: FaceRecognitionManager
    private lateinit var locationProvider: LocationProvider
    private lateinit var imageStorageManager: ImageStorageManager

    private lateinit var markAttendanceUseCase: MarkAttendanceUseCase

    @Before
    fun setUp() {
        attendanceRepository = mock()
        faceRecognitionManager = mock()
        locationProvider = mock()
        imageStorageManager = mock()

        markAttendanceUseCase = MarkAttendanceUseCase(
            attendanceRepository = attendanceRepository,
            faceRecognitionManager = faceRecognitionManager,
            locationProvider = locationProvider,
            imageStorageManager = imageStorageManager
        )
    }

    @Test
    fun markAttendance_noFaceDetected_returnsErrorWithoutNetworkCall() = runTest {
        val staffId = "staff_123"
        val selfieBitmap: Bitmap = mock()

        whenever(faceRecognitionManager.generateEmbedding(selfieBitmap))
            .thenReturn(AppResult.Error(AppError.FaceNotFound))

        val result = markAttendanceUseCase(staffId, selfieBitmap)

        assertTrue(result is AppResult.Error)
        assertEquals(AppError.FaceNotFound, (result as AppResult.Error).error)
        verify(attendanceRepository, never()).recordAttendance(any(), any(), any(), any(), any())
    }

    @Test
    fun markAttendance_serverFaceMismatch_returnsRemoteError() = runTest {
        val staffId = "staff_123"
        val capturedEmbedding = listOf(0.1f, 0.2f, 0.3f)
        val selfieBitmap: Bitmap = mock()
        val locationData = LocationData(latitude = 26.8467, longitude = 80.9462)

        whenever(faceRecognitionManager.generateEmbedding(selfieBitmap))
            .thenReturn(AppResult.Success(capturedEmbedding))
        whenever(locationProvider.getCurrentLocation()).thenReturn(locationData)
        whenever(imageStorageManager.saveBitmap(any(), any())).thenReturn("/storage/selfie.jpg")
        whenever(attendanceRepository.recordAttendance(eq(capturedEmbedding), eq("/storage/selfie.jpg"), eq(locationData.latitude), eq(locationData.longitude), any()))
            .thenReturn(AppResult.Error(AppError.FaceMismatch))

        val result = markAttendanceUseCase(staffId, selfieBitmap)

        assertTrue(result is AppResult.Error)
        assertEquals(AppError.FaceMismatch, (result as AppResult.Error).error)
    }

    @Test
    fun markAttendance_success_recordsAttendanceSuccessfully() = runTest {
        val staffId = "staff_123"
        val capturedEmbedding = listOf(0.1f, 0.2f, 0.3f)
        val selfieBitmap: Bitmap = mock()
        val locationData = LocationData(latitude = 26.8467, longitude = 80.9462)
        val expectedAttendance = Attendance(
            id = "6a7e7c305473c670e2ee5e56",
            staffId = staffId,
            timestamp = 1786674000000L,
            selfiePath = "/storage/selfie.jpg",
            latitude = locationData.latitude,
            longitude = locationData.longitude
        )

        whenever(faceRecognitionManager.generateEmbedding(selfieBitmap))
            .thenReturn(AppResult.Success(capturedEmbedding))
        whenever(locationProvider.getCurrentLocation()).thenReturn(locationData)
        whenever(imageStorageManager.saveBitmap(any(), any())).thenReturn("/storage/selfie.jpg")
        whenever(attendanceRepository.recordAttendance(eq(capturedEmbedding), eq("/storage/selfie.jpg"), eq(locationData.latitude), eq(locationData.longitude), any()))
            .thenReturn(AppResult.Success(expectedAttendance))

        val result = markAttendanceUseCase(staffId, selfieBitmap)

        assertTrue(result is AppResult.Success)
        val recorded = (result as AppResult.Success).data
        assertEquals(expectedAttendance.id, recorded.id)
        assertEquals(staffId, recorded.staffId)
        assertEquals(locationData.latitude, recorded.latitude, 0.0001)
        assertEquals(locationData.longitude, recorded.longitude, 0.0001)
    }
}
