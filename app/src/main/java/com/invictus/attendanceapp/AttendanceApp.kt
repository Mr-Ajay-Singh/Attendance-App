package com.invictus.attendanceapp

import android.app.Application
import android.util.Log
import com.google.android.gms.tflite.java.TfLite
import dagger.hilt.android.HiltAndroidApp
import org.maplibre.android.MapLibre

@HiltAndroidApp
class AttendanceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MapLibre.getInstance(this)
        try {
            TfLite.initialize(this).addOnSuccessListener {
                Log.d("AttendanceApp", "TfLite Play Services initialized successfully")
            }.addOnFailureListener { e ->
                Log.e("AttendanceApp", "Failed to initialize TfLite Play Services", e)
            }
        } catch (e: Exception) {
            Log.e("AttendanceApp", "Error triggering TfLite initialization", e)
        }
    }
}
