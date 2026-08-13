package com.invictus.attendanceapp.feature.staff.domain.usecase

import android.graphics.Bitmap
import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.core.face.FaceRecognitionManager
import com.invictus.attendanceapp.core.image.ImageStorageManager
import com.invictus.attendanceapp.feature.staff.domain.repository.StaffRepository
import javax.inject.Inject

class EnrollFaceUseCase @Inject constructor(
    private val staffRepository: StaffRepository,
    private val faceRecognitionManager: FaceRecognitionManager,
    private val imageStorageManager: ImageStorageManager
) {
    suspend operator fun invoke(staffId: String, faceBitmap: Bitmap): AppResult<Unit> {
        val staff = staffRepository.getStaff(staffId) ?: return AppResult.Error(AppError.StaffNotFound)

        val embeddingResult = faceRecognitionManager.generateEmbedding(faceBitmap)
        if (embeddingResult is AppResult.Error) {
            return embeddingResult
        }

        val embedding = (embeddingResult as AppResult.Success).data

        val savedImagePath = imageStorageManager.saveBitmap(faceBitmap, prefix = "enrollment_${staff.employeeId}")

        return staffRepository.updateFace(
            staffId = staffId,
            embedding = embedding,
            imagePath = savedImagePath
        )
    }
}
