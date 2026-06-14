package com.pnow.weatheractivityplanner.feature.weatheractivity.model

import androidx.annotation.StringRes
import com.pnow.weatheractivityplanner.domain.model.CurrentWeather
import com.pnow.weatheractivityplanner.feature.common.toDisplayNameRes

data class CurrentWeatherUiModel(
    val temperatureCelsius: Double,
    val apparentTemperatureCelsius: Double,
    val humidityPercent: Int,
    val windSpeedKph: Double,
    @param:StringRes val conditionDisplayNameRes: Int,
)

internal fun CurrentWeather.toUiModel(): CurrentWeatherUiModel = CurrentWeatherUiModel(
    temperatureCelsius = temperatureCelsius,
    apparentTemperatureCelsius = apparentTemperatureCelsius,
    conditionDisplayNameRes = condition.toDisplayNameRes(),
    humidityPercent = relativeHumidityPercent,
    windSpeedKph = windSpeedKph,
)
