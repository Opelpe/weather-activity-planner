package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Test

private object BeachDayScorerFixture {

    const val DATE = "2026-06-15"
    const val DEFAULT_PRECIPITATION_MM = 0.0
    const val DEFAULT_WIND_SPEED_KPH = 5.0
    const val PRECIPITATION_PROBABILITY_PERCENT = 0
    const val SNOWFALL_SUM_CM = 0.0
    const val WIND_GUSTS_KPH = 10.0
    const val DAYLIGHT_DURATION_HOURS = 12.0
    val CONDITION = WeatherCondition.Clear

    object WarmAndSunny {

        const val TEMPERATURE_CELSIUS = 26.0
        const val UV_INDEX_MAX = 8.0
        const val EXPECTED_SCORE = 80f
        val EXPECTED_REASON = ActivitiesRankingReason.BeachDay.WarmAndSunny
    }

    object Windy {

        const val TEMPERATURE_CELSIUS = 26.0
        const val UV_INDEX_MAX = 8.0
        const val WIND_SPEED_KPH = 30.0
        const val EXPECTED_SCORE = 50f
        val EXPECTED_REASON = ActivitiesRankingReason.BeachDay.Windy
    }

    object RainyWindyAndCold {

        const val TEMPERATURE_CELSIUS = 15.0
        const val UV_INDEX_MAX = 3.0
        const val WIND_SPEED_KPH = 30.0
        const val PRECIPITATION_MM = 5.0
        const val EXPECTED_SCORE = 0f
        val EXPECTED_REASON = ActivitiesRankingReason.BeachDay.Rain
    }

    object SunnyOnly {

        const val TEMPERATURE_CELSIUS = 18.0
        const val UV_INDEX_MAX = 8.0
        const val EXPECTED_SCORE = 45f
        val EXPECTED_REASON = ActivitiesRankingReason.BeachDay.SunnyOnly
    }

    object WarmOnly {

        const val TEMPERATURE_CELSIUS = 26.0
        const val UV_INDEX_MAX = 3.0
        const val EXPECTED_SCORE = 45f
        val EXPECTED_REASON = ActivitiesRankingReason.BeachDay.WarmOnly
    }

    object None {

        const val TEMPERATURE_CELSIUS = 15.0
        const val UV_INDEX_MAX = 3.0
        const val EXPECTED_SCORE = 10f
        val EXPECTED_REASON = ActivitiesRankingReason.BeachDay.None
    }
}

class BeachDayScorerTest {

    private val scorer = BeachDayScorer()

    @Test
    fun `given known day conditions, when score, then return expected score and reason`() {
        val cases = mapOf(
            buildDailyForecast(
                temperatureCelsius = BeachDayScorerFixture.WarmAndSunny.TEMPERATURE_CELSIUS,
                uvIndexMax = BeachDayScorerFixture.WarmAndSunny.UV_INDEX_MAX,
            ) to DayScore(
                score = BeachDayScorerFixture.WarmAndSunny.EXPECTED_SCORE,
                reason = BeachDayScorerFixture.WarmAndSunny.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = BeachDayScorerFixture.Windy.TEMPERATURE_CELSIUS,
                uvIndexMax = BeachDayScorerFixture.Windy.UV_INDEX_MAX,
                windSpeedKph = BeachDayScorerFixture.Windy.WIND_SPEED_KPH,
            ) to DayScore(
                score = BeachDayScorerFixture.Windy.EXPECTED_SCORE,
                reason = BeachDayScorerFixture.Windy.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = BeachDayScorerFixture.RainyWindyAndCold.TEMPERATURE_CELSIUS,
                uvIndexMax = BeachDayScorerFixture.RainyWindyAndCold.UV_INDEX_MAX,
                windSpeedKph = BeachDayScorerFixture.RainyWindyAndCold.WIND_SPEED_KPH,
                precipitationMm = BeachDayScorerFixture.RainyWindyAndCold.PRECIPITATION_MM,
            ) to DayScore(
                score = BeachDayScorerFixture.RainyWindyAndCold.EXPECTED_SCORE,
                reason = BeachDayScorerFixture.RainyWindyAndCold.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = BeachDayScorerFixture.SunnyOnly.TEMPERATURE_CELSIUS,
                uvIndexMax = BeachDayScorerFixture.SunnyOnly.UV_INDEX_MAX,
            ) to DayScore(
                score = BeachDayScorerFixture.SunnyOnly.EXPECTED_SCORE,
                reason = BeachDayScorerFixture.SunnyOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = BeachDayScorerFixture.WarmOnly.TEMPERATURE_CELSIUS,
                uvIndexMax = BeachDayScorerFixture.WarmOnly.UV_INDEX_MAX,
            ) to DayScore(
                score = BeachDayScorerFixture.WarmOnly.EXPECTED_SCORE,
                reason = BeachDayScorerFixture.WarmOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = BeachDayScorerFixture.None.TEMPERATURE_CELSIUS,
                uvIndexMax = BeachDayScorerFixture.None.UV_INDEX_MAX,
            ) to DayScore(
                score = BeachDayScorerFixture.None.EXPECTED_SCORE,
                reason = BeachDayScorerFixture.None.EXPECTED_REASON,
            ),
        )

        cases.forEach { (day, expected) ->
            assertEquals(day.uvIndexMax.toString(), expected, scorer.score(day))
        }
    }

    private fun buildDailyForecast(
        temperatureCelsius: Double,
        uvIndexMax: Double,
        windSpeedKph: Double = BeachDayScorerFixture.DEFAULT_WIND_SPEED_KPH,
        precipitationMm: Double = BeachDayScorerFixture.DEFAULT_PRECIPITATION_MM,
    ) = DailyForecast(
        date = BeachDayScorerFixture.DATE,
        maxTemperatureCelsius = temperatureCelsius,
        minTemperatureCelsius = temperatureCelsius,
        precipitationSumMm = precipitationMm,
        precipitationProbabilityMaxPercent = BeachDayScorerFixture.PRECIPITATION_PROBABILITY_PERCENT,
        snowfallSumCm = BeachDayScorerFixture.SNOWFALL_SUM_CM,
        windSpeedMaxKph = windSpeedKph,
        windGustsMaxKph = BeachDayScorerFixture.WIND_GUSTS_KPH,
        uvIndexMax = uvIndexMax,
        daylightDurationHours = BeachDayScorerFixture.DAYLIGHT_DURATION_HOURS,
        condition = BeachDayScorerFixture.CONDITION,
    )
}
