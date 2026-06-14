package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import com.pnow.weatheractivityplanner.domain.ranking.SkiingDayScorer
import org.junit.Assert.assertEquals
import org.junit.Test

private object SkiingDayScorerFixture {

    const val DATE = "2026-06-15"
    const val PRECIPITATION_MM = 0.0
    const val WIND_SPEED_KPH = 5.0
    const val PRECIPITATION_PROBABILITY_PERCENT = 0
    const val SNOWFALL_SUM_CM = 0.0
    const val WIND_GUSTS_KPH = 10.0
    const val UV_INDEX_MAX = 5.0
    const val DAYLIGHT_DURATION_HOURS = 12.0

    object SnowAndFreezing {

        const val TEMPERATURE_CELSIUS = -5.0
        val CONDITION = WeatherCondition.HeavySnow
        const val EXPECTED_SCORE = 100f
        val EXPECTED_REASON = ActivitiesRankingReason.SKIING_SNOW_AND_FREEZING
    }

    object FreezingOnly {

        const val TEMPERATURE_CELSIUS = -10.0
        val CONDITION = WeatherCondition.Clear
        const val EXPECTED_SCORE = 45f
        val EXPECTED_REASON = ActivitiesRankingReason.SKIING_FREEZING_ONLY
    }

    object SnowOnly {

        const val TEMPERATURE_CELSIUS = 0.0
        val CONDITION = WeatherCondition.LightSnow
        const val EXPECTED_SCORE = 80f
        val EXPECTED_REASON = ActivitiesRankingReason.SKIING_SNOW_ONLY
    }

    object Rain {

        const val TEMPERATURE_CELSIUS = 5.0
        val CONDITION = WeatherCondition.HeavyRain
        const val EXPECTED_SCORE = 0f
        val EXPECTED_REASON = ActivitiesRankingReason.SKIING_RAIN
    }

    object MildAndDry {

        const val TEMPERATURE_CELSIUS = 20.0
        val CONDITION = WeatherCondition.PartlyCloudy
        const val EXPECTED_SCORE = 0f
        val EXPECTED_REASON = ActivitiesRankingReason.SKIING_NONE
    }
}

class SkiingDayScorerTest {

    private val scorer = SkiingDayScorer()

    @Test
    fun `given known day conditions, when score, then return expected score and reason`() {
        val cases = mapOf(
            buildDailyForecast(
                temperatureCelsius = SkiingDayScorerFixture.SnowAndFreezing.TEMPERATURE_CELSIUS,
                condition = SkiingDayScorerFixture.SnowAndFreezing.CONDITION,
            ) to DayScore(
                score = SkiingDayScorerFixture.SnowAndFreezing.EXPECTED_SCORE,
                reason = SkiingDayScorerFixture.SnowAndFreezing.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SkiingDayScorerFixture.FreezingOnly.TEMPERATURE_CELSIUS,
                condition = SkiingDayScorerFixture.FreezingOnly.CONDITION,
            ) to DayScore(
                score = SkiingDayScorerFixture.FreezingOnly.EXPECTED_SCORE,
                reason = SkiingDayScorerFixture.FreezingOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SkiingDayScorerFixture.SnowOnly.TEMPERATURE_CELSIUS,
                condition = SkiingDayScorerFixture.SnowOnly.CONDITION,
            ) to DayScore(
                score = SkiingDayScorerFixture.SnowOnly.EXPECTED_SCORE,
                reason = SkiingDayScorerFixture.SnowOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SkiingDayScorerFixture.Rain.TEMPERATURE_CELSIUS,
                condition = SkiingDayScorerFixture.Rain.CONDITION,
            ) to DayScore(
                score = SkiingDayScorerFixture.Rain.EXPECTED_SCORE,
                reason = SkiingDayScorerFixture.Rain.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SkiingDayScorerFixture.MildAndDry.TEMPERATURE_CELSIUS,
                condition = SkiingDayScorerFixture.MildAndDry.CONDITION,
            ) to DayScore(
                score = SkiingDayScorerFixture.MildAndDry.EXPECTED_SCORE,
                reason = SkiingDayScorerFixture.MildAndDry.EXPECTED_REASON,
            ),
        )

        cases.forEach { (day, expected) ->
            assertEquals(day.condition.toString(), expected, scorer.score(day))
        }
    }

    private fun buildDailyForecast(
        temperatureCelsius: Double,
        condition: WeatherCondition,
    ) = DailyForecast(
        date = SkiingDayScorerFixture.DATE,
        maxTemperatureCelsius = temperatureCelsius,
        minTemperatureCelsius = temperatureCelsius,
        precipitationSumMm = SkiingDayScorerFixture.PRECIPITATION_MM,
        precipitationProbabilityMaxPercent = SkiingDayScorerFixture.PRECIPITATION_PROBABILITY_PERCENT,
        snowfallSumCm = SkiingDayScorerFixture.SNOWFALL_SUM_CM,
        windSpeedMaxKph = SkiingDayScorerFixture.WIND_SPEED_KPH,
        windGustsMaxKph = SkiingDayScorerFixture.WIND_GUSTS_KPH,
        uvIndexMax = SkiingDayScorerFixture.UV_INDEX_MAX,
        daylightDurationHours = SkiingDayScorerFixture.DAYLIGHT_DURATION_HOURS,
        condition = condition,
    )
}
