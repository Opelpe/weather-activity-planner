package com.pnow.weatheractivityplanner.feature.weatheractivity.model

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.ActivityWeeklyReason

@PluralsRes
internal fun ActivityWeeklyReason.toPluralsRes(): Int = when (this) {
    ActivityWeeklyReason.MIXED -> R.plurals.weather_activity_week_reason_mixed
    ActivityWeeklyReason.IMPROVING -> R.plurals.weather_activity_week_reason_improving
    ActivityWeeklyReason.DECLINING -> R.plurals.weather_activity_week_reason_declining
    ActivityWeeklyReason.CONSISTENTLY_GREAT -> R.plurals.weather_activity_week_reason_consistently_great
    ActivityWeeklyReason.CONSISTENTLY_GOOD -> R.plurals.weather_activity_week_reason_consistently_good
    ActivityWeeklyReason.CONSISTENTLY_AVERAGE -> R.plurals.weather_activity_week_reason_consistently_average
    ActivityWeeklyReason.CONSISTENTLY_POOR -> R.plurals.weather_activity_week_reason_consistently_poor
    ActivityWeeklyReason.CONSISTENTLY_TERRIBLE -> R.plurals.weather_activity_week_reason_consistently_terrible
}

@StringRes
internal fun ActivityDailyReason.toStringRes(): Int = when (this) {
    is ActivityDailyReason.Skiing -> toStringRes()
    is ActivityDailyReason.Surfing -> toStringRes()
    is ActivityDailyReason.OutdoorSightseeing -> toStringRes()
    is ActivityDailyReason.IndoorSightseeing -> toStringRes()
    is ActivityDailyReason.Cycling -> toStringRes()
    is ActivityDailyReason.Sunbathing -> toStringRes()
    is ActivityDailyReason.Stargazing -> toStringRes()
    is ActivityDailyReason.Fishing -> toStringRes()
}

@StringRes
private fun ActivityDailyReason.Skiing.toStringRes(): Int = when (this) {
    ActivityDailyReason.Skiing.SnowAndFreezing -> R.string.weather_activity_reason_skiing_snow_and_freezing
    ActivityDailyReason.Skiing.FreezingOnly -> R.string.weather_activity_reason_skiing_freezing_only
    ActivityDailyReason.Skiing.SnowOnly -> R.string.weather_activity_reason_skiing_snow_only
    ActivityDailyReason.Skiing.Rain -> R.string.weather_activity_reason_skiing_rain
    ActivityDailyReason.Skiing.TooWarm -> R.string.weather_activity_reason_skiing_too_warm
    ActivityDailyReason.Skiing.GettingColder -> R.string.weather_activity_reason_skiing_getting_colder
    ActivityDailyReason.Skiing.None -> R.string.weather_activity_reason_skiing_none
}

@StringRes
private fun ActivityDailyReason.Surfing.toStringRes(): Int = when (this) {
    ActivityDailyReason.Surfing.Thunderstorm -> R.string.weather_activity_reason_surfing_thunderstorm
    ActivityDailyReason.Surfing.Rain -> R.string.weather_activity_reason_surfing_rain
    ActivityDailyReason.Surfing.WarmAndWindy -> R.string.weather_activity_reason_surfing_warm_and_windy
    ActivityDailyReason.Surfing.WindyOnly -> R.string.weather_activity_reason_surfing_windy_only
    ActivityDailyReason.Surfing.WarmOnly -> R.string.weather_activity_reason_surfing_warm_only
    ActivityDailyReason.Surfing.BuildingConditions -> R.string.weather_activity_reason_surfing_building_conditions
    ActivityDailyReason.Surfing.None -> R.string.weather_activity_reason_surfing_none
}

@StringRes
private fun ActivityDailyReason.OutdoorSightseeing.toStringRes(): Int = when (this) {
    ActivityDailyReason.OutdoorSightseeing.ClearAndComfortable ->
        R.string.weather_activity_reason_outdoor_clear_and_comfortable

    ActivityDailyReason.OutdoorSightseeing.Rain -> R.string.weather_activity_reason_outdoor_rain
    ActivityDailyReason.OutdoorSightseeing.Fog -> R.string.weather_activity_reason_outdoor_fog
    ActivityDailyReason.OutdoorSightseeing.ClearOnly -> R.string.weather_activity_reason_outdoor_clear_only
    ActivityDailyReason.OutdoorSightseeing.WarmingUp -> R.string.weather_activity_reason_outdoor_warming_up
    ActivityDailyReason.OutdoorSightseeing.None -> R.string.weather_activity_reason_outdoor_none
}

