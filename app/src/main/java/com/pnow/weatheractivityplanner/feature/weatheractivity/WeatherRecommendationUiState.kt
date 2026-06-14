package com.pnow.weatheractivityplanner.feature.weatheractivity

import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.ActivitiesRankingUiModel
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.CurrentWeatherUiModel

data class WeatherRecommendationUiState(
    val locationName: String = "",
    val locationCountry: String = "",
    val isLoading: Boolean = false,
    val currentWeather: CurrentWeatherUiModel? = null,
    val ranking: List<ActivitiesRankingUiModel> = emptyList(),
    val error: UiError? = null,
)
