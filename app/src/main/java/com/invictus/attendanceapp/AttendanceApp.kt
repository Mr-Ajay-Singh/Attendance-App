package com.invictus.attendanceapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AttendanceApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
