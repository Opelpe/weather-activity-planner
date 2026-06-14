package com.pnow.weatheractivityplanner.domain.model

sealed interface WeatherCondition {
    data object Clear : WeatherCondition
    data object MainlyClear : WeatherCondition
    data object PartlyCloudy : WeatherCondition
    data object Overcast : WeatherCondition
    data object Fog : WeatherCondition
    data object DepositingRimeFog : WeatherCondition
    data object LightDrizzle : WeatherCondition
    data object ModerateDrizzle : WeatherCondition
    data object DenseDrizzle : WeatherCondition
    data object LightRain : WeatherCondition
    data object ModerateRain : WeatherCondition
    data object HeavyRain : WeatherCondition
    data object LightSnow : WeatherCondition
    data object ModerateSnow : WeatherCondition
    data object HeavySnow : WeatherCondition
    data object SnowGrains : WeatherCondition
    data object SlightRainShowers : WeatherCondition
    data object ModerateRainShowers : WeatherCondition
    data object ViolentRainShowers : WeatherCondition
    data object SlightSnowShowers : WeatherCondition
    data object HeavySnowShowers : WeatherCondition
    data object Thunderstorm : WeatherCondition
    data object ThunderstormWithSlightHail : WeatherCondition
    data object ThunderstormWithHeavyHail : WeatherCondition
    data class Unknown(val wmoCode: Int) : WeatherCondition
}
