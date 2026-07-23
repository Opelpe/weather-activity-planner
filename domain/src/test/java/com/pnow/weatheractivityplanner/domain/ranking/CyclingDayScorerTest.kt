package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Test

private object CyclingDayScorerFixture {

    const val DATE = "2026-06-15"
    const val DEFAULT_PRECIPITATION_MM = 0.0
    const val DEFAULT_WIND_SPEED_KPH = 5.0
    const val DEFAULT_DAYTIME_WIND_GUSTS_MAX_KPH = 10.0
    const val WIND_GUSTS_MAX_KPH = 20.0
    const val PRECIPITATION_PROBABILITY_PERCENT = 0
    const val SNOWFALL_SUM_CM = 0.0
    const val UV_INDEX_MAX = 5.0
    const val DAYLIGHT_DURATION_HOURS = 12.0
    const val NIGHT_CLOUD_COVER_PERCENT = 50.0
    const val DAWN_DUSK_WIND_SPEED_KPH = 15.0
    const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 30.0
    val CONDITION = WeatherCondition.PartlyCloudy

    object ComfortableAndCalm {

        const val TEMPERATURE_CELSIUS = 20.0
        const val EXPECTED_SCORE = 70f
        val EXPECTED_REASON = ActivityDailyReason.Cycling.Comfortable
    }

    object Gusty {

        const val TEMPERATURE_CELSIUS = 20.0
        const val DAYTIME_WIND_GUSTS_MAX_KPH = 45.0
        const val EXPECTED_SCORE = 30f
        val EXPECTED_REASON = ActivityDailyReason.Cycling.Gusty
    }

    object Rainy {

        const val TEMPERATURE_CELSIUS = 20.0
        const val PRECIPITATION_MM = 5.0
        const val EXPECTED_SCORE = 35f
        val EXPECTED_REASON = ActivityDailyReason.Cycling.Rain
    }

    object Cold {

        const val TEMPERATURE_CELSIUS = 0.0
        const val EXPECTED_SCORE = 5f
        val EXPECTED_REASON = ActivityDailyReason.Cycling.Cold
    }

    object MildNeither {

        const val TEMPERATURE_CELSIUS = 8.0
        const val EXPECTED_SCORE = 25f
        val EXPECTED_REASON = ActivityDailyReason.Cycling.None
    }

    object ColdGustyAndRainy {

        const val TEMPERATURE_CELSIUS = 0.0
        const val DAYTIME_WIND_GUSTS_MAX_KPH = 45.0
        const val PRECIPITATION_MM = 5.0
        const val EXPECTED_SCORE = 0f
        val EXPECTED_REASON = ActivityDailyReason.Cycling.Rain
    }

    object PartiallyGusty {

        // 50% of the way through the gusty ramp (20-40 kph), so only half the gusty penalty applies.
        const val TEMPERATURE_CELSIUS = 20.0
        const val DAYTIME_WIND_GUSTS_MAX_KPH = 30.0
        const val EXPECTED_SCORE = 50f
        val EXPECTED_REASON = ActivityDailyReason.Cycling.Comfortable
    }

    object PartiallyCold {

        // 50% of the way through the cold ramp (5-0°C), so only half the cold penalty applies.
        const val TEMPERATURE_CELSIUS = 2.5
        const val EXPECTED_SCORE = 15f
        val EXPECTED_REASON = ActivityDailyReason.Cycling.Cold
    }

    object WindPickingUp {

        const val TEMPERATURE_CELSIUS = 8.0
        const val DAYTIME_WIND_GUSTS_MAX_KPH = 30.0
        const val EXPECTED_SCORE = 5f
        val EXPECTED_REASON = ActivityDailyReason.Cycling.WindPickingUp
    }
}

class CyclingDayScorerTest {

    private val scorer = CyclingDayScorer()

