package com.pnow.weatheractivityplanner.data.remote.dto.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentWeatherDto(
    @param:Json(name = "time") val time: String,
    @param:Json(name = "temperature_2m") val temperatureCelsius: Double,
    @param:Json(name = "relative_humidity_2m") val relativeHumidityPercent: Int,
    @param:Json(name = "apparent_temperature") val apparentTemperatureCelsius: Double,
    @param:Json(name = "precipitation") val precipitation: Double,
    @param:Json(name = "weather_code") val weatherCode: Int,
    @param:Json(name = "wind_speed_10m") val windSpeedKph: Double,
    @param:Json(name = "is_day") val isDay: Int,
)
