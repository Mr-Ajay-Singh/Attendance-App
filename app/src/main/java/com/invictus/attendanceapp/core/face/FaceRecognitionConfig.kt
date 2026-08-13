package com.invictus.attendanceapp.core.face

object FaceRecognitionConfig {
    const val MATCH_THRESHOLD = 0.70f
    const val MODEL_FILE_NAME = "mobilefacenet.tflite"
    const val INPUT_IMAGE_SIZE = 112
    const val EMBEDDING_DIM = 192
}
