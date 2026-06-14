package com.pnow.weatheractivityplanner.domain.model

data class CurrentWeather(
    val temperatureCelsius: Double,
    val apparentTemperatureCelsius: Double,
    val relativeHumidityPercent: Int,
    val precipitationMm: Double,
    val windSpeedKph: Double,
    val condition: WeatherCondition,
    val isDay: Boolean,
)
