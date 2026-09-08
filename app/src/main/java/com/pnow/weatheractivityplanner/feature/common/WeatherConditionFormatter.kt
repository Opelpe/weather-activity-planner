package com.pnow.weatheractivityplanner.feature.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition

@StringRes
internal fun WeatherCondition.toDisplayNameRes(): Int = when (this) {
    WeatherCondition.Clear -> R.string.weather_condition_clear
    WeatherCondition.MainlyClear -> R.string.weather_condition_mainly_clear
    WeatherCondition.PartlyCloudy -> R.string.weather_condition_partly_cloudy
    WeatherCondition.Overcast -> R.string.weather_condition_overcast
    WeatherCondition.Fog -> R.string.weather_condition_fog
    WeatherCondition.DepositingRimeFog -> R.string.weather_condition_depositing_rime_fog
    WeatherCondition.LightDrizzle -> R.string.weather_condition_light_drizzle
    WeatherCondition.ModerateDrizzle -> R.string.weather_condition_moderate_drizzle
    WeatherCondition.DenseDrizzle -> R.string.weather_condition_dense_drizzle
    WeatherCondition.LightRain -> R.string.weather_condition_light_rain
    WeatherCondition.ModerateRain -> R.string.weather_condition_moderate_rain
    WeatherCondition.HeavyRain -> R.string.weather_condition_heavy_rain
    WeatherCondition.LightSnow -> R.string.weather_condition_light_snow
    WeatherCondition.ModerateSnow -> R.string.weather_condition_moderate_snow
    WeatherCondition.HeavySnow -> R.string.weather_condition_heavy_snow
    WeatherCondition.SnowGrains -> R.string.weather_condition_snow_grains
    WeatherCondition.SlightRainShowers -> R.string.weather_condition_slight_rain_showers
    WeatherCondition.ModerateRainShowers -> R.string.weather_condition_moderate_rain_showers
    WeatherCondition.ViolentRainShowers -> R.string.weather_condition_violent_rain_showers
    WeatherCondition.SlightSnowShowers -> R.string.weather_condition_slight_snow_showers
    WeatherCondition.HeavySnowShowers -> R.string.weather_condition_heavy_snow_showers
    WeatherCondition.Thunderstorm -> R.string.weather_condition_thunderstorm
    WeatherCondition.ThunderstormWithSlightHail -> R.string.weather_condition_thunderstorm_slight_hail
    WeatherCondition.ThunderstormWithHeavyHail -> R.string.weather_condition_thunderstorm_heavy_hail
    is WeatherCondition.Unknown -> R.string.weather_condition_unknown
}

@DrawableRes
internal fun WeatherCondition.toIconRes(): Int = when (this) {
    WeatherCondition.Clear,
    WeatherCondition.MainlyClear,
        -> R.drawable.ic_weather_clear

    WeatherCondition.PartlyCloudy -> R.drawable.ic_weather_partly_cloudy

    WeatherCondition.Overcast -> R.drawable.ic_weather_cloudy

    WeatherCondition.Fog,
    WeatherCondition.DepositingRimeFog,
        -> R.drawable.ic_weather_fog

    WeatherCondition.LightDrizzle,
    WeatherCondition.ModerateDrizzle,
    WeatherCondition.DenseDrizzle,
    WeatherCondition.LightRain,
    WeatherCondition.ModerateRain,
    WeatherCondition.HeavyRain,
    WeatherCondition.SlightRainShowers,
    WeatherCondition.ModerateRainShowers,
    WeatherCondition.ViolentRainShowers,
        -> R.drawable.ic_weather_rain

    WeatherCondition.LightSnow,
    WeatherCondition.ModerateSnow,
    WeatherCondition.HeavySnow,
    WeatherCondition.SnowGrains,
    WeatherCondition.SlightSnowShowers,
    WeatherCondition.HeavySnowShowers,
        -> R.drawable.ic_weather_snow

    WeatherCondition.Thunderstorm,
    WeatherCondition.ThunderstormWithSlightHail,
    WeatherCondition.ThunderstormWithHeavyHail,
        -> R.drawable.ic_weather_thunderstorm

    is WeatherCondition.Unknown -> R.drawable.ic_weather_cloudy
}
