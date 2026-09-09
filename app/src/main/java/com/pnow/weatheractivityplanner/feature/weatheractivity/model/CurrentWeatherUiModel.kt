package com.pnow.weatheractivityplanner.feature.weatheractivity.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.pnow.weatheractivityplanner.domain.model.CurrentWeather
import com.pnow.weatheractivityplanner.feature.common.toDisplayNameRes
import com.pnow.weatheractivityplanner.feature.common.toIconRes

data class CurrentWeatherUiModel(
    val temperatureCelsius: Double,
    val apparentTemperatureCelsius: Double,
    val humidityPercent: Int,
    val windSpeedKph: Double,
    @param:StringRes val conditionDisplayNameRes: Int,
    @param:DrawableRes val conditionIconRes: Int,
)

internal fun CurrentWeather.toUiModel(): CurrentWeatherUiModel = CurrentWeatherUiModel(
    temperatureCelsius = temperatureCelsius,
    apparentTemperatureCelsius = apparentTemperatureCelsius,
    conditionDisplayNameRes = condition.toDisplayNameRes(),
    conditionIconRes = condition.toIconRes(),
    humidityPercent = relativeHumidityPercent,
    windSpeedKph = windSpeedKph,
)
