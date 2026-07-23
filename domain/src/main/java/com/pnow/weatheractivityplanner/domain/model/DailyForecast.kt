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
    val nightCloudCoverPercent: Double,
    val dawnDuskWindSpeedKph: Double,
    val dawnDuskPrecipitationProbabilityPercent: Double,
    val daytimeWindSpeedMaxKph: Double,
    val daytimeWindGustsMaxKph: Double,
    val condition: WeatherCondition,
)
