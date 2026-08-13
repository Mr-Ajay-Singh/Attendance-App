package com.invictus.attendanceapp.core.common

sealed class AppError : Exception() {
    data class Custom(override val message: String) : AppError()
    object InvalidCredentials : AppError() {
        override val message: String = "Invalid username or password"
    }
    object StaffNotFound : AppError() {
        override val message: String = "Staff member not found"
    }
    object DuplicateEmployeeId : AppError() {
        override val message: String = "Employee ID already exists"
    }
    object FaceNotFound : AppError() {
        override val message: String = "No face detected. Position your face clearly inside the frame."
    }
    object MultipleFacesDetected : AppError() {
        override val message: String = "Multiple faces detected. Only one person should be visible."
    }
    object FaceMismatch : AppError() {
        override val message: String = "Face does not match the enrolled staff member."
    }
    object FaceNotEnrolled : AppError() {
        override val message: String = "Staff member does not have an enrolled face yet."
    }
    object LocationUnavailable : AppError() {
        override val message: String = "Location unavailable. Ensure GPS/Location services are enabled."
    }
    object CameraPermissionDenied : AppError() {
        override val message: String = "Camera permission is required to perform face recognition."
    }
    object LocationPermissionDenied : AppError() {
        override val message: String = "Location permission is required to record attendance."
    }
    object ImageStorageFailed : AppError() {
        override val message: String = "Failed to save selfie image."
    }
}
