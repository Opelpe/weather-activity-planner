package com.pnow.weatheractivityplanner.data.mapper

import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private object WeatherConditionFixture {

    const val UNKNOWN_WMO_CODE = 42
}

class WeatherConditionMapperTest {

    @Test
    fun `given known wmo codes, when mapped, then return expected conditions`() {
        val cases = mapOf(
            0 to WeatherCondition.Clear,
            1 to WeatherCondition.MainlyClear,
            2 to WeatherCondition.PartlyCloudy,
            3 to WeatherCondition.Overcast,
            45 to WeatherCondition.Fog,
            48 to WeatherCondition.DepositingRimeFog,
            51 to WeatherCondition.LightDrizzle,
            53 to WeatherCondition.ModerateDrizzle,
            55 to WeatherCondition.DenseDrizzle,
            61 to WeatherCondition.LightRain,
            63 to WeatherCondition.ModerateRain,
            65 to WeatherCondition.HeavyRain,
            71 to WeatherCondition.LightSnow,
            73 to WeatherCondition.ModerateSnow,
            75 to WeatherCondition.HeavySnow,
            77 to WeatherCondition.SnowGrains,
            80 to WeatherCondition.SlightRainShowers,
            81 to WeatherCondition.ModerateRainShowers,
            82 to WeatherCondition.ViolentRainShowers,
            85 to WeatherCondition.SlightSnowShowers,
            86 to WeatherCondition.HeavySnowShowers,
            95 to WeatherCondition.Thunderstorm,
            96 to WeatherCondition.ThunderstormWithSlightHail,
            99 to WeatherCondition.ThunderstormWithHeavyHail,
        )

        cases.forEach { (code, expected) ->
            assertEquals("wmo code $code", expected, code.toWeatherCondition())
        }
    }

    @Test
    fun `given unknown wmo code, when mapped, then returns Unknown with code preserved`() {
        val result = WeatherConditionFixture.UNKNOWN_WMO_CODE.toWeatherCondition()

        assertTrue(result is WeatherCondition.Unknown)
        assertEquals(
            WeatherConditionFixture.UNKNOWN_WMO_CODE,
            (result as WeatherCondition.Unknown).wmoCode,
        )
    }
}