    @Test
    fun `given known day conditions, when score, then return expected score and reason`() {
        val cases = mapOf(
            buildDailyForecast(
                temperatureCelsius = CyclingDayScorerFixture.ComfortableAndCalm.TEMPERATURE_CELSIUS,
            ) to DayScore(
                score = CyclingDayScorerFixture.ComfortableAndCalm.EXPECTED_SCORE,
                reason = CyclingDayScorerFixture.ComfortableAndCalm.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = CyclingDayScorerFixture.Gusty.TEMPERATURE_CELSIUS,
                daytimeWindGustsMaxKph = CyclingDayScorerFixture.Gusty.DAYTIME_WIND_GUSTS_MAX_KPH,
            ) to DayScore(
                score = CyclingDayScorerFixture.Gusty.EXPECTED_SCORE,
                reason = CyclingDayScorerFixture.Gusty.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = CyclingDayScorerFixture.Rainy.TEMPERATURE_CELSIUS,
                precipitationMm = CyclingDayScorerFixture.Rainy.PRECIPITATION_MM,
            ) to DayScore(
                score = CyclingDayScorerFixture.Rainy.EXPECTED_SCORE,
                reason = CyclingDayScorerFixture.Rainy.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = CyclingDayScorerFixture.Cold.TEMPERATURE_CELSIUS,
            ) to DayScore(
                score = CyclingDayScorerFixture.Cold.EXPECTED_SCORE,
                reason = CyclingDayScorerFixture.Cold.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = CyclingDayScorerFixture.MildNeither.TEMPERATURE_CELSIUS,
            ) to DayScore(
                score = CyclingDayScorerFixture.MildNeither.EXPECTED_SCORE,
                reason = CyclingDayScorerFixture.MildNeither.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = CyclingDayScorerFixture.ColdGustyAndRainy.TEMPERATURE_CELSIUS,
                daytimeWindGustsMaxKph = CyclingDayScorerFixture.ColdGustyAndRainy.DAYTIME_WIND_GUSTS_MAX_KPH,
                precipitationMm = CyclingDayScorerFixture.ColdGustyAndRainy.PRECIPITATION_MM,
            ) to DayScore(
                score = CyclingDayScorerFixture.ColdGustyAndRainy.EXPECTED_SCORE,
                reason = CyclingDayScorerFixture.ColdGustyAndRainy.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = CyclingDayScorerFixture.PartiallyGusty.TEMPERATURE_CELSIUS,
                daytimeWindGustsMaxKph = CyclingDayScorerFixture.PartiallyGusty.DAYTIME_WIND_GUSTS_MAX_KPH,
            ) to DayScore(
                score = CyclingDayScorerFixture.PartiallyGusty.EXPECTED_SCORE,
                reason = CyclingDayScorerFixture.PartiallyGusty.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = CyclingDayScorerFixture.PartiallyCold.TEMPERATURE_CELSIUS,
            ) to DayScore(
                score = CyclingDayScorerFixture.PartiallyCold.EXPECTED_SCORE,
                reason = CyclingDayScorerFixture.PartiallyCold.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = CyclingDayScorerFixture.WindPickingUp.TEMPERATURE_CELSIUS,
                daytimeWindGustsMaxKph = CyclingDayScorerFixture.WindPickingUp.DAYTIME_WIND_GUSTS_MAX_KPH,
            ) to DayScore(
                score = CyclingDayScorerFixture.WindPickingUp.EXPECTED_SCORE,
                reason = CyclingDayScorerFixture.WindPickingUp.EXPECTED_REASON,
            ),
        )

        cases.forEach { (day, expected) ->
            assertEquals(day.maxTemperatureCelsius.toString(), expected, scorer.score(day))
        }
    }

    private fun buildDailyForecast(
        temperatureCelsius: Double,
        precipitationMm: Double = CyclingDayScorerFixture.DEFAULT_PRECIPITATION_MM,
        daytimeWindGustsMaxKph: Double = CyclingDayScorerFixture.DEFAULT_DAYTIME_WIND_GUSTS_MAX_KPH,
    ) = DailyForecast(
        date = CyclingDayScorerFixture.DATE,
        maxTemperatureCelsius = temperatureCelsius,
        minTemperatureCelsius = temperatureCelsius,
        precipitationSumMm = precipitationMm,
        precipitationProbabilityMaxPercent = CyclingDayScorerFixture.PRECIPITATION_PROBABILITY_PERCENT,
        snowfallSumCm = CyclingDayScorerFixture.SNOWFALL_SUM_CM,
        windSpeedMaxKph = CyclingDayScorerFixture.DEFAULT_WIND_SPEED_KPH,
        windGustsMaxKph = CyclingDayScorerFixture.WIND_GUSTS_MAX_KPH,
        uvIndexMax = CyclingDayScorerFixture.UV_INDEX_MAX,
        daylightDurationHours = CyclingDayScorerFixture.DAYLIGHT_DURATION_HOURS,
        nightCloudCoverPercent = CyclingDayScorerFixture.NIGHT_CLOUD_COVER_PERCENT,
        dawnDuskWindSpeedKph = CyclingDayScorerFixture.DAWN_DUSK_WIND_SPEED_KPH,
        dawnDuskPrecipitationProbabilityPercent = CyclingDayScorerFixture.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
        daytimeWindSpeedMaxKph = CyclingDayScorerFixture.DEFAULT_WIND_SPEED_KPH,
        daytimeWindGustsMaxKph = daytimeWindGustsMaxKph,
        condition = CyclingDayScorerFixture.CONDITION,
    )
}
