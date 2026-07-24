package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Test

private object SurfingDayScorerFixture {

    const val DATE = "2026-06-15"
    const val PRECIPITATION_MM = 0.0
    const val DEFAULT_DAYTIME_WIND_SPEED_MAX_KPH = 5.0
    const val WIND_SPEED_MAX_KPH = 10.0
    const val PRECIPITATION_PROBABILITY_PERCENT = 0
    const val SNOWFALL_SUM_CM = 0.0
    const val WIND_GUSTS_KPH = 30.0
    const val UV_INDEX_MAX = 5.0
    const val DAYLIGHT_DURATION_HOURS = 12.0
    const val NIGHT_CLOUD_COVER_PERCENT = 50.0
    const val DAWN_DUSK_WIND_SPEED_KPH = 15.0
    const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 30.0

    object WarmAndWindy {

        const val TEMPERATURE_CELSIUS = 24.0
        const val DAYTIME_WIND_SPEED_MAX_KPH = 25.0
        val CONDITION = WeatherCondition.Overcast
        const val EXPECTED_SCORE = 80f
        val EXPECTED_REASON = ActivityDailyReason.Surfing.WarmAndWindy
    }

    object Thunderstorm {

        const val TEMPERATURE_CELSIUS = 18.0
        val CONDITION = WeatherCondition.Thunderstorm
        const val EXPECTED_SCORE = 0f
        val EXPECTED_REASON = ActivityDailyReason.Surfing.Thunderstorm
    }

    object WarmAndRainy {

        const val TEMPERATURE_CELSIUS = 24.0
        const val PRECIPITATION_MM = 5.0
        val CONDITION = WeatherCondition.HeavyRain
        const val EXPECTED_SCORE = 20f
        val EXPECTED_REASON = ActivityDailyReason.Surfing.Rain
    }

    object WindyOnly {

        const val TEMPERATURE_CELSIUS = 10.0
        const val DAYTIME_WIND_SPEED_MAX_KPH = 20.0
        val CONDITION = WeatherCondition.Overcast
        const val EXPECTED_SCORE = 45f
        val EXPECTED_REASON = ActivityDailyReason.Surfing.WindyOnly
    }

    object WarmOnly {

        const val TEMPERATURE_CELSIUS = 20.0
        val CONDITION = WeatherCondition.Clear
        const val EXPECTED_SCORE = 45f
        val EXPECTED_REASON = ActivityDailyReason.Surfing.WarmOnly
    }

    object ColdAndCalm {

        const val TEMPERATURE_CELSIUS = 0.0
        val CONDITION = WeatherCondition.Clear
        const val EXPECTED_SCORE = 0f
        val EXPECTED_REASON = ActivityDailyReason.Surfing.None
    }

    object PartiallyWarm {

        // 50% of the way through the warm ramp (10-20°C), so only half the warm bonus applies.
        const val TEMPERATURE_CELSIUS = 15.0
        val CONDITION = WeatherCondition.Overcast
        const val EXPECTED_SCORE = 27.5f
        val EXPECTED_REASON = ActivityDailyReason.Surfing.BuildingConditions
    }

    object PartiallyGusty {

        // 50% of the way through the windy ramp (5-15 kph), so only half the wind bonus applies.
        const val TEMPERATURE_CELSIUS = 10.0
        const val DAYTIME_WIND_SPEED_MAX_KPH = 10.0
        val CONDITION = WeatherCondition.Overcast
        const val EXPECTED_SCORE = 27.5f
        val EXPECTED_REASON = ActivityDailyReason.Surfing.BuildingConditions
    }
}

class SurfingDayScorerTest {

    private val scorer = SurfingDayScorer()

