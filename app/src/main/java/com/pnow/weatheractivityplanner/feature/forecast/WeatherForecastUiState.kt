package com.pnow.weatheractivityplanner.feature.forecast

import com.pnow.weatheractivityplanner.feature.common.UiError

data class WeatherForecastUiState(
    val locationName: String = "",
    val locationCountry: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val dailyForecast: List<DailyForecastUiModel> = emptyList(),
    val error: UiError? = null,
)
