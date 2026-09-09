package com.pnow.weatheractivityplanner.feature.common

import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherConditionFormatterTest {

    @Test
    fun `given weather conditions, when mapped to icon, then returns expected drawable`() {
        val cases = mapOf(
            WeatherCondition.Clear to R.drawable.ic_weather_clear,
            WeatherCondition.MainlyClear to R.drawable.ic_weather_clear,
            WeatherCondition.PartlyCloudy to R.drawable.ic_weather_partly_cloudy,
            WeatherCondition.Overcast to R.drawable.ic_weather_cloudy,
            WeatherCondition.Fog to R.drawable.ic_weather_fog,
            WeatherCondition.DepositingRimeFog to R.drawable.ic_weather_fog,
            WeatherCondition.LightDrizzle to R.drawable.ic_weather_rain,
            WeatherCondition.ModerateDrizzle to R.drawable.ic_weather_rain,
            WeatherCondition.DenseDrizzle to R.drawable.ic_weather_rain,
            WeatherCondition.LightRain to R.drawable.ic_weather_rain,
            WeatherCondition.ModerateRain to R.drawable.ic_weather_rain,
            WeatherCondition.HeavyRain to R.drawable.ic_weather_rain,
            WeatherCondition.SlightRainShowers to R.drawable.ic_weather_rain,
            WeatherCondition.ModerateRainShowers to R.drawable.ic_weather_rain,
            WeatherCondition.ViolentRainShowers to R.drawable.ic_weather_rain,
            WeatherCondition.LightSnow to R.drawable.ic_weather_snow,
            WeatherCondition.ModerateSnow to R.drawable.ic_weather_snow,
            WeatherCondition.HeavySnow to R.drawable.ic_weather_snow,
            WeatherCondition.SnowGrains to R.drawable.ic_weather_snow,
            WeatherCondition.SlightSnowShowers to R.drawable.ic_weather_snow,
            WeatherCondition.HeavySnowShowers to R.drawable.ic_weather_snow,
            WeatherCondition.Thunderstorm to R.drawable.ic_weather_thunderstorm,
            WeatherCondition.ThunderstormWithSlightHail to R.drawable.ic_weather_thunderstorm,
            WeatherCondition.ThunderstormWithHeavyHail to R.drawable.ic_weather_thunderstorm,
            WeatherCondition.Unknown(wmoCode = 42) to R.drawable.ic_weather_cloudy,
        )

        cases.forEach { (condition, expectedIconRes) ->
            assertEquals(condition.toString(), expectedIconRes, condition.toIconRes())
        }
    }
}
