package com.pnow.weatheractivityplanner.domain.repository

import com.pnow.weatheractivityplanner.domain.model.Forecast

interface WeatherRepository {

    suspend fun getForecast(
        latitude: Double,
        longitude: Double,
    ): Result<Forecast>
}
