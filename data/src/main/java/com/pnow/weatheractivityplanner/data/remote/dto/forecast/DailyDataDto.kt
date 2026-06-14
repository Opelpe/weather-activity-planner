package com.pnow.weatheractivityplanner.data.remote.dto.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class DailyDataDto(
    @param:Json(name = "time") val time: List<String>,
    @param:Json(name = "weather_code") val weatherCode: List<Int>,
    @param:Json(name = "temperature_2m_max") val maxTemperatureCelsius: List<Double>,
    @param:Json(name = "temperature_2m_min") val minTemperatureCelsius: List<Double>,
    @param:Json(name = "precipitation_sum") val precipitationSumMm: List<Double>,
    @param:Json(name = "precipitation_probability_max") val precipitationProbabilityMaxPercent: List<Int>,
    @param:Json(name = "snowfall_sum") val snowfallSumCm: List<Double>,
    @param:Json(name = "wind_speed_10m_max") val windSpeedMaxKph: List<Double>,
    @param:Json(name = "wind_gusts_10m_max") val windGustsMaxKph: List<Double>,
    @param:Json(name = "uv_index_max") val uvIndexMax: List<Double>,
    @param:Json(name = "daylight_duration") val daylightDurationSeconds: List<Double>,
)
