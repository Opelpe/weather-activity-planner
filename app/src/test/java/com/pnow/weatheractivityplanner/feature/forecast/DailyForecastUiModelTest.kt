package com.pnow.weatheractivityplanner.feature.forecast

import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import java.util.Locale
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

private object DailyForecastUiModelFixture {

    const val DATE = "2026-06-12"
    const val DISPLAY_DATE_US = "Fri, Jun 12"
    const val MAX_TEMPERATURE_CELSIUS = 24.0
    const val MIN_TEMPERATURE_CELSIUS = 14.0
    const val PRECIPITATION_SUM_MM = 2.0
    const val PRECIPITATION_PROBABILITY_PERCENT = 40
    const val SNOWFALL_SUM_CM = 0.0
    const val WIND_SPEED_MAX_KPH = 12.0
    const val WIND_GUSTS_MAX_KPH = 20.0
    const val UV_INDEX_MAX = 5.0
    const val DAYLIGHT_DURATION_HOURS = 15.5
    const val NIGHT_CLOUD_COVER_PERCENT = 50.0
    const val DAWN_DUSK_WIND_SPEED_KPH = 15.0
    const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 30.0
    const val DAYTIME_WIND_SPEED_MAX_KPH = 15.0
    const val DAYTIME_WIND_GUSTS_MAX_KPH = 25.0

    fun buildDailyForecast(condition: WeatherCondition = WeatherCondition.Clear) = DailyForecast(
        date = DATE,
        maxTemperatureCelsius = MAX_TEMPERATURE_CELSIUS,
        minTemperatureCelsius = MIN_TEMPERATURE_CELSIUS,
        precipitationSumMm = PRECIPITATION_SUM_MM,
        precipitationProbabilityMaxPercent = PRECIPITATION_PROBABILITY_PERCENT,
        snowfallSumCm = SNOWFALL_SUM_CM,
        windSpeedMaxKph = WIND_SPEED_MAX_KPH,
        windGustsMaxKph = WIND_GUSTS_MAX_KPH,
        uvIndexMax = UV_INDEX_MAX,
        daylightDurationHours = DAYLIGHT_DURATION_HOURS,
        nightCloudCoverPercent = NIGHT_CLOUD_COVER_PERCENT,
        dawnDuskWindSpeedKph = DAWN_DUSK_WIND_SPEED_KPH,
        dawnDuskPrecipitationProbabilityPercent = DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
        daytimeWindSpeedMaxKph = DAYTIME_WIND_SPEED_MAX_KPH,
        daytimeWindGustsMaxKph = DAYTIME_WIND_GUSTS_MAX_KPH,
        condition = condition,
    )
}

class DailyForecastUiModelTest {

    private lateinit var defaultLocale: Locale

    @Before
    fun setUp() {
        defaultLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        Locale.setDefault(defaultLocale)
    }

    @Test
    fun `given a daily forecast, when mapped to ui model, then formats the date as weekday, month and day`() {
        val result = DailyForecastUiModelFixture.buildDailyForecast().toUiModel()

        assertEquals(DailyForecastUiModelFixture.DISPLAY_DATE_US, result.displayDate)
    }

    @Test
    fun `given a daily forecast, when mapped to ui model, then keeps the original iso date for identity`() {
        val result = DailyForecastUiModelFixture.buildDailyForecast().toUiModel()

        assertEquals(DailyForecastUiModelFixture.DATE, result.date)
    }

    @Test
    fun `given a daily forecast, when mapped to ui model, then carries over precipitation probability and temperatures`() {
        val result = DailyForecastUiModelFixture.buildDailyForecast().toUiModel()

        assertEquals(DailyForecastUiModelFixture.PRECIPITATION_PROBABILITY_PERCENT, result.precipitationProbabilityPercent)
        assertEquals(DailyForecastUiModelFixture.MAX_TEMPERATURE_CELSIUS, result.maxTemperatureCelsius, 0.0)
        assertEquals(DailyForecastUiModelFixture.MIN_TEMPERATURE_CELSIUS, result.minTemperatureCelsius, 0.0)
    }

    @Test
    fun `given a thunderstorm condition, when mapped to ui model, then resolves the matching display name and icon`() {
        val result = DailyForecastUiModelFixture.buildDailyForecast(
            condition = WeatherCondition.Thunderstorm,
        ).toUiModel()

        assertEquals(R.string.weather_condition_thunderstorm, result.conditionDisplayNameRes)
        assertEquals(R.drawable.ic_weather_thunderstorm, result.conditionIconRes)
    }
}
