package com.pnow.weatheractivityplanner.domain.repository

import com.pnow.weatheractivityplanner.domain.model.Coordinates

interface CurrentLocationRepository {

    suspend fun getCurrentLocation(): Result<Coordinates>

    fun hasPermission(): Boolean
}
