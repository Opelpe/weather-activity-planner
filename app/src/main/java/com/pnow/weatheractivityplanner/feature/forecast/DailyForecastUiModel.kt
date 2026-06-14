package com.pnow.weatheractivityplanner.feature.forecast

import androidx.annotation.StringRes
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.feature.common.toDisplayNameRes

data class DailyForecastUiModel(
    val date: String,
    val maxTemperatureCelsius: Double,
    val minTemperatureCelsius: Double,
    @param:StringRes val conditionDisplayNameRes: Int,
)

internal fun DailyForecast.toUiModel(): DailyForecastUiModel = DailyForecastUiModel(
    date = date,
    conditionDisplayNameRes = condition.toDisplayNameRes(),
    maxTemperatureCelsius = maxTemperatureCelsius,
    minTemperatureCelsius = minTemperatureCelsius,
)
