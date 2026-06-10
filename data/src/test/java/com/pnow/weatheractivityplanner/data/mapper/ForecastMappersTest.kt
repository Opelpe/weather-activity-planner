package com.pnow.weatheractivityplanner.data.mapper

import com.pnow.weatheractivityplanner.data.remote.dto.forecast.CurrentWeatherDto
import com.pnow.weatheractivityplanner.data.remote.dto.forecast.DailyDataDto
import com.pnow.weatheractivityplanner.data.remote.dto.forecast.ForecastResponseDto
import com.pnow.weatheractivityplanner.domain.model.CurrentWeather
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

private const val SECONDS_PER_HOUR = 3600.0
private const val DELTA = 0.0

private object ForecastFixture {

    const val LATITUDE = 52.52
    const val LONGITUDE = 13.42
    const val TIMEZONE = "Europe/Berlin"
    const val DAYS_COUNT = 2

    object Current {

        const val TIME = "2026-01-01T12:00"
        const val WEATHER_CODE_CLEAR = 0
        const val WEATHER_CODE_OVERCAST = 3
        const val IS_DAY = 1
        const val IS_NIGHT = 0
        const val TEMPERATURE_CELSIUS = 24.1
        const val APPARENT_TEMPERATURE_CELSIUS = 23.5
        const val HUMIDITY_PERCENT = 60
        const val PRECIPITATION_MM = 0.0
        const val WIND_SPEED_KPH = 11.3
    }

    object Day1 {

        const val DATE = "2026-01-01"
        const val WEATHER_CODE_CLEAR = 0
        const val MAX_TEMPERATURE_CELSIUS = 26.5
        const val MIN_TEMPERATURE_CELSIUS = 16.2
        const val PRECIPITATION_SUM_MM = 0.0
        const val PRECIPITATION_PROBABILITY_PERCENT = 10
        const val SNOWFALL_SUM_CM = 0.0
        const val WIND_SPEED_MAX_KPH = 12.5
        const val WIND_GUSTS_MAX_KPH = 20.0
        const val UV_INDEX_MAX = 5.5
        const val DAYLIGHT_DURATION_SECONDS = 32_400.0
    }

    object Day2 {

        const val DATE = "2026-01-02"
        const val WEATHER_CODE_PARTLY_CLOUDY = 2
        const val MAX_TEMPERATURE_CELSIUS = 24.0
        const val MIN_TEMPERATURE_CELSIUS = 14.0
        const val PRECIPITATION_SUM_MM = 1.2
        const val PRECIPITATION_PROBABILITY_PERCENT = 60
        const val SNOWFALL_SUM_CM = 5.0
        const val WIND_SPEED_MAX_KPH = 30.0
        const val WIND_GUSTS_MAX_KPH = 45.0
        const val UV_INDEX_MAX = 2.0
        const val DAYLIGHT_DURATION_SECONDS = 32_000.0
    }
}

class ForecastMappersTest {

    @Test
    fun `given full forecast dto, when toDomain, then all fields are mapped correctly`() {
        val dto = buildForecastResponseDto()

        val forecast = dto.toDomain()

        val expectedCurrent = CurrentWeather(
            temperatureCelsius = ForecastFixture.Current.TEMPERATURE_CELSIUS,
            apparentTemperatureCelsius = ForecastFixture.Current.APPARENT_TEMPERATURE_CELSIUS,
            relativeHumidityPercent = ForecastFixture.Current.HUMIDITY_PERCENT,
            precipitationMm = ForecastFixture.Current.PRECIPITATION_MM,
            windSpeedKph = ForecastFixture.Current.WIND_SPEED_KPH,
            condition = WeatherCondition.Clear,
            isDay = true,
        )

        assertEquals(ForecastFixture.LATITUDE, forecast.latitude, DELTA)
        assertEquals(ForecastFixture.LONGITUDE, forecast.longitude, DELTA)
        assertEquals(ForecastFixture.TIMEZONE, forecast.timezone)
        assertEquals(expectedCurrent, forecast.current)
    }

    @Test
    fun `given is_day 0, when toDomain, then isDay is false`() {
        val dto = buildForecastResponseDto(isDay = ForecastFixture.Current.IS_NIGHT)

        val forecast = dto.toDomain()

        assertFalse(forecast.current.isDay)
    }

