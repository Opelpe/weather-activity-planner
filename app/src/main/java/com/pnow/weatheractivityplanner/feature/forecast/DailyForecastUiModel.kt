package com.pnow.weatheractivityplanner.feature.forecast

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.feature.common.toDisplayNameRes
import com.pnow.weatheractivityplanner.feature.common.toIconRes
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DailyForecastUiModel(
    val date: String,
    val displayDate: String,
    val isTomorrow: Boolean,
    val maxTemperatureCelsius: Double,
    val minTemperatureCelsius: Double,
    val precipitationProbabilityPercent: Int,
    @param:StringRes val conditionDisplayNameRes: Int,
    @param:DrawableRes val conditionIconRes: Int,
)

private const val DISPLAY_DATE_PATTERN = "EEE, MMM d"

internal fun DailyForecast.toUiModel(isTomorrow: Boolean = false): DailyForecastUiModel =
    DailyForecastUiModel(
        date = date,
        displayDate = LocalDate.parse(date)
            .format(DateTimeFormatter.ofPattern(DISPLAY_DATE_PATTERN, Locale.getDefault())),
        isTomorrow = isTomorrow,
        conditionDisplayNameRes = condition.toDisplayNameRes(),
        conditionIconRes = condition.toIconRes(),
        maxTemperatureCelsius = maxTemperatureCelsius,
        minTemperatureCelsius = minTemperatureCelsius,
        precipitationProbabilityPercent = precipitationProbabilityMaxPercent,
    )
