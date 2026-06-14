package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.model.Activities
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.CurrentWeather
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private object ActivityRankingCalculatorFixture {

    const val DEFAULT_TEMPERATURE_CELSIUS = 20.0
    const val DEFAULT_APPARENT_TEMPERATURE_CELSIUS = 20.0
    const val DEFAULT_HUMIDITY_PERCENT = 50
    const val DEFAULT_PRECIPITATION_MM = 0.0
    const val DEFAULT_WIND_SPEED_KPH = 5.0

    object SnowyAndFreezing {
        const val TEMPERATURE_CELSIUS = -5.0
        val CONDITION = WeatherCondition.HeavySnow
        val EXPECTED_REASON = ActivitiesRankingReason.SKIING_SNOW_AND_FREEZING
    }

    object WarmAndWindy {
        const val TEMPERATURE_CELSIUS = 24.0
        const val WIND_SPEED_KPH = 25.0
        val CONDITION = WeatherCondition.Overcast
        val EXPECTED_REASON = ActivitiesRankingReason.SURFING_WARM_AND_WINDY
    }

    object ClearAndComfortable {
        const val TEMPERATURE_CELSIUS = 22.0
        val CONDITION = WeatherCondition.Clear
        val EXPECTED_REASON = ActivitiesRankingReason.OUTDOOR_CLEAR_AND_COMFORTABLE
    }

    object HeavyRain {
        const val TEMPERATURE_CELSIUS = 15.0
        const val PRECIPITATION_MM = 5.0
        val CONDITION = WeatherCondition.HeavyRain
        val EXPECTED_REASON = ActivitiesRankingReason.INDOOR_POOR_OUTDOOR
    }

    object Thunderstorm {
        const val TEMPERATURE_CELSIUS = 18.0
        val CONDITION = WeatherCondition.Thunderstorm
        const val EXPECTED_SURFING_SCORE = 0f
        val EXPECTED_SURFING_REASON = ActivitiesRankingReason.SURFING_THUNDERSTORM
    }

    object HotAndClear {
        const val TEMPERATURE_CELSIUS = 29.0
        val CONDITION = WeatherCondition.Clear
        const val EXPECTED_TIED_SCORE = 55f
    }
}

class ActivitiesRankingCalculatorTest {

    private val calculator = ActivitiesRankingCalculator()

    @Test
    fun `given snowy and freezing weather, when calculate, then skiing ranks first`() {
        val current = buildCurrentWeather(
            temperatureCelsius = ActivityRankingCalculatorFixture.SnowyAndFreezing.TEMPERATURE_CELSIUS,
            condition = ActivityRankingCalculatorFixture.SnowyAndFreezing.CONDITION,
        )

        val rankings = calculator.calculate(current)

        assertEquals(Activities.SKIING, rankings.first().activities)
        assertEquals(ActivityRankingCalculatorFixture.SnowyAndFreezing.EXPECTED_REASON, rankings.first().reason)
    }

    @Test
    fun `given warm and windy weather, when calculate, then surfing ranks first`() {
        val current = buildCurrentWeather(
            temperatureCelsius = ActivityRankingCalculatorFixture.WarmAndWindy.TEMPERATURE_CELSIUS,
            windSpeedKph = ActivityRankingCalculatorFixture.WarmAndWindy.WIND_SPEED_KPH,
            condition = ActivityRankingCalculatorFixture.WarmAndWindy.CONDITION,
        )

        val rankings = calculator.calculate(current)

        assertEquals(Activities.SURFING, rankings.first().activities)
        assertEquals(ActivityRankingCalculatorFixture.WarmAndWindy.EXPECTED_REASON, rankings.first().reason)
    }

    @Test
    fun `given clear and comfortable weather, when calculate, then outdoor sightseeing ranks first`() {
        val current = buildCurrentWeather(
            temperatureCelsius = ActivityRankingCalculatorFixture.ClearAndComfortable.TEMPERATURE_CELSIUS,
            condition = ActivityRankingCalculatorFixture.ClearAndComfortable.CONDITION,
        )

        val rankings = calculator.calculate(current)

        assertEquals(Activities.OUTDOOR_SIGHTSEEING, rankings.first().activities)
        assertEquals(ActivityRankingCalculatorFixture.ClearAndComfortable.EXPECTED_REASON, rankings.first().reason)
    }

