package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Test

private object OutdoorSightseeingDayScorerFixture {

    const val DATE = "2026-06-15"
    const val DEFAULT_PRECIPITATION_MM = 0.0
    const val WIND_SPEED_KPH = 5.0
    const val PRECIPITATION_PROBABILITY_PERCENT = 0
    const val SNOWFALL_SUM_CM = 0.0
    const val WIND_GUSTS_KPH = 10.0
    const val UV_INDEX_MAX = 5.0
    const val DAYLIGHT_DURATION_HOURS = 12.0
    const val NIGHT_CLOUD_COVER_PERCENT = 50.0
    const val DAWN_DUSK_WIND_SPEED_KPH = 15.0
    const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 30.0
    const val DAYTIME_WIND_SPEED_MAX_KPH = 15.0
    const val DAYTIME_WIND_GUSTS_MAX_KPH = 25.0

    object ClearAndComfortable {

        const val TEMPERATURE_CELSIUS = 22.0
        val CONDITION = WeatherCondition.Clear
        const val EXPECTED_SCORE = 100f
        val EXPECTED_REASON = ActivityDailyReason.OutdoorSightseeing.ClearAndComfortable
    }

    object Rain {

        const val TEMPERATURE_CELSIUS = 15.0
        const val PRECIPITATION_MM = 5.0
        val CONDITION = WeatherCondition.HeavyRain
        const val EXPECTED_SCORE = 15f
        val EXPECTED_REASON = ActivityDailyReason.OutdoorSightseeing.Rain
    }

    object Fog {

        const val TEMPERATURE_CELSIUS = 15.0
        val CONDITION = WeatherCondition.Fog
        const val EXPECTED_SCORE = 35f
        val EXPECTED_REASON = ActivityDailyReason.OutdoorSightseeing.Fog
    }

    object ClearOnly {

        const val TEMPERATURE_CELSIUS = 35.0
        val CONDITION = WeatherCondition.Clear
        const val EXPECTED_SCORE = 70f
        val EXPECTED_REASON = ActivityDailyReason.OutdoorSightseeing.ClearOnly
    }

    object None {

        const val TEMPERATURE_CELSIUS = 22.0
        val CONDITION = WeatherCondition.Overcast
        const val EXPECTED_SCORE = 60f
        val EXPECTED_REASON = ActivityDailyReason.OutdoorSightseeing.None
    }

    object PartiallyComfortable {

        // 40% of the way through the comfortable-tolerance zone (9.0-12.0), matching the previous
        // test intent of a partial comfort fraction.
        const val TEMPERATURE_CELSIUS = 10.2
        val CONDITION = WeatherCondition.Overcast
        const val EXPECTED_SCORE = 39f
        val EXPECTED_REASON = ActivityDailyReason.OutdoorSightseeing.WarmingUp
    }

    object PartiallyRainy {

        // 50% of the way through the precipitation ramp (0.5-5.0mm), so only half the rain penalty applies.
        const val TEMPERATURE_CELSIUS = 22.0
        const val PRECIPITATION_MM = 2.75
        val CONDITION = WeatherCondition.Overcast
        const val EXPECTED_SCORE = 37.5f
        val EXPECTED_REASON = ActivityDailyReason.OutdoorSightseeing.Rain
    }
}

class OutdoorSightseeingDayScorerTest {

    private val scorer = OutdoorSightseeingDayScorer()

