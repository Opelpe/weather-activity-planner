package com.pnow.weatheractivityplanner.feature.weatheractivity

import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.domain.model.Activities
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.ActivitiesRankingUiModel
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.CurrentWeatherUiModel

internal object WeatherRecommendationPreviewData {

    const val LOCATION_NAME = "London"
    const val LOCATION_COUNTRY = "United Kingdom"

    val CurrentWeather = CurrentWeatherUiModel(
        temperatureCelsius = 18.0,
        apparentTemperatureCelsius = 16.0,
        conditionDisplayNameRes = R.string.weather_condition_partly_cloudy,
        humidityPercent = 64,
        windSpeedKph = 14.0,
    )

    val Rankings = listOf(
        ActivitiesRankingUiModel(
            activities = Activities.OUTDOOR_SIGHTSEEING,
            score = 92f,
            weeklyReasonRes = R.string.weather_activity_week_reason_consistently_great,
            dailyReasonRes = R.string.weather_activity_reason_outdoor_clear_and_comfortable,
            isTopRanked = true,
        ),
        ActivitiesRankingUiModel(
            activities = Activities.INDOOR_SIGHTSEEING,
            score = 78f,
            weeklyReasonRes = R.string.weather_activity_week_reason_mixed,
            dailyReasonRes = R.string.weather_activity_reason_indoor_none,
            isTopRanked = false,
        ),
        ActivitiesRankingUiModel(
            activities = Activities.SURFING,
            score = 41f,
            weeklyReasonRes = R.string.weather_activity_week_reason_consistently_average,
            dailyReasonRes = R.string.weather_activity_reason_surfing_none,
            isTopRanked = false,
        ),
        ActivitiesRankingUiModel(
            activities = Activities.SKIING,
            score = 12f,
            weeklyReasonRes = R.string.weather_activity_week_reason_consistently_terrible,
            dailyReasonRes = R.string.weather_activity_reason_skiing_rain,
            isTopRanked = false,
        ),
    )

    val SuccessState = WeatherRecommendationUiState(
        locationName = LOCATION_NAME,
        locationCountry = LOCATION_COUNTRY,
        currentWeather = CurrentWeather,
        ranking = Rankings,
    )
}
