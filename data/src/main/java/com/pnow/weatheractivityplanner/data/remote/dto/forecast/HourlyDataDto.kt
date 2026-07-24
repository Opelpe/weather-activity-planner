package com.pnow.weatheractivityplanner.data.remote.dto.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class HourlyDataDto(
    @param:Json(name = "time") val time: List<String>,
    @param:Json(name = "cloud_cover") val cloudCoverPercent: List<Int>,
    @param:Json(name = "is_day") val isDay: List<Int>,
    @param:Json(name = "wind_speed_10m") val windSpeedKph: List<Double>,
    @param:Json(name = "precipitation_probability") val precipitationProbabilityPercent: List<Int>,
    @param:Json(name = "wind_gusts_10m") val windGustsKph: List<Double>,
)