@StringRes
private fun ActivityDailyReason.IndoorSightseeing.toStringRes(): Int = when (this) {
    ActivityDailyReason.IndoorSightseeing.PoorOutdoor -> R.string.weather_activity_reason_indoor_poor_outdoor
    ActivityDailyReason.IndoorSightseeing.ExtremeTemp -> R.string.weather_activity_reason_indoor_extreme_temp
    ActivityDailyReason.IndoorSightseeing.GreatOutdoor -> R.string.weather_activity_reason_indoor_great_outdoor
    ActivityDailyReason.IndoorSightseeing.None -> R.string.weather_activity_reason_indoor_none
}

@StringRes
private fun ActivityDailyReason.Cycling.toStringRes(): Int = when (this) {
    ActivityDailyReason.Cycling.Rain -> R.string.weather_activity_reason_cycling_rain
    ActivityDailyReason.Cycling.Gusty -> R.string.weather_activity_reason_cycling_gusty
    ActivityDailyReason.Cycling.Comfortable -> R.string.weather_activity_reason_cycling_comfortable
    ActivityDailyReason.Cycling.Cold -> R.string.weather_activity_reason_cycling_cold
    ActivityDailyReason.Cycling.WindPickingUp -> R.string.weather_activity_reason_cycling_wind_picking_up
    ActivityDailyReason.Cycling.None -> R.string.weather_activity_reason_cycling_none
}

@StringRes
private fun ActivityDailyReason.Sunbathing.toStringRes(): Int = when (this) {
    ActivityDailyReason.Sunbathing.Rain -> R.string.weather_activity_reason_sunbathing_rain
    ActivityDailyReason.Sunbathing.Windy -> R.string.weather_activity_reason_sunbathing_windy
    ActivityDailyReason.Sunbathing.WarmAndSunny -> R.string.weather_activity_reason_sunbathing_warm_and_sunny
    ActivityDailyReason.Sunbathing.SunnyOnly -> R.string.weather_activity_reason_sunbathing_sunny_only
    ActivityDailyReason.Sunbathing.WarmOnly -> R.string.weather_activity_reason_sunbathing_warm_only
    ActivityDailyReason.Sunbathing.WarmingUp -> R.string.weather_activity_reason_sunbathing_warming_up
    ActivityDailyReason.Sunbathing.None -> R.string.weather_activity_reason_sunbathing_none
}

@StringRes
private fun ActivityDailyReason.Stargazing.toStringRes(): Int = when (this) {
    ActivityDailyReason.Stargazing.Rain -> R.string.weather_activity_reason_stargazing_rain
    ActivityDailyReason.Stargazing.Fog -> R.string.weather_activity_reason_stargazing_fog
    ActivityDailyReason.Stargazing.ClearAndLongNight ->
        R.string.weather_activity_reason_stargazing_clear_and_long_night

    ActivityDailyReason.Stargazing.ClearOnly -> R.string.weather_activity_reason_stargazing_clear_only
    ActivityDailyReason.Stargazing.LongNightOnly -> R.string.weather_activity_reason_stargazing_long_night_only
    ActivityDailyReason.Stargazing.None -> R.string.weather_activity_reason_stargazing_none
}

@StringRes
private fun ActivityDailyReason.Fishing.toStringRes(): Int = when (this) {
    ActivityDailyReason.Fishing.Thunderstorm -> R.string.weather_activity_reason_fishing_thunderstorm
    ActivityDailyReason.Fishing.Windy -> R.string.weather_activity_reason_fishing_windy
    ActivityDailyReason.Fishing.CalmAndDry -> R.string.weather_activity_reason_fishing_calm_and_dry
    ActivityDailyReason.Fishing.CalmOnly -> R.string.weather_activity_reason_fishing_calm_only
    ActivityDailyReason.Fishing.DryOnly -> R.string.weather_activity_reason_fishing_dry_only
    ActivityDailyReason.Fishing.WatersCalming -> R.string.weather_activity_reason_fishing_waters_calming
    ActivityDailyReason.Fishing.None -> R.string.weather_activity_reason_fishing_none
}
