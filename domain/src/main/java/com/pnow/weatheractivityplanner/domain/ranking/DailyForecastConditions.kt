package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.DailyForecast

private const val COMFORTABLE_MIN_CELSIUS = 12.0
private const val COMFORTABLE_MAX_CELSIUS = 27.0
private const val COMFORTABLE_TOLERANCE_CELSIUS = 3.0
private const val SIGNIFICANT_PRECIPITATION_THRESHOLD_MM = 0.5
private const val HEAVY_PRECIPITATION_MM = 5.0

internal val DailyForecast.isComfortableTemperature: Boolean
    get() = maxTemperatureCelsius in COMFORTABLE_MIN_CELSIUS..COMFORTABLE_MAX_CELSIUS

internal val DailyForecast.comfortableTemperatureFraction: Float
    get() = minOf(
        fractionBetween(
            maxTemperatureCelsius,
            COMFORTABLE_MIN_CELSIUS - COMFORTABLE_TOLERANCE_CELSIUS,
            COMFORTABLE_MIN_CELSIUS,
        ),
        fractionBetween(
            maxTemperatureCelsius,
            COMFORTABLE_MAX_CELSIUS + COMFORTABLE_TOLERANCE_CELSIUS,
            COMFORTABLE_MAX_CELSIUS,
        ),
    )

internal val DailyForecast.hasSignificantPrecipitation: Boolean
    get() = precipitationSumMm > SIGNIFICANT_PRECIPITATION_THRESHOLD_MM

internal val DailyForecast.precipitationFraction: Float
    get() = fractionBetween(
        precipitationSumMm,
        SIGNIFICANT_PRECIPITATION_THRESHOLD_MM,
        HEAVY_PRECIPITATION_MM,
    )

