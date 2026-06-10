package com.pnow.weatheractivityplanner.data.mapper

import com.pnow.weatheractivityplanner.domain.model.WeatherCondition

internal fun Int.toWeatherCondition(): WeatherCondition = when (this) {
    0 -> WeatherCondition.Clear
    1 -> WeatherCondition.MainlyClear
    2 -> WeatherCondition.PartlyCloudy
    3 -> WeatherCondition.Overcast
    45 -> WeatherCondition.Fog
    48 -> WeatherCondition.DepositingRimeFog
    51 -> WeatherCondition.LightDrizzle
    53 -> WeatherCondition.ModerateDrizzle
    55 -> WeatherCondition.DenseDrizzle
    61 -> WeatherCondition.LightRain
    63 -> WeatherCondition.ModerateRain
    65 -> WeatherCondition.HeavyRain
    71 -> WeatherCondition.LightSnow
    73 -> WeatherCondition.ModerateSnow
    75 -> WeatherCondition.HeavySnow
    77 -> WeatherCondition.SnowGrains
    80 -> WeatherCondition.SlightRainShowers
    81 -> WeatherCondition.ModerateRainShowers
    82 -> WeatherCondition.ViolentRainShowers
    85 -> WeatherCondition.SlightSnowShowers
    86 -> WeatherCondition.HeavySnowShowers
    95 -> WeatherCondition.Thunderstorm
    96 -> WeatherCondition.ThunderstormWithSlightHail
    99 -> WeatherCondition.ThunderstormWithHeavyHail
    else -> WeatherCondition.Unknown(wmoCode = this)
}
