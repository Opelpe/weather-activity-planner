package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Test

private object FishingDayScorerFixture {

    const val DATE = "2026-06-15"
    const val TEMPERATURE_CELSIUS = 18.0
    const val DEFAULT_PRECIPITATION_MM = 0.0
    const val SNOWFALL_SUM_CM = 0.0
    const val WIND_GUSTS_KPH = 10.0
    const val UV_INDEX_MAX = 5.0
    const val DAYLIGHT_DURATION_HOURS = 12.0
    val DEFAULT_CONDITION = WeatherCondition.PartlyCloudy

    object CalmAndDry {

        const val WIND_SPEED_KPH = 5.0
        const val PRECIPITATION_PROBABILITY_PERCENT = 10
        const val EXPECTED_SCORE = 80f
        val EXPECTED_REASON = ActivitiesRankingReason.FISHING_CALM_AND_DRY
    }

    object Thunderstorm {

        const val WIND_SPEED_KPH = 5.0
        const val PRECIPITATION_PROBABILITY_PERCENT = 10
        val CONDITION = WeatherCondition.Thunderstorm
        const val EXPECTED_SCORE = 20f
        val EXPECTED_REASON = ActivitiesRankingReason.FISHING_THUNDERSTORM
    }

    object Windy {

        const val WIND_SPEED_KPH = 35.0
        const val PRECIPITATION_PROBABILITY_PERCENT = 10
        const val EXPECTED_SCORE = 15f
        val EXPECTED_REASON = ActivitiesRankingReason.FISHING_WINDY
    }

    object CalmOnly {

        const val WIND_SPEED_KPH = 5.0
        const val PRECIPITATION_PROBABILITY_PERCENT = 80
        const val EXPECTED_SCORE = 50f
        val EXPECTED_REASON = ActivitiesRankingReason.FISHING_CALM_ONLY
    }

    object DryOnly {

        const val WIND_SPEED_KPH = 20.0
        const val PRECIPITATION_PROBABILITY_PERCENT = 10
        const val EXPECTED_SCORE = 50f
        val EXPECTED_REASON = ActivitiesRankingReason.FISHING_DRY_ONLY
    }

    object None {

        const val WIND_SPEED_KPH = 20.0
        const val PRECIPITATION_PROBABILITY_PERCENT = 80
        const val EXPECTED_SCORE = 20f
        val EXPECTED_REASON = ActivitiesRankingReason.FISHING_NONE
    }
}

class FishingDayScorerTest {

    private val scorer = FishingDayScorer()

    @Test
    fun `given known day conditions, when score, then return expected score and reason`() {
        val cases = mapOf(
            buildDailyForecast(
                windSpeedKph = FishingDayScorerFixture.CalmAndDry.WIND_SPEED_KPH,
                precipitationProbabilityPercent = FishingDayScorerFixture.CalmAndDry.PRECIPITATION_PROBABILITY_PERCENT,
            ) to DayScore(
                score = FishingDayScorerFixture.CalmAndDry.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.CalmAndDry.EXPECTED_REASON,
            ),
            buildDailyForecast(
                windSpeedKph = FishingDayScorerFixture.Thunderstorm.WIND_SPEED_KPH,
                precipitationProbabilityPercent = FishingDayScorerFixture.Thunderstorm.PRECIPITATION_PROBABILITY_PERCENT,
                condition = FishingDayScorerFixture.Thunderstorm.CONDITION,
            ) to DayScore(
                score = FishingDayScorerFixture.Thunderstorm.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.Thunderstorm.EXPECTED_REASON,
            ),
            buildDailyForecast(
                windSpeedKph = FishingDayScorerFixture.Windy.WIND_SPEED_KPH,
                precipitationProbabilityPercent = FishingDayScorerFixture.Windy.PRECIPITATION_PROBABILITY_PERCENT,
            ) to DayScore(
                score = FishingDayScorerFixture.Windy.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.Windy.EXPECTED_REASON,
            ),
            buildDailyForecast(
                windSpeedKph = FishingDayScorerFixture.CalmOnly.WIND_SPEED_KPH,
                precipitationProbabilityPercent = FishingDayScorerFixture.CalmOnly.PRECIPITATION_PROBABILITY_PERCENT,
            ) to DayScore(
                score = FishingDayScorerFixture.CalmOnly.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.CalmOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                windSpeedKph = FishingDayScorerFixture.DryOnly.WIND_SPEED_KPH,
                precipitationProbabilityPercent = FishingDayScorerFixture.DryOnly.PRECIPITATION_PROBABILITY_PERCENT,
            ) to DayScore(
                score = FishingDayScorerFixture.DryOnly.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.DryOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                windSpeedKph = FishingDayScorerFixture.None.WIND_SPEED_KPH,
                precipitationProbabilityPercent = FishingDayScorerFixture.None.PRECIPITATION_PROBABILITY_PERCENT,
            ) to DayScore(
                score = FishingDayScorerFixture.None.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.None.EXPECTED_REASON,
            ),
        )

        cases.forEach { (day, expected) ->
            assertEquals(
                "wind=${day.windSpeedMaxKph} precipProb=${day.precipitationProbabilityMaxPercent} ${day.condition}",
                expected,
                scorer.score(day),
            )
        }
    }

    private fun buildDailyForecast(
        windSpeedKph: Double,
        precipitationProbabilityPercent: Int,
        condition: WeatherCondition = FishingDayScorerFixture.DEFAULT_CONDITION,
    ) = DailyForecast(
        date = FishingDayScorerFixture.DATE,
        maxTemperatureCelsius = FishingDayScorerFixture.TEMPERATURE_CELSIUS,
        minTemperatureCelsius = FishingDayScorerFixture.TEMPERATURE_CELSIUS,
        precipitationSumMm = FishingDayScorerFixture.DEFAULT_PRECIPITATION_MM,
        precipitationProbabilityMaxPercent = precipitationProbabilityPercent,
        snowfallSumCm = FishingDayScorerFixture.SNOWFALL_SUM_CM,
        windSpeedMaxKph = windSpeedKph,
        windGustsMaxKph = FishingDayScorerFixture.WIND_GUSTS_KPH,
        uvIndexMax = FishingDayScorerFixture.UV_INDEX_MAX,
        daylightDurationHours = FishingDayScorerFixture.DAYLIGHT_DURATION_HOURS,
        condition = condition,
    )
}
