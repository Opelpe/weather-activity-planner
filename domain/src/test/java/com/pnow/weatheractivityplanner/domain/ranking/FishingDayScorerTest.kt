package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Test

private object FishingDayScorerFixture {

    const val DATE = "2026-06-15"
    const val TEMPERATURE_CELSIUS = 18.0
    const val DEFAULT_PRECIPITATION_MM = 0.0
    const val DEFAULT_WIND_SPEED_MAX_KPH = 20.0
    const val DEFAULT_PRECIPITATION_PROBABILITY_MAX_PERCENT = 30
    const val SNOWFALL_SUM_CM = 0.0
    const val WIND_GUSTS_KPH = 10.0
    const val UV_INDEX_MAX = 5.0
    const val DAYLIGHT_DURATION_HOURS = 12.0
    const val NIGHT_CLOUD_COVER_PERCENT = 50.0
    const val DAYTIME_WIND_SPEED_MAX_KPH = 15.0
    const val DAYTIME_WIND_GUSTS_MAX_KPH = 25.0
    val DEFAULT_CONDITION = WeatherCondition.PartlyCloudy

    object CalmAndDry {

        const val DAWN_DUSK_WIND_SPEED_KPH = 5.0
        const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 10.0
        const val EXPECTED_SCORE = 80f
        val EXPECTED_REASON = ActivityDailyReason.Fishing.CalmAndDry
    }

    object Thunderstorm {

        const val DAWN_DUSK_WIND_SPEED_KPH = 5.0
        const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 10.0
        val CONDITION = WeatherCondition.Thunderstorm
        const val EXPECTED_SCORE = 20f
        val EXPECTED_REASON = ActivityDailyReason.Fishing.Thunderstorm
    }

    object Windy {

        const val DAWN_DUSK_WIND_SPEED_KPH = 35.0
        const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 10.0
        const val EXPECTED_SCORE = 15f
        val EXPECTED_REASON = ActivityDailyReason.Fishing.Windy
    }

    object CalmOnly {

        const val DAWN_DUSK_WIND_SPEED_KPH = 5.0
        const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 80.0
        const val EXPECTED_SCORE = 50f
        val EXPECTED_REASON = ActivityDailyReason.Fishing.CalmOnly
    }

    object DryOnly {

        const val DAWN_DUSK_WIND_SPEED_KPH = 20.0
        const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 10.0
        const val EXPECTED_SCORE = 50f
        val EXPECTED_REASON = ActivityDailyReason.Fishing.DryOnly
    }

    object None {

        const val DAWN_DUSK_WIND_SPEED_KPH = 20.0
        const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 80.0
        const val EXPECTED_SCORE = 20f
        val EXPECTED_REASON = ActivityDailyReason.Fishing.None
    }

    object PartiallyCalm {

        // 50% of the way through the calm ramp (20-10 kph), so only half the calm bonus applies.
        const val DAWN_DUSK_WIND_SPEED_KPH = 15.0
        const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 10.0
        const val EXPECTED_SCORE = 65f
        val EXPECTED_REASON = ActivityDailyReason.Fishing.DryOnly
    }

    object PartiallyWindy {

        // 50% of the way through the windy ramp (20-30 kph), so only half the windy penalty applies.
        const val DAWN_DUSK_WIND_SPEED_KPH = 25.0
        const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 80.0
        const val EXPECTED_SCORE = 2.5f
        val EXPECTED_REASON = ActivityDailyReason.Fishing.None
    }

    object WatersCalming {

        const val DAWN_DUSK_WIND_SPEED_KPH = 15.0
        const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 80.0
        const val EXPECTED_SCORE = 35f
        val EXPECTED_REASON = ActivityDailyReason.Fishing.WatersCalming
    }
}

class FishingDayScorerTest {

    private val scorer = FishingDayScorer()

