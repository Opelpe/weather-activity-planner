package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.WeatherCondition

private val SNOWY_CONDITIONS = setOf(
    WeatherCondition.LightSnow,
    WeatherCondition.ModerateSnow,
    WeatherCondition.HeavySnow,
    WeatherCondition.SnowGrains,
    WeatherCondition.SlightSnowShowers,
    WeatherCondition.HeavySnowShowers,
)

private val RAINY_CONDITIONS = setOf(
    WeatherCondition.LightDrizzle,
    WeatherCondition.ModerateDrizzle,
    WeatherCondition.DenseDrizzle,
    WeatherCondition.LightRain,
    WeatherCondition.ModerateRain,
    WeatherCondition.HeavyRain,
    WeatherCondition.SlightRainShowers,
    WeatherCondition.ModerateRainShowers,
    WeatherCondition.ViolentRainShowers,
)

private val THUNDERSTORM_CONDITIONS = setOf(
    WeatherCondition.Thunderstorm,
    WeatherCondition.ThunderstormWithSlightHail,
    WeatherCondition.ThunderstormWithHeavyHail,
)

private val CLEAR_CONDITIONS = setOf(
    WeatherCondition.Clear,
    WeatherCondition.MainlyClear,
    WeatherCondition.PartlyCloudy,
)

private val FOGGY_CONDITIONS = setOf(
    WeatherCondition.Fog,
    WeatherCondition.DepositingRimeFog,
)

internal fun WeatherCondition.isSnowy(): Boolean = this in SNOWY_CONDITIONS
internal fun WeatherCondition.isRainy(): Boolean = this in RAINY_CONDITIONS
internal fun WeatherCondition.isThunderstorm(): Boolean = this in THUNDERSTORM_CONDITIONS
internal fun WeatherCondition.isClear(): Boolean = this in CLEAR_CONDITIONS
internal fun WeatherCondition.isFoggy(): Boolean = this in FOGGY_CONDITIONS
