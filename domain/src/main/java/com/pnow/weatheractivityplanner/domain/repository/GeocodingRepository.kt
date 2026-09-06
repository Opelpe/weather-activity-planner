package com.pnow.weatheractivityplanner.domain.repository

import com.pnow.weatheractivityplanner.domain.model.Location

interface GeocodingRepository {

    suspend fun searchLocations(
        query: String,
        count: Int = DEFAULT_RESULT_LIMIT,
    ): Result<List<Location>>

    suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
    ): Result<Location>

    companion object {

        const val DEFAULT_RESULT_LIMIT = 20
    }
}
