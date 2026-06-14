package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.DailyForecast

private const val COMFORTABLE_MIN_CELSIUS = 10.0
private const val COMFORTABLE_MAX_CELSIUS = 28.0
private const val SIGNIFICANT_PRECIPITATION_THRESHOLD_MM = 0.5

internal val DailyForecast.isComfortableTemperature: Boolean
    get() = maxTemperatureCelsius in COMFORTABLE_MIN_CELSIUS..COMFORTABLE_MAX_CELSIUS

internal val DailyForecast.hasSignificantPrecipitation: Boolean
    get() = precipitationSumMm > SIGNIFICANT_PRECIPITATION_THRESHOLD_MM