    @Test
    fun `given known day conditions, when score, then return expected score and reason`() {
        val cases = mapOf(
            buildDailyForecast(
                temperatureCelsius = SurfingDayScorerFixture.WarmAndWindy.TEMPERATURE_CELSIUS,
                daytimeWindSpeedMaxKph = SurfingDayScorerFixture.WarmAndWindy.DAYTIME_WIND_SPEED_MAX_KPH,
                condition = SurfingDayScorerFixture.WarmAndWindy.CONDITION,
            ) to DayScore(
                score = SurfingDayScorerFixture.WarmAndWindy.EXPECTED_SCORE,
                reason = SurfingDayScorerFixture.WarmAndWindy.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SurfingDayScorerFixture.Thunderstorm.TEMPERATURE_CELSIUS,
                condition = SurfingDayScorerFixture.Thunderstorm.CONDITION,
            ) to DayScore(
                score = SurfingDayScorerFixture.Thunderstorm.EXPECTED_SCORE,
                reason = SurfingDayScorerFixture.Thunderstorm.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SurfingDayScorerFixture.WarmAndRainy.TEMPERATURE_CELSIUS,
                condition = SurfingDayScorerFixture.WarmAndRainy.CONDITION,
                precipitationMm = SurfingDayScorerFixture.WarmAndRainy.PRECIPITATION_MM,
            ) to DayScore(
                score = SurfingDayScorerFixture.WarmAndRainy.EXPECTED_SCORE,
                reason = SurfingDayScorerFixture.WarmAndRainy.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SurfingDayScorerFixture.WindyOnly.TEMPERATURE_CELSIUS,
                daytimeWindSpeedMaxKph = SurfingDayScorerFixture.WindyOnly.DAYTIME_WIND_SPEED_MAX_KPH,
                condition = SurfingDayScorerFixture.WindyOnly.CONDITION,
            ) to DayScore(
                score = SurfingDayScorerFixture.WindyOnly.EXPECTED_SCORE,
                reason = SurfingDayScorerFixture.WindyOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SurfingDayScorerFixture.WarmOnly.TEMPERATURE_CELSIUS,
                condition = SurfingDayScorerFixture.WarmOnly.CONDITION,
            ) to DayScore(
                score = SurfingDayScorerFixture.WarmOnly.EXPECTED_SCORE,
                reason = SurfingDayScorerFixture.WarmOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SurfingDayScorerFixture.ColdAndCalm.TEMPERATURE_CELSIUS,
                condition = SurfingDayScorerFixture.ColdAndCalm.CONDITION,
            ) to DayScore(
                score = SurfingDayScorerFixture.ColdAndCalm.EXPECTED_SCORE,
                reason = SurfingDayScorerFixture.ColdAndCalm.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SurfingDayScorerFixture.PartiallyWarm.TEMPERATURE_CELSIUS,
                condition = SurfingDayScorerFixture.PartiallyWarm.CONDITION,
            ) to DayScore(
                score = SurfingDayScorerFixture.PartiallyWarm.EXPECTED_SCORE,
                reason = SurfingDayScorerFixture.PartiallyWarm.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SurfingDayScorerFixture.PartiallyGusty.TEMPERATURE_CELSIUS,
                daytimeWindSpeedMaxKph = SurfingDayScorerFixture.PartiallyGusty.DAYTIME_WIND_SPEED_MAX_KPH,
                condition = SurfingDayScorerFixture.PartiallyGusty.CONDITION,
            ) to DayScore(
                score = SurfingDayScorerFixture.PartiallyGusty.EXPECTED_SCORE,
                reason = SurfingDayScorerFixture.PartiallyGusty.EXPECTED_REASON,
            ),
        )

        cases.forEach { (day, expected) ->
            assertEquals(day.condition.toString(), expected, scorer.score(day))
        }
    }

    private fun buildDailyForecast(
        temperatureCelsius: Double,
        condition: WeatherCondition,
        daytimeWindSpeedMaxKph: Double = SurfingDayScorerFixture.DEFAULT_DAYTIME_WIND_SPEED_MAX_KPH,
        precipitationMm: Double = SurfingDayScorerFixture.PRECIPITATION_MM,
    ) = DailyForecast(
        date = SurfingDayScorerFixture.DATE,
        maxTemperatureCelsius = temperatureCelsius,
        minTemperatureCelsius = temperatureCelsius,
        precipitationSumMm = precipitationMm,
        precipitationProbabilityMaxPercent = SurfingDayScorerFixture.PRECIPITATION_PROBABILITY_PERCENT,
        snowfallSumCm = SurfingDayScorerFixture.SNOWFALL_SUM_CM,
        windSpeedMaxKph = SurfingDayScorerFixture.WIND_SPEED_MAX_KPH,
        windGustsMaxKph = SurfingDayScorerFixture.WIND_GUSTS_KPH,
        uvIndexMax = SurfingDayScorerFixture.UV_INDEX_MAX,
        daylightDurationHours = SurfingDayScorerFixture.DAYLIGHT_DURATION_HOURS,
        nightCloudCoverPercent = SurfingDayScorerFixture.NIGHT_CLOUD_COVER_PERCENT,
        dawnDuskWindSpeedKph = SurfingDayScorerFixture.DAWN_DUSK_WIND_SPEED_KPH,
        dawnDuskPrecipitationProbabilityPercent = SurfingDayScorerFixture.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
        daytimeWindSpeedMaxKph = daytimeWindSpeedMaxKph,
        daytimeWindGustsMaxKph = SurfingDayScorerFixture.WIND_GUSTS_KPH,
        condition = condition,
    )
}
