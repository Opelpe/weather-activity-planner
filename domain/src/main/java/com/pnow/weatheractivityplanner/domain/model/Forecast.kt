package com.pnow.weatheractivityplanner.domain.model

data class Forecast(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: CurrentWeather,
    val daily: List<DailyForecast>,
)
