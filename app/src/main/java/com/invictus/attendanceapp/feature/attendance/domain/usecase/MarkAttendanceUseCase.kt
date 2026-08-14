package com.invictus.attendanceapp.feature.attendance.domain.usecase

import android.graphics.Bitmap
import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.core.face.FaceRecognitionManager
import com.invictus.attendanceapp.core.image.ImageStorageManager
import com.invictus.attendanceapp.core.location.LocationProvider
import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance
import com.invictus.attendanceapp.feature.attendance.domain.repository.AttendanceRepository
import javax.inject.Inject

class MarkAttendanceUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val faceRecognitionManager: FaceRecognitionManager,
    private val locationProvider: LocationProvider,
    private val imageStorageManager: ImageStorageManager
) {
    suspend operator fun invoke(staffId: String, selfieBitmap: Bitmap): AppResult<Attendance> {
        // 1. Detect face and generate 192-dim embedding vector from captured selfie
        val capturedEmbeddingResult = faceRecognitionManager.generateEmbedding(selfieBitmap)
        if (capturedEmbeddingResult is AppResult.Error) {
            return capturedEmbeddingResult
        }
        val capturedEmbedding = (capturedEmbeddingResult as AppResult.Success).data

        // 2. Acquire current GPS location coordinates
        val location = locationProvider.getCurrentLocation()
            ?: return AppResult.Error(AppError.LocationUnavailable)

        // 3. Save local selfie preview file
        val selfiePath = imageStorageManager.saveBitmap(selfieBitmap, prefix = "attendance_$staffId")

        // 4. Submit to Backend (Server verifies face matching against enrolled database profile)
        return attendanceRepository.recordAttendance(
            embedding = capturedEmbedding,
            selfiePath = selfiePath,
            latitude = location.latitude,
            longitude = location.longitude
        )
    }
}
