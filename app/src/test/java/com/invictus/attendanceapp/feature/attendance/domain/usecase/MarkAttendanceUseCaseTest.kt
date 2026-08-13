package com.invictus.attendanceapp.feature.attendance.domain.usecase

import android.graphics.Bitmap
import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.core.face.FaceRecognitionManager
import com.invictus.attendanceapp.core.image.ImageStorageManager
import com.invictus.attendanceapp.core.location.LocationData
import com.invictus.attendanceapp.core.location.LocationProvider
import com.invictus.attendanceapp.feature.attendance.domain.repository.AttendanceRepository
import com.invictus.attendanceapp.feature.staff.domain.model.Staff
import com.invictus.attendanceapp.feature.staff.domain.repository.StaffRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class MarkAttendanceUseCaseTest {

    private lateinit var staffRepository: StaffRepository
    private lateinit var attendanceRepository: AttendanceRepository
    private lateinit var faceRecognitionManager: FaceRecognitionManager
    private lateinit var locationProvider: LocationProvider
    private lateinit var imageStorageManager: ImageStorageManager

    private lateinit var markAttendanceUseCase: MarkAttendanceUseCase

    @Before
    fun setUp() {
        staffRepository = mock()
        attendanceRepository = mock()
        faceRecognitionManager = mock()
        locationProvider = mock()
        imageStorageManager = mock()

        markAttendanceUseCase = MarkAttendanceUseCase(
            staffRepository = staffRepository,
            attendanceRepository = attendanceRepository,
            faceRecognitionManager = faceRecognitionManager,
            locationProvider = locationProvider,
            imageStorageManager = imageStorageManager
        )
    }

    @Test
    fun markAttendance_faceMismatch_doesNOTRecordAttendance() = runTest {
        val staffId = "staff_123"
        val enrolledEmbedding = listOf(1.0f, 0.0f)
        val capturedEmbedding = listOf(0.0f, 1.0f)

        val staff = Staff(
            id = staffId,
            name = "Test Staff",
            employeeId = "EMP100",
            faceEmbedding = enrolledEmbedding,
            faceImagePath = "/dummy/path.jpg"
        )
        val selfieBitmap: Bitmap = mock()

        whenever(staffRepository.getStaff(staffId)).thenReturn(staff)
        whenever(faceRecognitionManager.generateEmbedding(selfieBitmap)).thenReturn(AppResult.Success(capturedEmbedding))
        // Low similarity -> 0.1f (below MATCH_THRESHOLD 0.70f)
        whenever(faceRecognitionManager.compare(capturedEmbedding, enrolledEmbedding)).thenReturn(0.10f)

        val result = markAttendanceUseCase(staffId, selfieBitmap)

        // Assert that result is FaceMismatch error
        assertTrue(result is AppResult.Error)
        assertEquals(AppError.FaceMismatch, (result as AppResult.Error).error)

        // CRITICAL SPEC REQUIREMENT: recordAttendance MUST NOT BE CALLED ON MISMATCH!
        verify(attendanceRepository, never()).recordAttendance(any())
    }

    @Test
    fun markAttendance_faceMatch_recordsAttendanceSuccessfully() = runTest {
        val staffId = "staff_123"
        val enrolledEmbedding = listOf(0.8f, 0.6f)
        val capturedEmbedding = listOf(0.82f, 0.58f)

        val staff = Staff(
            id = staffId,
            name = "Test Staff",
            employeeId = "EMP100",
            faceEmbedding = enrolledEmbedding,
            faceImagePath = "/dummy/path.jpg"
        )
        val selfieBitmap: Bitmap = mock()
        val locationData = LocationData(latitude = 26.8467, longitude = 80.9462)

        whenever(staffRepository.getStaff(staffId)).thenReturn(staff)
        whenever(faceRecognitionManager.generateEmbedding(selfieBitmap)).thenReturn(AppResult.Success(capturedEmbedding))
        // High similarity -> 0.95f (above MATCH_THRESHOLD 0.70f)
        whenever(faceRecognitionManager.compare(capturedEmbedding, enrolledEmbedding)).thenReturn(0.95f)
        whenever(locationProvider.getCurrentLocation()).thenReturn(locationData)
        whenever(imageStorageManager.saveBitmap(any(), any())).thenReturn("/storage/selfie.jpg")
        whenever(attendanceRepository.recordAttendance(any())).thenReturn(AppResult.Success(Unit))

        val result = markAttendanceUseCase(staffId, selfieBitmap)

        assertTrue(result is AppResult.Success)
        val recorded = (result as AppResult.Success).data
        assertEquals(staffId, recorded.staffId)
        assertEquals(locationData.latitude, recorded.latitude, 0.0001)
        assertEquals(locationData.longitude, recorded.longitude, 0.0001)

        // Verify recordAttendance was called exactly once
        verify(attendanceRepository).recordAttendance(any())
    }
}
