package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
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
    const val CLEAR_NIGHT_CLOUD_COVER_PERCENT = 10.0
    const val CLOUDY_NIGHT_CLOUD_COVER_PERCENT = 70.0
    const val DAWN_DUSK_WIND_SPEED_KPH = 15.0
    const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 30.0
    const val DAYTIME_WIND_SPEED_MAX_KPH = 15.0
    const val DAYTIME_WIND_GUSTS_MAX_KPH = 25.0

    object ClearAndLongNight {

        val CONDITION = WeatherCondition.Clear
        const val DAYLIGHT_DURATION_HOURS = LONG_NIGHT_DAYLIGHT_DURATION_HOURS
        const val NIGHT_CLOUD_COVER_PERCENT = CLEAR_NIGHT_CLOUD_COVER_PERCENT
        const val EXPECTED_SCORE = 90f
        val EXPECTED_REASON = ActivityDailyReason.Stargazing.ClearAndLongNight
    }

    object Rain {

        val CONDITION = WeatherCondition.HeavyRain
        const val DAYLIGHT_DURATION_HOURS = SHORT_DAYLIGHT_DURATION_HOURS
        const val NIGHT_CLOUD_COVER_PERCENT = CLOUDY_NIGHT_CLOUD_COVER_PERCENT
        const val PRECIPITATION_MM = 5.0
        const val EXPECTED_SCORE = 0f
        val EXPECTED_REASON = ActivityDailyReason.Stargazing.Rain
    }

    object Fog {

        val CONDITION = WeatherCondition.Fog
        const val DAYLIGHT_DURATION_HOURS = SHORT_DAYLIGHT_DURATION_HOURS
        const val NIGHT_CLOUD_COVER_PERCENT = CLOUDY_NIGHT_CLOUD_COVER_PERCENT
        const val EXPECTED_SCORE = 0f
        val EXPECTED_REASON = ActivityDailyReason.Stargazing.Fog
    }

    object ClearOnly {

        val CONDITION = WeatherCondition.Clear
        const val DAYLIGHT_DURATION_HOURS = SHORT_DAYLIGHT_DURATION_HOURS
        const val NIGHT_CLOUD_COVER_PERCENT = CLEAR_NIGHT_CLOUD_COVER_PERCENT
        const val EXPECTED_SCORE = 65f
        val EXPECTED_REASON = ActivityDailyReason.Stargazing.ClearOnly
    }

    object LongNightOnly {

        val CONDITION = WeatherCondition.Overcast
        const val DAYLIGHT_DURATION_HOURS = LONG_NIGHT_DAYLIGHT_DURATION_HOURS
        const val NIGHT_CLOUD_COVER_PERCENT = CLOUDY_NIGHT_CLOUD_COVER_PERCENT
        const val EXPECTED_SCORE = 45f
        val EXPECTED_REASON = ActivityDailyReason.Stargazing.LongNightOnly
    }

    object None {

        val CONDITION = WeatherCondition.Overcast
        const val DAYLIGHT_DURATION_HOURS = SHORT_DAYLIGHT_DURATION_HOURS
        const val NIGHT_CLOUD_COVER_PERCENT = CLOUDY_NIGHT_CLOUD_COVER_PERCENT
        const val EXPECTED_SCORE = 20f
        val EXPECTED_REASON = ActivityDailyReason.Stargazing.None
    }

    object PartiallyLongNight {

        // 50% of the way through the long-night ramp (10-8 hours), so only half the long-night bonus applies;
        // cloud cover is fully overcast so the clear-sky bonus stays at zero.
        val CONDITION = WeatherCondition.Overcast
        const val DAYLIGHT_DURATION_HOURS = 9.0
        const val NIGHT_CLOUD_COVER_PERCENT = CLOUDY_NIGHT_CLOUD_COVER_PERCENT
        const val EXPECTED_SCORE = 32.5f
        val EXPECTED_REASON = ActivityDailyReason.Stargazing.LongNightOnly
    }

    object PartiallyRainy {

        // 50% of the way through the precipitation ramp (0.5-5.0mm), so only half the rain penalty applies.
        val CONDITION = WeatherCondition.Clear
        const val DAYLIGHT_DURATION_HOURS = SHORT_DAYLIGHT_DURATION_HOURS
        const val NIGHT_CLOUD_COVER_PERCENT = CLEAR_NIGHT_CLOUD_COVER_PERCENT
        const val PRECIPITATION_MM = 2.75
        const val EXPECTED_SCORE = 45f
        val EXPECTED_REASON = ActivityDailyReason.Stargazing.Rain
    }

    object PartiallyCloudyNight {

        // 50% of the way through the clear-sky ramp (70-20% cloud cover), so only half the clear-sky bonus applies.
        val CONDITION = WeatherCondition.PartlyCloudy
        const val DAYLIGHT_DURATION_HOURS = SHORT_DAYLIGHT_DURATION_HOURS
        const val NIGHT_CLOUD_COVER_PERCENT = 45.0
        const val EXPECTED_SCORE = 42.5f
        val EXPECTED_REASON = ActivityDailyReason.Stargazing.None
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
                nightCloudCoverPercent = StargazingDayScorerFixture.ClearAndLongNight.NIGHT_CLOUD_COVER_PERCENT,
            ) to DayScore(
                score = StargazingDayScorerFixture.ClearAndLongNight.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.ClearAndLongNight.EXPECTED_REASON,
            ),
            buildDailyForecast(
                condition = StargazingDayScorerFixture.Rain.CONDITION,
                daylightDurationHours = StargazingDayScorerFixture.Rain.DAYLIGHT_DURATION_HOURS,
                nightCloudCoverPercent = StargazingDayScorerFixture.Rain.NIGHT_CLOUD_COVER_PERCENT,
                precipitationMm = StargazingDayScorerFixture.Rain.PRECIPITATION_MM,
            ) to DayScore(
                score = StargazingDayScorerFixture.Rain.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.Rain.EXPECTED_REASON,
            ),
            buildDailyForecast(
                condition = StargazingDayScorerFixture.Fog.CONDITION,
                daylightDurationHours = StargazingDayScorerFixture.Fog.DAYLIGHT_DURATION_HOURS,
                nightCloudCoverPercent = StargazingDayScorerFixture.Fog.NIGHT_CLOUD_COVER_PERCENT,
            ) to DayScore(
                score = StargazingDayScorerFixture.Fog.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.Fog.EXPECTED_REASON,
            ),
            buildDailyForecast(
                condition = StargazingDayScorerFixture.ClearOnly.CONDITION,
                daylightDurationHours = StargazingDayScorerFixture.ClearOnly.DAYLIGHT_DURATION_HOURS,
                nightCloudCoverPercent = StargazingDayScorerFixture.ClearOnly.NIGHT_CLOUD_COVER_PERCENT,
            ) to DayScore(
                score = StargazingDayScorerFixture.ClearOnly.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.ClearOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                condition = StargazingDayScorerFixture.LongNightOnly.CONDITION,
                daylightDurationHours = StargazingDayScorerFixture.LongNightOnly.DAYLIGHT_DURATION_HOURS,
                nightCloudCoverPercent = StargazingDayScorerFixture.LongNightOnly.NIGHT_CLOUD_COVER_PERCENT,
            ) to DayScore(
                score = StargazingDayScorerFixture.LongNightOnly.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.LongNightOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                condition = StargazingDayScorerFixture.None.CONDITION,
                daylightDurationHours = StargazingDayScorerFixture.None.DAYLIGHT_DURATION_HOURS,
                nightCloudCoverPercent = StargazingDayScorerFixture.None.NIGHT_CLOUD_COVER_PERCENT,
            ) to DayScore(
                score = StargazingDayScorerFixture.None.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.None.EXPECTED_REASON,
            ),
            buildDailyForecast(
                condition = StargazingDayScorerFixture.PartiallyLongNight.CONDITION,
                daylightDurationHours = StargazingDayScorerFixture.PartiallyLongNight.DAYLIGHT_DURATION_HOURS,
                nightCloudCoverPercent = StargazingDayScorerFixture.PartiallyLongNight.NIGHT_CLOUD_COVER_PERCENT,
            ) to DayScore(
                score = StargazingDayScorerFixture.PartiallyLongNight.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.PartiallyLongNight.EXPECTED_REASON,
            ),
            buildDailyForecast(
                condition = StargazingDayScorerFixture.PartiallyRainy.CONDITION,
                daylightDurationHours = StargazingDayScorerFixture.PartiallyRainy.DAYLIGHT_DURATION_HOURS,
                nightCloudCoverPercent = StargazingDayScorerFixture.PartiallyRainy.NIGHT_CLOUD_COVER_PERCENT,
                precipitationMm = StargazingDayScorerFixture.PartiallyRainy.PRECIPITATION_MM,
            ) to DayScore(
                score = StargazingDayScorerFixture.PartiallyRainy.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.PartiallyRainy.EXPECTED_REASON,
            ),
            buildDailyForecast(
                condition = StargazingDayScorerFixture.PartiallyCloudyNight.CONDITION,
                daylightDurationHours = StargazingDayScorerFixture.PartiallyCloudyNight.DAYLIGHT_DURATION_HOURS,
                nightCloudCoverPercent = StargazingDayScorerFixture.PartiallyCloudyNight.NIGHT_CLOUD_COVER_PERCENT,
            ) to DayScore(
                score = StargazingDayScorerFixture.PartiallyCloudyNight.EXPECTED_SCORE,
                reason = StargazingDayScorerFixture.PartiallyCloudyNight.EXPECTED_REASON,
            ),
        )

        cases.forEach { (day, expected) ->
            assertEquals(
                "${day.condition} / ${day.daylightDurationHours}h / ${day.nightCloudCoverPercent}%",
                expected,
                scorer.score(day),
            )
        }
    }

    private fun buildDailyForecast(
        condition: WeatherCondition,
        daylightDurationHours: Double,
        nightCloudCoverPercent: Double,
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
        nightCloudCoverPercent = nightCloudCoverPercent,
        dawnDuskWindSpeedKph = StargazingDayScorerFixture.DAWN_DUSK_WIND_SPEED_KPH,
        dawnDuskPrecipitationProbabilityPercent = StargazingDayScorerFixture.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
        daytimeWindSpeedMaxKph = StargazingDayScorerFixture.DAYTIME_WIND_SPEED_MAX_KPH,
        daytimeWindGustsMaxKph = StargazingDayScorerFixture.DAYTIME_WIND_GUSTS_MAX_KPH,
        condition = condition,
    )
}
