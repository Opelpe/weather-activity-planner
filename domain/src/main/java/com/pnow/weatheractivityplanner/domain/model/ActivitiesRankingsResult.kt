package com.pnow.weatheractivityplanner.domain.model

data class ActivitiesRankingsResult(
    val currentWeather: CurrentWeather,
    val rankings: List<ActivitiesRanking>,
)
