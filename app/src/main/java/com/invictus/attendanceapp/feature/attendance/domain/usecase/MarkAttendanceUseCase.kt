package com.invictus.attendanceapp.feature.attendance.domain.usecase

import android.graphics.Bitmap
import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.core.face.FaceRecognitionConfig
import com.invictus.attendanceapp.core.face.FaceRecognitionManager
import com.invictus.attendanceapp.core.image.ImageStorageManager
import com.invictus.attendanceapp.core.location.LocationProvider
import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance
import com.invictus.attendanceapp.feature.attendance.domain.repository.AttendanceRepository
import com.invictus.attendanceapp.feature.staff.domain.repository.StaffRepository
import java.util.UUID
import javax.inject.Inject

class MarkAttendanceUseCase @Inject constructor(
    private val staffRepository: StaffRepository,
    private val attendanceRepository: AttendanceRepository,
    private val faceRecognitionManager: FaceRecognitionManager,
    private val locationProvider: LocationProvider,
    private val imageStorageManager: ImageStorageManager
) {
    suspend operator fun invoke(staffId: String, selfieBitmap: Bitmap): AppResult<Attendance> {
        val staff = staffRepository.getStaff(staffId)
            ?: return AppResult.Error(AppError.StaffNotFound)

        val enrolledEmbedding = staff.faceEmbedding
        if (enrolledEmbedding.isNullOrEmpty()) {
            return AppResult.Error(AppError.FaceNotEnrolled)
        }

        // 1. Detect face and generate embedding from selfie
        val capturedEmbeddingResult = faceRecognitionManager.generateEmbedding(selfieBitmap)
        if (capturedEmbeddingResult is AppResult.Error) {
            return capturedEmbeddingResult
        }

        val capturedEmbedding = (capturedEmbeddingResult as AppResult.Success).data

        // 2. Compare embeddings using Cosine Similarity
        val similarity = faceRecognitionManager.compare(capturedEmbedding, enrolledEmbedding)
        if (similarity < FaceRecognitionConfig.MATCH_THRESHOLD) {
            // STRICT GUARANTEE: Mismatch prevents recordAttendance from ever being called!
            return AppResult.Error(AppError.FaceMismatch)
        }

        // 3. Acquire location
        val location = locationProvider.getCurrentLocation()
            ?: return AppResult.Error(AppError.LocationUnavailable)

        // 4. Save selfie image to private app storage
        val selfiePath = imageStorageManager.saveBitmap(selfieBitmap, prefix = "attendance_${staff.employeeId}")

        // 5. Create Attendance record
        val attendance = Attendance(
            id = UUID.randomUUID().toString(),
            staffId = staffId,
            timestamp = System.currentTimeMillis(),
            selfiePath = selfiePath,
            latitude = location.latitude,
            longitude = location.longitude
        )

        return when (val recordResult = attendanceRepository.recordAttendance(attendance)) {
            is AppResult.Success -> AppResult.Success(attendance)
            is AppResult.Error -> recordResult
        }
    }
}