    @Test
    fun `given heavy rain, when calculate, then indoor sightseeing ranks first`() {
        val current = buildCurrentWeather(
            temperatureCelsius = ActivityRankingCalculatorFixture.HeavyRain.TEMPERATURE_CELSIUS,
            precipitationMm = ActivityRankingCalculatorFixture.HeavyRain.PRECIPITATION_MM,
            condition = ActivityRankingCalculatorFixture.HeavyRain.CONDITION,
        )

        val rankings = calculator.calculate(current)

        assertEquals(Activities.INDOOR_SIGHTSEEING, rankings.first().activities)
        assertEquals(ActivityRankingCalculatorFixture.HeavyRain.EXPECTED_REASON, rankings.first().reason)
    }

    @Test
    fun `given thunderstorm, when calculate, then surfing scores at minimum`() {
        val current = buildCurrentWeather(
            temperatureCelsius = ActivityRankingCalculatorFixture.Thunderstorm.TEMPERATURE_CELSIUS,
            condition = ActivityRankingCalculatorFixture.Thunderstorm.CONDITION,
        )

        val surfing = calculator.calculate(current).first { it.activities == Activities.SURFING }

        assertEquals(ActivityRankingCalculatorFixture.Thunderstorm.EXPECTED_SURFING_SCORE, surfing.score)
        assertEquals(ActivityRankingCalculatorFixture.Thunderstorm.EXPECTED_SURFING_REASON, surfing.reason)
    }

    @Test
    fun `given any weather, when calculate, then returns all activities sorted by score descending`() {
        val current = buildCurrentWeather()

        val rankings = calculator.calculate(current)

        assertEquals(Activities.entries.size, rankings.size)
        assertEquals(Activities.entries.toSet(), rankings.map { it.activities }.toSet())
        assertEquals(rankings.sortedByDescending { it.score }, rankings)
    }

    @Test
    fun `given weather that ties surfing and indoor sightseeing, when calculate, then tie is broken alphabetically by activity name`() {
        val current = buildCurrentWeather(
            temperatureCelsius = ActivityRankingCalculatorFixture.HotAndClear.TEMPERATURE_CELSIUS,
            condition = ActivityRankingCalculatorFixture.HotAndClear.CONDITION,
        )

        val rankings = calculator.calculate(current)
        val surfing = rankings.first { it.activities == Activities.SURFING }
        val indoor = rankings.first { it.activities == Activities.INDOOR_SIGHTSEEING }

        assertEquals(ActivityRankingCalculatorFixture.HotAndClear.EXPECTED_TIED_SCORE, surfing.score)
        assertEquals(ActivityRankingCalculatorFixture.HotAndClear.EXPECTED_TIED_SCORE, indoor.score)
        assertTrue(rankings.indexOf(indoor) < rankings.indexOf(surfing))
    }

    private fun buildCurrentWeather(
        temperatureCelsius: Double = ActivityRankingCalculatorFixture.DEFAULT_TEMPERATURE_CELSIUS,
        precipitationMm: Double = ActivityRankingCalculatorFixture.DEFAULT_PRECIPITATION_MM,
        windSpeedKph: Double = ActivityRankingCalculatorFixture.DEFAULT_WIND_SPEED_KPH,
        condition: WeatherCondition = WeatherCondition.PartlyCloudy,
    ) = CurrentWeather(
        temperatureCelsius = temperatureCelsius,
        apparentTemperatureCelsius = ActivityRankingCalculatorFixture.DEFAULT_APPARENT_TEMPERATURE_CELSIUS,
        relativeHumidityPercent = ActivityRankingCalculatorFixture.DEFAULT_HUMIDITY_PERCENT,
        precipitationMm = precipitationMm,
        windSpeedKph = windSpeedKph,
        condition = condition,
        isDay = true,
    )
}
