package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
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
    const val NIGHT_CLOUD_COVER_PERCENT = 50.0
    const val DAWN_DUSK_WIND_SPEED_KPH = 15.0
    const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 30.0
    const val DAYTIME_WIND_SPEED_MAX_KPH = 15.0
    const val DAYTIME_WIND_GUSTS_MAX_KPH = 25.0

    object SnowAndFreezing {

        const val TEMPERATURE_CELSIUS = -5.0
        val CONDITION = WeatherCondition.HeavySnow
        const val EXPECTED_SCORE = 100f
        val EXPECTED_REASON = ActivityDailyReason.Skiing.SnowAndFreezing
    }

    object FreezingOnly {

        const val TEMPERATURE_CELSIUS = -10.0
        val CONDITION = WeatherCondition.Clear
        const val EXPECTED_SCORE = 45f
        val EXPECTED_REASON = ActivityDailyReason.Skiing.FreezingOnly
    }

    object SnowOnly {

        const val TEMPERATURE_CELSIUS = 0.0
        val CONDITION = WeatherCondition.LightSnow
        const val EXPECTED_SCORE = 80f
        val EXPECTED_REASON = ActivityDailyReason.Skiing.SnowOnly
    }

    object Rain {

        const val TEMPERATURE_CELSIUS = 5.0
        val CONDITION = WeatherCondition.HeavyRain
        const val EXPECTED_SCORE = 0f
        val EXPECTED_REASON = ActivityDailyReason.Skiing.Rain
    }

    object MildAndDry {

        const val TEMPERATURE_CELSIUS = 20.0
        val CONDITION = WeatherCondition.PartlyCloudy
        const val EXPECTED_SCORE = 0f
        val EXPECTED_REASON = ActivityDailyReason.Skiing.TooWarm
    }

    object PartiallyFreezing {

        // 50% of the way through the freezing ramp (0 to -5°C), so only half the freezing bonus applies.
        const val TEMPERATURE_CELSIUS = -2.5
        val CONDITION = WeatherCondition.Clear
        const val EXPECTED_SCORE = 27.5f
        val EXPECTED_REASON = ActivityDailyReason.Skiing.GettingColder
    }

    object PartiallyTooWarm {

        // 20% of the way through the too-warm ramp (10-20°C), so only a fifth of the too-warm penalty applies.
        const val TEMPERATURE_CELSIUS = 12.0
        val CONDITION = WeatherCondition.Clear
        const val EXPECTED_SCORE = 5f
        val EXPECTED_REASON = ActivityDailyReason.Skiing.TooWarm
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
            buildDailyForecast(
                temperatureCelsius = SkiingDayScorerFixture.PartiallyFreezing.TEMPERATURE_CELSIUS,
                condition = SkiingDayScorerFixture.PartiallyFreezing.CONDITION,
            ) to DayScore(
                score = SkiingDayScorerFixture.PartiallyFreezing.EXPECTED_SCORE,
                reason = SkiingDayScorerFixture.PartiallyFreezing.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SkiingDayScorerFixture.PartiallyTooWarm.TEMPERATURE_CELSIUS,
                condition = SkiingDayScorerFixture.PartiallyTooWarm.CONDITION,
            ) to DayScore(
                score = SkiingDayScorerFixture.PartiallyTooWarm.EXPECTED_SCORE,
                reason = SkiingDayScorerFixture.PartiallyTooWarm.EXPECTED_REASON,
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
        nightCloudCoverPercent = SkiingDayScorerFixture.NIGHT_CLOUD_COVER_PERCENT,
        dawnDuskWindSpeedKph = SkiingDayScorerFixture.DAWN_DUSK_WIND_SPEED_KPH,
        dawnDuskPrecipitationProbabilityPercent = SkiingDayScorerFixture.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
        daytimeWindSpeedMaxKph = SkiingDayScorerFixture.DAYTIME_WIND_SPEED_MAX_KPH,
        daytimeWindGustsMaxKph = SkiingDayScorerFixture.DAYTIME_WIND_GUSTS_MAX_KPH,
        condition = condition,
    )
}
