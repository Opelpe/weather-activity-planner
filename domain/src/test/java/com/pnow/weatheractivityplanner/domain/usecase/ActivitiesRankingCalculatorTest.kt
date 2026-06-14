package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.model.Activities
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import com.pnow.weatheractivityplanner.domain.ranking.ActivitiesRankingCalculator
import com.pnow.weatheractivityplanner.domain.ranking.IndoorSightseeingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.OutdoorSightseeingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.SkiingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.SurfingDayScorer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private object ActivityRankingCalculatorFixture {

    const val DEFAULT_DATE = "2026-06-15"
    const val DEFAULT_TEMPERATURE_CELSIUS = 20.0
    const val DEFAULT_PRECIPITATION_MM = 0.0
    const val DEFAULT_WIND_SPEED_KPH = 5.0
    const val DEFAULT_PRECIPITATION_PROBABILITY_PERCENT = 0
    const val DEFAULT_SNOWFALL_SUM_CM = 0.0
    const val DEFAULT_WIND_GUSTS_KPH = 10.0
    const val DEFAULT_UV_INDEX_MAX = 5.0
    const val DEFAULT_DAYLIGHT_DURATION_HOURS = 12.0

    object SnowyAndFreezing {

        const val TEMPERATURE_CELSIUS = -5.0
        val CONDITION = WeatherCondition.HeavySnow
        const val EXPECTED_SKIING_SCORE = 100f
    }

    object SnowOnly {

        const val TEMPERATURE_CELSIUS = 0.0
        val CONDITION = WeatherCondition.LightSnow
        val EXPECTED_REASON = ActivitiesRankingReason.SKIING_SNOW_ONLY
        const val EXPECTED_SKIING_SCORE = 80f
    }

    object HotAndClear {

        const val TEMPERATURE_CELSIUS = 29.0
        val CONDITION = WeatherCondition.Clear
        const val EXPECTED_TIED_SCORE = 55f
    }

    object MildAndDry {

        const val TEMPERATURE_CELSIUS = 20.0
        val CONDITION = WeatherCondition.PartlyCloudy
        const val EXPECTED_SKIING_SCORE = 0f
    }

    object MixedSkiWeek {

        const val EXPECTED_AVERAGE_SKIING_SCORE =
            (SnowyAndFreezing.EXPECTED_SKIING_SCORE + SnowOnly.EXPECTED_SKIING_SCORE + MildAndDry.EXPECTED_SKIING_SCORE) / 3f
    }
}

class ActivitiesRankingCalculatorTest {

    private val calculator = ActivitiesRankingCalculator(
        skiingDayScorer = SkiingDayScorer(),
        surfingDayScorer = SurfingDayScorer(),
        outdoorSightseeingDayScorer = OutdoorSightseeingDayScorer(),
        indoorSightseeingDayScorer = IndoorSightseeingDayScorer(),
    )

    @Test
    fun `given any forecast, when calculate, then returns all activities sorted by score descending`() {
        val daily = listOf(buildDailyForecast())

        val rankings = calculator.calculate(daily)

        assertEquals(Activities.entries.size, rankings.size)
        assertEquals(Activities.entries.toSet(), rankings.map { it.activities }.toSet())
        assertEquals(rankings.sortedByDescending { it.score }, rankings)
    }

    @Test
    fun `given forecast that ties surfing and indoor sightseeing, when calculate, then tie is broken alphabetically by activity name`() {
        val daily = listOf(
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.HotAndClear.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.HotAndClear.CONDITION,
            ),
        )

        val rankings = calculator.calculate(daily)
        val surfing = rankings.first { it.activities == Activities.SURFING }
        val indoor = rankings.first { it.activities == Activities.INDOOR_SIGHTSEEING }

        assertEquals(
            ActivityRankingCalculatorFixture.HotAndClear.EXPECTED_TIED_SCORE,
            surfing.score,
        )
        assertEquals(ActivityRankingCalculatorFixture.HotAndClear.EXPECTED_TIED_SCORE, indoor.score)
        assertTrue(rankings.indexOf(indoor) < rankings.indexOf(surfing))
    }

    @Test
    fun `given a week mixing great, good and poor skiing days, when calculate, then skiing score is averaged across days and reason reflects the day closest to the average`() {
        val daily = listOf(
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.SnowyAndFreezing.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.SnowyAndFreezing.CONDITION,
            ),
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.SnowOnly.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.SnowOnly.CONDITION,
            ),
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.MildAndDry.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.MildAndDry.CONDITION,
            ),
        )

        val skiing = calculator.calculate(daily).first { it.activities == Activities.SKIING }

        assertEquals(
            ActivityRankingCalculatorFixture.MixedSkiWeek.EXPECTED_AVERAGE_SKIING_SCORE,
            skiing.score,
        )
        assertEquals(ActivityRankingCalculatorFixture.SnowOnly.EXPECTED_REASON, skiing.reason)
    }

    private fun buildDailyForecast(
        date: String = ActivityRankingCalculatorFixture.DEFAULT_DATE,
        temperatureCelsius: Double = ActivityRankingCalculatorFixture.DEFAULT_TEMPERATURE_CELSIUS,
        precipitationMm: Double = ActivityRankingCalculatorFixture.DEFAULT_PRECIPITATION_MM,
        windSpeedKph: Double = ActivityRankingCalculatorFixture.DEFAULT_WIND_SPEED_KPH,
        condition: WeatherCondition = WeatherCondition.PartlyCloudy,
    ) = DailyForecast(
        date = date,
        maxTemperatureCelsius = temperatureCelsius,
        minTemperatureCelsius = temperatureCelsius,
        precipitationSumMm = precipitationMm,
        precipitationProbabilityMaxPercent = ActivityRankingCalculatorFixture.DEFAULT_PRECIPITATION_PROBABILITY_PERCENT,
        snowfallSumCm = ActivityRankingCalculatorFixture.DEFAULT_SNOWFALL_SUM_CM,
        windSpeedMaxKph = windSpeedKph,
        windGustsMaxKph = ActivityRankingCalculatorFixture.DEFAULT_WIND_GUSTS_KPH,
        uvIndexMax = ActivityRankingCalculatorFixture.DEFAULT_UV_INDEX_MAX,
        daylightDurationHours = ActivityRankingCalculatorFixture.DEFAULT_DAYLIGHT_DURATION_HOURS,
        condition = condition,
    )
}
