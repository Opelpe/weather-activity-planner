package com.pnow.weatheractivityplanner.domain.repository

import com.pnow.weatheractivityplanner.domain.model.Forecast

interface WeatherRepository {

    suspend fun getForecast(
        locationId: Long,
        latitude: Double,
        longitude: Double,
        forceRefresh: Boolean = false,
    ): Result<Forecast>
}