    @Test
    fun `given known day conditions, when score, then return expected score and reason`() {
        val cases = mapOf(
            buildDailyForecast(
                temperatureCelsius = OutdoorSightseeingDayScorerFixture.ClearAndComfortable.TEMPERATURE_CELSIUS,
                condition = OutdoorSightseeingDayScorerFixture.ClearAndComfortable.CONDITION,
            ) to DayScore(
                score = OutdoorSightseeingDayScorerFixture.ClearAndComfortable.EXPECTED_SCORE,
                reason = OutdoorSightseeingDayScorerFixture.ClearAndComfortable.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = OutdoorSightseeingDayScorerFixture.Rain.TEMPERATURE_CELSIUS,
                condition = OutdoorSightseeingDayScorerFixture.Rain.CONDITION,
                precipitationMm = OutdoorSightseeingDayScorerFixture.Rain.PRECIPITATION_MM,
            ) to DayScore(
                score = OutdoorSightseeingDayScorerFixture.Rain.EXPECTED_SCORE,
                reason = OutdoorSightseeingDayScorerFixture.Rain.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = OutdoorSightseeingDayScorerFixture.Fog.TEMPERATURE_CELSIUS,
                condition = OutdoorSightseeingDayScorerFixture.Fog.CONDITION,
            ) to DayScore(
                score = OutdoorSightseeingDayScorerFixture.Fog.EXPECTED_SCORE,
                reason = OutdoorSightseeingDayScorerFixture.Fog.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = OutdoorSightseeingDayScorerFixture.ClearOnly.TEMPERATURE_CELSIUS,
                condition = OutdoorSightseeingDayScorerFixture.ClearOnly.CONDITION,
            ) to DayScore(
                score = OutdoorSightseeingDayScorerFixture.ClearOnly.EXPECTED_SCORE,
                reason = OutdoorSightseeingDayScorerFixture.ClearOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = OutdoorSightseeingDayScorerFixture.None.TEMPERATURE_CELSIUS,
                condition = OutdoorSightseeingDayScorerFixture.None.CONDITION,
            ) to DayScore(
                score = OutdoorSightseeingDayScorerFixture.None.EXPECTED_SCORE,
                reason = OutdoorSightseeingDayScorerFixture.None.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = OutdoorSightseeingDayScorerFixture.PartiallyComfortable.TEMPERATURE_CELSIUS,
                condition = OutdoorSightseeingDayScorerFixture.PartiallyComfortable.CONDITION,
            ) to DayScore(
                score = OutdoorSightseeingDayScorerFixture.PartiallyComfortable.EXPECTED_SCORE,
                reason = OutdoorSightseeingDayScorerFixture.PartiallyComfortable.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = OutdoorSightseeingDayScorerFixture.PartiallyRainy.TEMPERATURE_CELSIUS,
                condition = OutdoorSightseeingDayScorerFixture.PartiallyRainy.CONDITION,
                precipitationMm = OutdoorSightseeingDayScorerFixture.PartiallyRainy.PRECIPITATION_MM,
            ) to DayScore(
                score = OutdoorSightseeingDayScorerFixture.PartiallyRainy.EXPECTED_SCORE,
                reason = OutdoorSightseeingDayScorerFixture.PartiallyRainy.EXPECTED_REASON,
            ),
        )

        cases.forEach { (day, expected) ->
            assertEquals(day.condition.toString(), expected, scorer.score(day))
        }
    }

    private fun buildDailyForecast(
        temperatureCelsius: Double,
        condition: WeatherCondition,
        precipitationMm: Double = OutdoorSightseeingDayScorerFixture.DEFAULT_PRECIPITATION_MM,
    ) = DailyForecast(
        date = OutdoorSightseeingDayScorerFixture.DATE,
        maxTemperatureCelsius = temperatureCelsius,
        minTemperatureCelsius = temperatureCelsius,
        precipitationSumMm = precipitationMm,
        precipitationProbabilityMaxPercent = OutdoorSightseeingDayScorerFixture.PRECIPITATION_PROBABILITY_PERCENT,
        snowfallSumCm = OutdoorSightseeingDayScorerFixture.SNOWFALL_SUM_CM,
        windSpeedMaxKph = OutdoorSightseeingDayScorerFixture.WIND_SPEED_KPH,
        windGustsMaxKph = OutdoorSightseeingDayScorerFixture.WIND_GUSTS_KPH,
        uvIndexMax = OutdoorSightseeingDayScorerFixture.UV_INDEX_MAX,
        daylightDurationHours = OutdoorSightseeingDayScorerFixture.DAYLIGHT_DURATION_HOURS,
        nightCloudCoverPercent = OutdoorSightseeingDayScorerFixture.NIGHT_CLOUD_COVER_PERCENT,
        dawnDuskWindSpeedKph = OutdoorSightseeingDayScorerFixture.DAWN_DUSK_WIND_SPEED_KPH,
        dawnDuskPrecipitationProbabilityPercent = OutdoorSightseeingDayScorerFixture.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
        daytimeWindSpeedMaxKph = OutdoorSightseeingDayScorerFixture.DAYTIME_WIND_SPEED_MAX_KPH,
        daytimeWindGustsMaxKph = OutdoorSightseeingDayScorerFixture.DAYTIME_WIND_GUSTS_MAX_KPH,
        condition = condition,
    )
}
