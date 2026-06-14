package com.pnow.weatheractivityplanner.data.remote.dto.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ForecastResponseDto(
    @param:Json(name = "latitude") val latitude: Double,
    @param:Json(name = "longitude") val longitude: Double,
    @param:Json(name = "timezone") val timezone: String,
    @param:Json(name = "current") val current: CurrentWeatherDto? = null,
    @param:Json(name = "daily") val daily: DailyDataDto,
)
