package com.pnow.weatheractivityplanner.feature.weatheractivity

import com.pnow.weatheractivityplanner.domain.usecase.ActivityRankingDayRange
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.ActivitiesRankingUiModel
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.CurrentWeatherUiModel

data class WeatherRecommendationUiState(
    val locationName: String = "",
    val locationCountry: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val currentWeather: CurrentWeatherUiModel? = null,
    val ranking: List<ActivitiesRankingUiModel> = emptyList(),
    val selectedDayCount: Int = ActivityRankingDayRange.DEFAULT_DAY_COUNT,
    val error: UiError? = null,
)
