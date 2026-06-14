package com.pnow.weatheractivityplanner.feature.weatheractivity.model

import androidx.annotation.StringRes
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason

@StringRes
internal fun ActivitiesRankingReason.toStringRes(): Int = when (this) {
    ActivitiesRankingReason.SKIING_SNOW_AND_FREEZING -> R.string.weather_activity_reason_skiing_snow_and_freezing
    ActivitiesRankingReason.SKIING_FREEZING_ONLY -> R.string.weather_activity_reason_skiing_freezing_only
    ActivitiesRankingReason.SKIING_SNOW_ONLY -> R.string.weather_activity_reason_skiing_snow_only
    ActivitiesRankingReason.SKIING_RAIN -> R.string.weather_activity_reason_skiing_rain
    ActivitiesRankingReason.SKIING_NONE -> R.string.weather_activity_reason_skiing_none
    ActivitiesRankingReason.SURFING_THUNDERSTORM -> R.string.weather_activity_reason_surfing_thunderstorm
    ActivitiesRankingReason.SURFING_RAIN -> R.string.weather_activity_reason_surfing_rain
    ActivitiesRankingReason.SURFING_WARM_AND_WINDY -> R.string.weather_activity_reason_surfing_warm_and_windy
    ActivitiesRankingReason.SURFING_WINDY_ONLY -> R.string.weather_activity_reason_surfing_windy_only
    ActivitiesRankingReason.SURFING_WARM_ONLY -> R.string.weather_activity_reason_surfing_warm_only
    ActivitiesRankingReason.SURFING_NONE -> R.string.weather_activity_reason_surfing_none
    ActivitiesRankingReason.OUTDOOR_CLEAR_AND_COMFORTABLE -> R.string.weather_activity_reason_outdoor_clear_and_comfortable
    ActivitiesRankingReason.OUTDOOR_RAIN -> R.string.weather_activity_reason_outdoor_rain
    ActivitiesRankingReason.OUTDOOR_FOG -> R.string.weather_activity_reason_outdoor_fog
    ActivitiesRankingReason.OUTDOOR_CLEAR_ONLY -> R.string.weather_activity_reason_outdoor_clear_only
    ActivitiesRankingReason.OUTDOOR_NONE -> R.string.weather_activity_reason_outdoor_none
    ActivitiesRankingReason.INDOOR_POOR_OUTDOOR -> R.string.weather_activity_reason_indoor_poor_outdoor
    ActivitiesRankingReason.INDOOR_EXTREME_TEMP -> R.string.weather_activity_reason_indoor_extreme_temp
    ActivitiesRankingReason.INDOOR_GREAT_OUTDOOR -> R.string.weather_activity_reason_indoor_great_outdoor
    ActivitiesRankingReason.INDOOR_NONE -> R.string.weather_activity_reason_indoor_none
}
