package com.pnow.weatheractivityplanner.domain.model

data class DailyForecast(
    val date: String,
    val maxTemperatureCelsius: Double,
    val minTemperatureCelsius: Double,
    val precipitationSumMm: Double,
    val precipitationProbabilityMaxPercent: Int,
    val snowfallSumCm: Double,
    val windSpeedMaxKph: Double,
    val windGustsMaxKph: Double,
    val uvIndexMax: Double,
    val daylightDurationHours: Double,
    val condition: WeatherCondition,
)
