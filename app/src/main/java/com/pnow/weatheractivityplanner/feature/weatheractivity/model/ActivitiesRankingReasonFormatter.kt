package com.pnow.weatheractivityplanner.feature.weatheractivity.model

import androidx.annotation.StringRes
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason

@StringRes
internal fun ActivitiesRankingReason.toStringRes(): Int = when (this) {
    is ActivitiesRankingReason.Skiing -> toStringRes()
    is ActivitiesRankingReason.Surfing -> toStringRes()
    is ActivitiesRankingReason.OutdoorSightseeing -> toStringRes()
    is ActivitiesRankingReason.IndoorSightseeing -> toStringRes()
    is ActivitiesRankingReason.Cycling -> toStringRes()
    is ActivitiesRankingReason.BeachDay -> toStringRes()
    is ActivitiesRankingReason.Stargazing -> toStringRes()
    is ActivitiesRankingReason.Fishing -> toStringRes()
}

@StringRes
private fun ActivitiesRankingReason.Skiing.toStringRes(): Int = when (this) {
    ActivitiesRankingReason.Skiing.SnowAndFreezing -> R.string.weather_activity_reason_skiing_snow_and_freezing
    ActivitiesRankingReason.Skiing.FreezingOnly -> R.string.weather_activity_reason_skiing_freezing_only
    ActivitiesRankingReason.Skiing.SnowOnly -> R.string.weather_activity_reason_skiing_snow_only
    ActivitiesRankingReason.Skiing.Rain -> R.string.weather_activity_reason_skiing_rain
    ActivitiesRankingReason.Skiing.None -> R.string.weather_activity_reason_skiing_none
}

@StringRes
private fun ActivitiesRankingReason.Surfing.toStringRes(): Int = when (this) {
    ActivitiesRankingReason.Surfing.Thunderstorm -> R.string.weather_activity_reason_surfing_thunderstorm
    ActivitiesRankingReason.Surfing.Rain -> R.string.weather_activity_reason_surfing_rain
    ActivitiesRankingReason.Surfing.WarmAndWindy -> R.string.weather_activity_reason_surfing_warm_and_windy
    ActivitiesRankingReason.Surfing.WindyOnly -> R.string.weather_activity_reason_surfing_windy_only
    ActivitiesRankingReason.Surfing.WarmOnly -> R.string.weather_activity_reason_surfing_warm_only
    ActivitiesRankingReason.Surfing.None -> R.string.weather_activity_reason_surfing_none
}

@StringRes
private fun ActivitiesRankingReason.OutdoorSightseeing.toStringRes(): Int = when (this) {
    ActivitiesRankingReason.OutdoorSightseeing.ClearAndComfortable ->
        R.string.weather_activity_reason_outdoor_clear_and_comfortable
    ActivitiesRankingReason.OutdoorSightseeing.Rain -> R.string.weather_activity_reason_outdoor_rain
    ActivitiesRankingReason.OutdoorSightseeing.Fog -> R.string.weather_activity_reason_outdoor_fog
    ActivitiesRankingReason.OutdoorSightseeing.ClearOnly -> R.string.weather_activity_reason_outdoor_clear_only
    ActivitiesRankingReason.OutdoorSightseeing.None -> R.string.weather_activity_reason_outdoor_none
}

@StringRes
private fun ActivitiesRankingReason.IndoorSightseeing.toStringRes(): Int = when (this) {
    ActivitiesRankingReason.IndoorSightseeing.PoorOutdoor -> R.string.weather_activity_reason_indoor_poor_outdoor
    ActivitiesRankingReason.IndoorSightseeing.ExtremeTemp -> R.string.weather_activity_reason_indoor_extreme_temp
    ActivitiesRankingReason.IndoorSightseeing.GreatOutdoor -> R.string.weather_activity_reason_indoor_great_outdoor
    ActivitiesRankingReason.IndoorSightseeing.None -> R.string.weather_activity_reason_indoor_none
}

@StringRes
private fun ActivitiesRankingReason.Cycling.toStringRes(): Int = when (this) {
    ActivitiesRankingReason.Cycling.Rain -> R.string.weather_activity_reason_cycling_rain
    ActivitiesRankingReason.Cycling.Gusty -> R.string.weather_activity_reason_cycling_gusty
    ActivitiesRankingReason.Cycling.Comfortable -> R.string.weather_activity_reason_cycling_comfortable
    ActivitiesRankingReason.Cycling.Cold -> R.string.weather_activity_reason_cycling_cold
    ActivitiesRankingReason.Cycling.None -> R.string.weather_activity_reason_cycling_none
}

@StringRes
private fun ActivitiesRankingReason.BeachDay.toStringRes(): Int = when (this) {
    ActivitiesRankingReason.BeachDay.Rain -> R.string.weather_activity_reason_beach_day_rain
    ActivitiesRankingReason.BeachDay.Windy -> R.string.weather_activity_reason_beach_day_windy
    ActivitiesRankingReason.BeachDay.WarmAndSunny -> R.string.weather_activity_reason_beach_day_warm_and_sunny
    ActivitiesRankingReason.BeachDay.SunnyOnly -> R.string.weather_activity_reason_beach_day_sunny_only
    ActivitiesRankingReason.BeachDay.WarmOnly -> R.string.weather_activity_reason_beach_day_warm_only
    ActivitiesRankingReason.BeachDay.None -> R.string.weather_activity_reason_beach_day_none
}

@StringRes
private fun ActivitiesRankingReason.Stargazing.toStringRes(): Int = when (this) {
    ActivitiesRankingReason.Stargazing.Rain -> R.string.weather_activity_reason_stargazing_rain
    ActivitiesRankingReason.Stargazing.Fog -> R.string.weather_activity_reason_stargazing_fog
    ActivitiesRankingReason.Stargazing.ClearAndLongNight ->
        R.string.weather_activity_reason_stargazing_clear_and_long_night
    ActivitiesRankingReason.Stargazing.ClearOnly -> R.string.weather_activity_reason_stargazing_clear_only
    ActivitiesRankingReason.Stargazing.LongNightOnly -> R.string.weather_activity_reason_stargazing_long_night_only
    ActivitiesRankingReason.Stargazing.None -> R.string.weather_activity_reason_stargazing_none
}

@StringRes
private fun ActivitiesRankingReason.Fishing.toStringRes(): Int = when (this) {
    ActivitiesRankingReason.Fishing.Thunderstorm -> R.string.weather_activity_reason_fishing_thunderstorm
    ActivitiesRankingReason.Fishing.Windy -> R.string.weather_activity_reason_fishing_windy
    ActivitiesRankingReason.Fishing.CalmAndDry -> R.string.weather_activity_reason_fishing_calm_and_dry
    ActivitiesRankingReason.Fishing.CalmOnly -> R.string.weather_activity_reason_fishing_calm_only
    ActivitiesRankingReason.Fishing.DryOnly -> R.string.weather_activity_reason_fishing_dry_only
    ActivitiesRankingReason.Fishing.None -> R.string.weather_activity_reason_fishing_none
}
