package com.invictus.attendanceapp

import android.app.Application
import com.invictus.attendanceapp.feature.staff.domain.model.Staff
import com.invictus.attendanceapp.feature.staff.domain.repository.StaffRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class AttendanceApp : Application() {

    @Inject
    lateinit var staffRepository: StaffRepository

    override fun onCreate() {
        super.onCreate()
        seedInitialData()
    }

    private fun seedInitialData() {
        CoroutineScope(Dispatchers.IO).launch {
            val existing = staffRepository.getStaff("staff_default_001")
            if (existing == null) {
                staffRepository.addStaff(
                    Staff(
                        id = "staff_default_001",
                        name = "Demo Staff",
                        employeeId = "EMP001",
                        faceEmbedding = null,
                        faceImagePath = null
                    )
                )
            }
        }
    }
}
