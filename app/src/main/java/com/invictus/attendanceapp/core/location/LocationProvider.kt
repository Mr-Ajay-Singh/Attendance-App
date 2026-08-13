package com.invictus.attendanceapp.core.location

data class LocationData(
    val latitude: Double,
    val longitude: Double
)

interface LocationProvider {
    suspend fun getCurrentLocation(): LocationData?
}
