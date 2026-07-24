package com.pnow.weatheractivityplanner.domain.repository

import com.pnow.weatheractivityplanner.domain.model.Location

interface GeocodingRepository {

    suspend fun searchLocations(
        query: String,
        count: Int = 20,
    ): Result<List<Location>>
}