    @Test
    fun `given daily data, when toDomain, then both days are mapped correctly`() {
        val dto = buildForecastResponseDto()

        val forecast = dto.toDomain()

        val expectedDaily = listOf(
            DailyForecast(
                date = ForecastFixture.Day1.DATE,
                maxTemperatureCelsius = ForecastFixture.Day1.MAX_TEMPERATURE_CELSIUS,
                minTemperatureCelsius = ForecastFixture.Day1.MIN_TEMPERATURE_CELSIUS,
                precipitationSumMm = ForecastFixture.Day1.PRECIPITATION_SUM_MM,
                precipitationProbabilityMaxPercent = ForecastFixture.Day1.PRECIPITATION_PROBABILITY_PERCENT,
                snowfallSumCm = ForecastFixture.Day1.SNOWFALL_SUM_CM,
                windSpeedMaxKph = ForecastFixture.Day1.WIND_SPEED_MAX_KPH,
                windGustsMaxKph = ForecastFixture.Day1.WIND_GUSTS_MAX_KPH,
                uvIndexMax = ForecastFixture.Day1.UV_INDEX_MAX,
                daylightDurationHours = ForecastFixture.Day1.DAYLIGHT_DURATION_SECONDS / SECONDS_PER_HOUR,
                condition = WeatherCondition.Clear,
            ),
            DailyForecast(
                date = ForecastFixture.Day2.DATE,
                maxTemperatureCelsius = ForecastFixture.Day2.MAX_TEMPERATURE_CELSIUS,
                minTemperatureCelsius = ForecastFixture.Day2.MIN_TEMPERATURE_CELSIUS,
                precipitationSumMm = ForecastFixture.Day2.PRECIPITATION_SUM_MM,
                precipitationProbabilityMaxPercent = ForecastFixture.Day2.PRECIPITATION_PROBABILITY_PERCENT,
                snowfallSumCm = ForecastFixture.Day2.SNOWFALL_SUM_CM,
                windSpeedMaxKph = ForecastFixture.Day2.WIND_SPEED_MAX_KPH,
                windGustsMaxKph = ForecastFixture.Day2.WIND_GUSTS_MAX_KPH,
                uvIndexMax = ForecastFixture.Day2.UV_INDEX_MAX,
                daylightDurationHours = ForecastFixture.Day2.DAYLIGHT_DURATION_SECONDS / SECONDS_PER_HOUR,
                condition = WeatherCondition.PartlyCloudy,
            ),
        )

        assertEquals(ForecastFixture.DAYS_COUNT, forecast.daily.size)
        expectedDaily.forEachIndexed { index, expected ->
            assertEquals("day $index", expected, forecast.daily[index])
        }
    }

    @Test
    fun `given weather code 3, when toDomain, then condition is Overcast`() {
        val dto =
            buildForecastResponseDto(currentWeatherCode = ForecastFixture.Current.WEATHER_CODE_OVERCAST)

        val forecast = dto.toDomain()

        assertEquals(WeatherCondition.Overcast, forecast.current.condition)
    }

    private fun buildForecastResponseDto(
        currentWeatherCode: Int = ForecastFixture.Current.WEATHER_CODE_CLEAR,
        isDay: Int = ForecastFixture.Current.IS_DAY,
    ) = ForecastResponseDto(
        latitude = ForecastFixture.LATITUDE,
        longitude = ForecastFixture.LONGITUDE,
        timezone = ForecastFixture.TIMEZONE,
        current = CurrentWeatherDto(
            time = ForecastFixture.Current.TIME,
            temperatureCelsius = ForecastFixture.Current.TEMPERATURE_CELSIUS,
            relativeHumidityPercent = ForecastFixture.Current.HUMIDITY_PERCENT,
            apparentTemperatureCelsius = ForecastFixture.Current.APPARENT_TEMPERATURE_CELSIUS,
            precipitation = ForecastFixture.Current.PRECIPITATION_MM,
            weatherCode = currentWeatherCode,
            windSpeedKph = ForecastFixture.Current.WIND_SPEED_KPH,
            isDay = isDay,
        ),
        daily = DailyDataDto(
            time = listOf(ForecastFixture.Day1.DATE, ForecastFixture.Day2.DATE),
            weatherCode = listOf(
                ForecastFixture.Day1.WEATHER_CODE_CLEAR,
                ForecastFixture.Day2.WEATHER_CODE_PARTLY_CLOUDY,
            ),
            maxTemperatureCelsius = listOf(
                ForecastFixture.Day1.MAX_TEMPERATURE_CELSIUS,
                ForecastFixture.Day2.MAX_TEMPERATURE_CELSIUS,
            ),
            minTemperatureCelsius = listOf(
                ForecastFixture.Day1.MIN_TEMPERATURE_CELSIUS,
                ForecastFixture.Day2.MIN_TEMPERATURE_CELSIUS,
            ),
            precipitationSumMm = listOf(
                ForecastFixture.Day1.PRECIPITATION_SUM_MM,
                ForecastFixture.Day2.PRECIPITATION_SUM_MM,
            ),
            precipitationProbabilityMaxPercent = listOf(
                ForecastFixture.Day1.PRECIPITATION_PROBABILITY_PERCENT,
                ForecastFixture.Day2.PRECIPITATION_PROBABILITY_PERCENT,
            ),
            snowfallSumCm = listOf(
                ForecastFixture.Day1.SNOWFALL_SUM_CM,
                ForecastFixture.Day2.SNOWFALL_SUM_CM,
            ),
            windSpeedMaxKph = listOf(
                ForecastFixture.Day1.WIND_SPEED_MAX_KPH,
                ForecastFixture.Day2.WIND_SPEED_MAX_KPH,
            ),
            windGustsMaxKph = listOf(
                ForecastFixture.Day1.WIND_GUSTS_MAX_KPH,
                ForecastFixture.Day2.WIND_GUSTS_MAX_KPH,
            ),
            uvIndexMax = listOf(
                ForecastFixture.Day1.UV_INDEX_MAX,
                ForecastFixture.Day2.UV_INDEX_MAX,
            ),
            daylightDurationSeconds = listOf(
                ForecastFixture.Day1.DAYLIGHT_DURATION_SECONDS,
                ForecastFixture.Day2.DAYLIGHT_DURATION_SECONDS,
            ),
        ),
    )
}
