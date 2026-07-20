package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Test

private object StargazingDayScorerFixture {

    const val DATE = "2026-06-15"
    const val TEMPERATURE_CELSIUS = 15.0
    const val DEFAULT_PRECIPITATION_MM = 0.0
    const val DEFAULT_WIND_SPEED_KPH = 5.0
    const val PRECIPITATION_PROBABILITY_PERCENT = 0
    const val SNOWFALL_SUM_CM = 0.0
    const val WIND_GUSTS_KPH = 10.0
    const val UV_INDEX_MAX = 5.0
    const val SHORT_DAYLIGHT_DURATION_HOURS = 15.0
    const val LONG_NIGHT_DAYLIGHT_DURATION_HOURS = 8.0

    object ClearAndLongNight {

        val CONDITION = WeatherCondition.Clear
        const val DAYLIGHT_DURATION_HOURS = LONG_NIGHT_DAYLIGHT_DURATION_HOURS
        const val EXPECTED_SCORE = 90f
        val EXPECTED_REASON = ActivitiesRankingReason.STARGAZING_CLEAR_AND_LONG_NIGHT
    }

    object Rain {

        val CONDITION = WeatherCondition.HeavyRain
        const val DAYLIGHT_DURATION_HOURS = SHORT_DAYLIGHT_DURATION_HOURS
        const val PRECIPITATION_MM = 5.0
        const val EXPECTED_SCORE = 0f
        val EXPECTED_REASON = ActivitiesRankingReason.STARGAZING_RAIN
    }

    object Fog {

        val CONDITION = WeatherCondition.Fog
        const val DAYLIGHT_DURATION_HOURS = SHORT_DAYLIGHT_DURATION_HOURS
        const val EXPECTED_SCORE = 0f
        val EXPECTED_REASON = ActivitiesRankingReason.STARGAZING_FOG
    }

    object ClearOnly {

        val CONDITION = WeatherCondition.Clear
        const val DAYLIGHT_DURATION_HOURS = SHORT_DAYLIGHT_DURATION_HOURS
        const val EXPECTED_SCORE = 65f
        val EXPECTED_REASON = ActivitiesRankingReason.STARGAZING_CLEAR_ONLY
    }

    object LongNightOnly {

        val CONDITION = WeatherCondition.Overcast
        const val DAYLIGHT_DURATION_HOURS = LONG_NIGHT_DAYLIGHT_DURATION_HOURS
        const val EXPECTED_SCORE = 45f
        val EXPECTED_REASON = ActivitiesRankingReason.STARGAZING_LONG_NIGHT_ONLY
    }

    object None {

        val CONDITION = WeatherCondition.Overcast
        const val DAYLIGHT_DURATION_HOURS = SHORT_DAYLIGHT_DURATION_HOURS
        const val EXPECTED_SCORE = 20f
        val EXPECTED_REASON = ActivitiesRankingReason.STARGAZING_NONE
    }
}

class StargazingDayScorerTest {

    private val scorer = StargazingDayScorer()

    @Test
    fun `given known day conditions, when score, then return expected score and reason`() {
        val cases = mapOf(
            buildDailyForecast(
                condition = StargazingDayScorerFixture.ClearAndLongNight.CONDITION,
                daylightDurationHours = StargazingDayScorerFixture.ClearAndLongNight.DAYLIGHT_DURATION_HOURS,
            ) to DayScore(
                score = StargazingDayScorerFixture.ClearAndLongNight.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.ClearAndLongNight.EXPECTED_REASON,
            ),
            buildDailyForecast(
                condition = StargazingDayScorerFixture.Rain.CONDITION,
                daylightDurationHours = StargazingDayScorerFixture.Rain.DAYLIGHT_DURATION_HOURS,
                precipitationMm = StargazingDayScorerFixture.Rain.PRECIPITATION_MM,
            ) to DayScore(
                score = StargazingDayScorerFixture.Rain.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.Rain.EXPECTED_REASON,
            ),
            buildDailyForecast(
                condition = StargazingDayScorerFixture.Fog.CONDITION,
                daylightDurationHours = StargazingDayScorerFixture.Fog.DAYLIGHT_DURATION_HOURS,
            ) to DayScore(
                score = StargazingDayScorerFixture.Fog.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.Fog.EXPECTED_REASON,
            ),
            buildDailyForecast(
                condition = StargazingDayScorerFixture.ClearOnly.CONDITION,
                daylightDurationHours = StargazingDayScorerFixture.ClearOnly.DAYLIGHT_DURATION_HOURS,
            ) to DayScore(
                score = StargazingDayScorerFixture.ClearOnly.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.ClearOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                condition = StargazingDayScorerFixture.LongNightOnly.CONDITION,
                daylightDurationHours = StargazingDayScorerFixture.LongNightOnly.DAYLIGHT_DURATION_HOURS,
            ) to DayScore(
                score = StargazingDayScorerFixture.LongNightOnly.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.LongNightOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                condition = StargazingDayScorerFixture.None.CONDITION,
                daylightDurationHours = StargazingDayScorerFixture.None.DAYLIGHT_DURATION_HOURS,
            ) to DayScore(
                score = StargazingDayScorerFixture.None.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.None.EXPECTED_REASON,
            ),
        )

        cases.forEach { (day, expected) ->
            assertEquals(
                "${day.condition} / ${day.daylightDurationHours}h",
                expected,
                scorer.score(day),
            )
        }
    }

    private fun buildDailyForecast(
        condition: WeatherCondition,
        daylightDurationHours: Double,
        precipitationMm: Double = StargazingDayScorerFixture.DEFAULT_PRECIPITATION_MM,
    ) = DailyForecast(
        date = StargazingDayScorerFixture.DATE,
        maxTemperatureCelsius = StargazingDayScorerFixture.TEMPERATURE_CELSIUS,
        minTemperatureCelsius = StargazingDayScorerFixture.TEMPERATURE_CELSIUS,
        precipitationSumMm = precipitationMm,
        precipitationProbabilityMaxPercent = StargazingDayScorerFixture.PRECIPITATION_PROBABILITY_PERCENT,
        snowfallSumCm = StargazingDayScorerFixture.SNOWFALL_SUM_CM,
        windSpeedMaxKph = StargazingDayScorerFixture.DEFAULT_WIND_SPEED_KPH,
        windGustsMaxKph = StargazingDayScorerFixture.WIND_GUSTS_KPH,
        uvIndexMax = StargazingDayScorerFixture.UV_INDEX_MAX,
        daylightDurationHours = daylightDurationHours,
        condition = condition,
    )
}
