package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import com.pnow.weatheractivityplanner.domain.ranking.IndoorSightseeingDayScorer
import org.junit.Assert.assertEquals
import org.junit.Test

private object IndoorSightseeingDayScorerFixture {

    const val DATE = "2026-06-15"
    const val DEFAULT_PRECIPITATION_MM = 0.0
    const val WIND_SPEED_KPH = 5.0
    const val PRECIPITATION_PROBABILITY_PERCENT = 0
    const val SNOWFALL_SUM_CM = 0.0
    const val WIND_GUSTS_KPH = 10.0
    const val UV_INDEX_MAX = 5.0
    const val DAYLIGHT_DURATION_HOURS = 12.0

    object PoorOutdoor {

        const val TEMPERATURE_CELSIUS = 15.0
        const val PRECIPITATION_MM = 5.0
        val CONDITION = WeatherCondition.HeavyRain
        const val EXPECTED_SCORE = 75f
        val EXPECTED_REASON = ActivitiesRankingReason.INDOOR_POOR_OUTDOOR
    }

    object ExtremeTemp {

        const val TEMPERATURE_CELSIUS = 35.0
        val CONDITION = WeatherCondition.Clear
        const val EXPECTED_SCORE = 55f
        val EXPECTED_REASON = ActivitiesRankingReason.INDOOR_EXTREME_TEMP
    }

    object GreatOutdoor {

        const val TEMPERATURE_CELSIUS = 22.0
        val CONDITION = WeatherCondition.Clear
        const val EXPECTED_SCORE = 25f
        val EXPECTED_REASON = ActivitiesRankingReason.INDOOR_GREAT_OUTDOOR
    }

    object None {

        const val TEMPERATURE_CELSIUS = 22.0
        val CONDITION = WeatherCondition.Overcast
        const val EXPECTED_SCORE = 40f
        val EXPECTED_REASON = ActivitiesRankingReason.INDOOR_NONE
    }
}

class IndoorSightseeingDayScorerTest {

    private val scorer = IndoorSightseeingDayScorer()

    @Test
    fun `given known day conditions, when score, then return expected score and reason`() {
        val cases = mapOf(
            buildDailyForecast(
                temperatureCelsius = IndoorSightseeingDayScorerFixture.PoorOutdoor.TEMPERATURE_CELSIUS,
                condition = IndoorSightseeingDayScorerFixture.PoorOutdoor.CONDITION,
                precipitationMm = IndoorSightseeingDayScorerFixture.PoorOutdoor.PRECIPITATION_MM,
            ) to DayScore(
                score = IndoorSightseeingDayScorerFixture.PoorOutdoor.EXPECTED_SCORE,
                reason = IndoorSightseeingDayScorerFixture.PoorOutdoor.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = IndoorSightseeingDayScorerFixture.ExtremeTemp.TEMPERATURE_CELSIUS,
                condition = IndoorSightseeingDayScorerFixture.ExtremeTemp.CONDITION,
            ) to DayScore(
                score = IndoorSightseeingDayScorerFixture.ExtremeTemp.EXPECTED_SCORE,
                reason = IndoorSightseeingDayScorerFixture.ExtremeTemp.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = IndoorSightseeingDayScorerFixture.GreatOutdoor.TEMPERATURE_CELSIUS,
                condition = IndoorSightseeingDayScorerFixture.GreatOutdoor.CONDITION,
            ) to DayScore(
                score = IndoorSightseeingDayScorerFixture.GreatOutdoor.EXPECTED_SCORE,
                reason = IndoorSightseeingDayScorerFixture.GreatOutdoor.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = IndoorSightseeingDayScorerFixture.None.TEMPERATURE_CELSIUS,
                condition = IndoorSightseeingDayScorerFixture.None.CONDITION,
            ) to DayScore(
                score = IndoorSightseeingDayScorerFixture.None.EXPECTED_SCORE,
                reason = IndoorSightseeingDayScorerFixture.None.EXPECTED_REASON,
            ),
        )

        cases.forEach { (day, expected) ->
            assertEquals(day.condition.toString(), expected, scorer.score(day))
        }
    }

    private fun buildDailyForecast(
        temperatureCelsius: Double,
        condition: WeatherCondition,
        precipitationMm: Double = IndoorSightseeingDayScorerFixture.DEFAULT_PRECIPITATION_MM,
    ) = DailyForecast(
        date = IndoorSightseeingDayScorerFixture.DATE,
        maxTemperatureCelsius = temperatureCelsius,
        minTemperatureCelsius = temperatureCelsius,
        precipitationSumMm = precipitationMm,
        precipitationProbabilityMaxPercent = IndoorSightseeingDayScorerFixture.PRECIPITATION_PROBABILITY_PERCENT,
        snowfallSumCm = IndoorSightseeingDayScorerFixture.SNOWFALL_SUM_CM,
        windSpeedMaxKph = IndoorSightseeingDayScorerFixture.WIND_SPEED_KPH,
        windGustsMaxKph = IndoorSightseeingDayScorerFixture.WIND_GUSTS_KPH,
        uvIndexMax = IndoorSightseeingDayScorerFixture.UV_INDEX_MAX,
        daylightDurationHours = IndoorSightseeingDayScorerFixture.DAYLIGHT_DURATION_HOURS,
        condition = condition,
    )
}