    @Test
    fun `given known day conditions, when score, then return expected score and reason`() {
        val cases = mapOf(
            buildDailyForecast(
                dawnDuskWindSpeedKph = FishingDayScorerFixture.CalmAndDry.DAWN_DUSK_WIND_SPEED_KPH,
                dawnDuskPrecipitationProbabilityPercent = FishingDayScorerFixture.CalmAndDry.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
            ) to DayScore(
                score = FishingDayScorerFixture.CalmAndDry.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.CalmAndDry.EXPECTED_REASON,
            ),
            buildDailyForecast(
                dawnDuskWindSpeedKph = FishingDayScorerFixture.Thunderstorm.DAWN_DUSK_WIND_SPEED_KPH,
                dawnDuskPrecipitationProbabilityPercent = FishingDayScorerFixture.Thunderstorm.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
                condition = FishingDayScorerFixture.Thunderstorm.CONDITION,
            ) to DayScore(
                score = FishingDayScorerFixture.Thunderstorm.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.Thunderstorm.EXPECTED_REASON,
            ),
            buildDailyForecast(
                dawnDuskWindSpeedKph = FishingDayScorerFixture.Windy.DAWN_DUSK_WIND_SPEED_KPH,
                dawnDuskPrecipitationProbabilityPercent = FishingDayScorerFixture.Windy.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
            ) to DayScore(
                score = FishingDayScorerFixture.Windy.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.Windy.EXPECTED_REASON,
            ),
            buildDailyForecast(
                dawnDuskWindSpeedKph = FishingDayScorerFixture.CalmOnly.DAWN_DUSK_WIND_SPEED_KPH,
                dawnDuskPrecipitationProbabilityPercent = FishingDayScorerFixture.CalmOnly.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
            ) to DayScore(
                score = FishingDayScorerFixture.CalmOnly.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.CalmOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                dawnDuskWindSpeedKph = FishingDayScorerFixture.DryOnly.DAWN_DUSK_WIND_SPEED_KPH,
                dawnDuskPrecipitationProbabilityPercent = FishingDayScorerFixture.DryOnly.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
            ) to DayScore(
                score = FishingDayScorerFixture.DryOnly.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.DryOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                dawnDuskWindSpeedKph = FishingDayScorerFixture.None.DAWN_DUSK_WIND_SPEED_KPH,
                dawnDuskPrecipitationProbabilityPercent = FishingDayScorerFixture.None.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
            ) to DayScore(
                score = FishingDayScorerFixture.None.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.None.EXPECTED_REASON,
            ),
            buildDailyForecast(
                dawnDuskWindSpeedKph = FishingDayScorerFixture.PartiallyCalm.DAWN_DUSK_WIND_SPEED_KPH,
                dawnDuskPrecipitationProbabilityPercent = FishingDayScorerFixture.PartiallyCalm.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
            ) to DayScore(
                score = FishingDayScorerFixture.PartiallyCalm.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.PartiallyCalm.EXPECTED_REASON,
            ),
            buildDailyForecast(
                dawnDuskWindSpeedKph = FishingDayScorerFixture.PartiallyWindy.DAWN_DUSK_WIND_SPEED_KPH,
                dawnDuskPrecipitationProbabilityPercent = FishingDayScorerFixture.PartiallyWindy.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
            ) to DayScore(
                score = FishingDayScorerFixture.PartiallyWindy.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.PartiallyWindy.EXPECTED_REASON,
            ),
            buildDailyForecast(
                dawnDuskWindSpeedKph = FishingDayScorerFixture.WatersCalming.DAWN_DUSK_WIND_SPEED_KPH,
                dawnDuskPrecipitationProbabilityPercent = FishingDayScorerFixture.WatersCalming.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
            ) to DayScore(
                score = FishingDayScorerFixture.WatersCalming.EXPECTED_SCORE,
                reason = FishingDayScorerFixture.WatersCalming.EXPECTED_REASON,
            ),
        )

        cases.forEach { (day, expected) ->
            assertEquals(
                "wind=${day.dawnDuskWindSpeedKph} precipProb=${day.dawnDuskPrecipitationProbabilityPercent} ${day.condition}",
                expected,
                scorer.score(day),
            )
        }
    }

    private fun buildDailyForecast(
        dawnDuskWindSpeedKph: Double,
        dawnDuskPrecipitationProbabilityPercent: Double,
        condition: WeatherCondition = FishingDayScorerFixture.DEFAULT_CONDITION,
    ) = DailyForecast(
        date = FishingDayScorerFixture.DATE,
        maxTemperatureCelsius = FishingDayScorerFixture.TEMPERATURE_CELSIUS,
        minTemperatureCelsius = FishingDayScorerFixture.TEMPERATURE_CELSIUS,
        precipitationSumMm = FishingDayScorerFixture.DEFAULT_PRECIPITATION_MM,
        precipitationProbabilityMaxPercent = FishingDayScorerFixture.DEFAULT_PRECIPITATION_PROBABILITY_MAX_PERCENT,
        snowfallSumCm = FishingDayScorerFixture.SNOWFALL_SUM_CM,
        windSpeedMaxKph = FishingDayScorerFixture.DEFAULT_WIND_SPEED_MAX_KPH,
        windGustsMaxKph = FishingDayScorerFixture.WIND_GUSTS_KPH,
        uvIndexMax = FishingDayScorerFixture.UV_INDEX_MAX,
        daylightDurationHours = FishingDayScorerFixture.DAYLIGHT_DURATION_HOURS,
        nightCloudCoverPercent = FishingDayScorerFixture.NIGHT_CLOUD_COVER_PERCENT,
        dawnDuskWindSpeedKph = dawnDuskWindSpeedKph,
        dawnDuskPrecipitationProbabilityPercent = dawnDuskPrecipitationProbabilityPercent,
        daytimeWindSpeedMaxKph = FishingDayScorerFixture.DAYTIME_WIND_SPEED_MAX_KPH,
        daytimeWindGustsMaxKph = FishingDayScorerFixture.DAYTIME_WIND_GUSTS_MAX_KPH,
        condition = condition,
    )
}
