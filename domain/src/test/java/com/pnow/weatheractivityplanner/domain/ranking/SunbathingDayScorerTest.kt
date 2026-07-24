package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Test

private object SunbathingDayScorerFixture {

    const val DATE = "2026-06-15"
    const val DEFAULT_PRECIPITATION_MM = 0.0
    const val DEFAULT_DAYTIME_WIND_SPEED_MAX_KPH = 5.0
    const val WIND_SPEED_MAX_KPH = 10.0
    const val PRECIPITATION_PROBABILITY_PERCENT = 0
    const val SNOWFALL_SUM_CM = 0.0
    const val WIND_GUSTS_KPH = 10.0
    const val DAYLIGHT_DURATION_HOURS = 12.0
    const val NIGHT_CLOUD_COVER_PERCENT = 50.0
    const val DAWN_DUSK_WIND_SPEED_KPH = 15.0
    const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 30.0
    const val DAYTIME_WIND_GUSTS_MAX_KPH = 25.0
    val CONDITION = WeatherCondition.Clear

    object WarmAndSunny {

        const val TEMPERATURE_CELSIUS = 26.0
        const val UV_INDEX_MAX = 8.0
        const val EXPECTED_SCORE = 80f
        val EXPECTED_REASON = ActivityDailyReason.Sunbathing.WarmAndSunny
    }

    object Windy {

        const val TEMPERATURE_CELSIUS = 26.0
        const val UV_INDEX_MAX = 8.0
        const val DAYTIME_WIND_SPEED_MAX_KPH = 30.0
        const val EXPECTED_SCORE = 50f
        val EXPECTED_REASON = ActivityDailyReason.Sunbathing.Windy
    }

    object RainyWindyAndCold {

        const val TEMPERATURE_CELSIUS = 15.0
        const val UV_INDEX_MAX = 3.0
        const val DAYTIME_WIND_SPEED_MAX_KPH = 30.0
        const val PRECIPITATION_MM = 5.0
        const val EXPECTED_SCORE = 0f
        val EXPECTED_REASON = ActivityDailyReason.Sunbathing.Rain
    }

    object SunnyOnly {

        const val TEMPERATURE_CELSIUS = 18.0
        const val UV_INDEX_MAX = 8.0
        const val EXPECTED_SCORE = 45f
        val EXPECTED_REASON = ActivityDailyReason.Sunbathing.SunnyOnly
    }

    object WarmOnly {

        const val TEMPERATURE_CELSIUS = 26.0
        const val UV_INDEX_MAX = 3.0
        const val EXPECTED_SCORE = 45f
        val EXPECTED_REASON = ActivityDailyReason.Sunbathing.WarmOnly
    }

    object None {

        const val TEMPERATURE_CELSIUS = 15.0
        const val UV_INDEX_MAX = 3.0
        const val EXPECTED_SCORE = 10f
        val EXPECTED_REASON = ActivityDailyReason.Sunbathing.None
    }

    object PartiallyWarm {

        // 50% of the way through the warm ramp (18-24°C), so only half the warm bonus applies.
        const val TEMPERATURE_CELSIUS = 21.0
        const val UV_INDEX_MAX = 3.0
        const val EXPECTED_SCORE = 27.5f
        val EXPECTED_REASON = ActivityDailyReason.Sunbathing.WarmingUp
    }

    object PartiallyWindy {

        // 25% of the way through the windy ramp (15-25 kph), so only a quarter of the windy penalty applies.
        const val TEMPERATURE_CELSIUS = 15.0
        const val UV_INDEX_MAX = 3.0
        const val DAYTIME_WIND_SPEED_MAX_KPH = 17.5
        const val EXPECTED_SCORE = 2.5f
        val EXPECTED_REASON = ActivityDailyReason.Sunbathing.None
    }
}

class SunbathingDayScorerTest {

    private val scorer = SunbathingDayScorer()

    @Test
    fun `given known day conditions, when score, then return expected score and reason`() {
        val cases = mapOf(
            buildDailyForecast(
                temperatureCelsius = SunbathingDayScorerFixture.WarmAndSunny.TEMPERATURE_CELSIUS,
                uvIndexMax = SunbathingDayScorerFixture.WarmAndSunny.UV_INDEX_MAX,
            ) to DayScore(
                score = SunbathingDayScorerFixture.WarmAndSunny.EXPECTED_SCORE,
                reason = SunbathingDayScorerFixture.WarmAndSunny.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SunbathingDayScorerFixture.Windy.TEMPERATURE_CELSIUS,
                uvIndexMax = SunbathingDayScorerFixture.Windy.UV_INDEX_MAX,
                daytimeWindSpeedMaxKph = SunbathingDayScorerFixture.Windy.DAYTIME_WIND_SPEED_MAX_KPH,
            ) to DayScore(
                score = SunbathingDayScorerFixture.Windy.EXPECTED_SCORE,
                reason = SunbathingDayScorerFixture.Windy.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SunbathingDayScorerFixture.RainyWindyAndCold.TEMPERATURE_CELSIUS,
                uvIndexMax = SunbathingDayScorerFixture.RainyWindyAndCold.UV_INDEX_MAX,
                daytimeWindSpeedMaxKph = SunbathingDayScorerFixture.RainyWindyAndCold.DAYTIME_WIND_SPEED_MAX_KPH,
                precipitationMm = SunbathingDayScorerFixture.RainyWindyAndCold.PRECIPITATION_MM,
            ) to DayScore(
                score = SunbathingDayScorerFixture.RainyWindyAndCold.EXPECTED_SCORE,
                reason = SunbathingDayScorerFixture.RainyWindyAndCold.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SunbathingDayScorerFixture.SunnyOnly.TEMPERATURE_CELSIUS,
                uvIndexMax = SunbathingDayScorerFixture.SunnyOnly.UV_INDEX_MAX,
            ) to DayScore(
                score = SunbathingDayScorerFixture.SunnyOnly.EXPECTED_SCORE,
                reason = SunbathingDayScorerFixture.SunnyOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SunbathingDayScorerFixture.WarmOnly.TEMPERATURE_CELSIUS,
                uvIndexMax = SunbathingDayScorerFixture.WarmOnly.UV_INDEX_MAX,
            ) to DayScore(
                score = SunbathingDayScorerFixture.WarmOnly.EXPECTED_SCORE,
                reason = SunbathingDayScorerFixture.WarmOnly.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SunbathingDayScorerFixture.None.TEMPERATURE_CELSIUS,
                uvIndexMax = SunbathingDayScorerFixture.None.UV_INDEX_MAX,
            ) to DayScore(
                score = SunbathingDayScorerFixture.None.EXPECTED_SCORE,
                reason = SunbathingDayScorerFixture.None.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SunbathingDayScorerFixture.PartiallyWarm.TEMPERATURE_CELSIUS,
                uvIndexMax = SunbathingDayScorerFixture.PartiallyWarm.UV_INDEX_MAX,
            ) to DayScore(
                score = SunbathingDayScorerFixture.PartiallyWarm.EXPECTED_SCORE,
                reason = SunbathingDayScorerFixture.PartiallyWarm.EXPECTED_REASON,
            ),
            buildDailyForecast(
                temperatureCelsius = SunbathingDayScorerFixture.PartiallyWindy.TEMPERATURE_CELSIUS,
                uvIndexMax = SunbathingDayScorerFixture.PartiallyWindy.UV_INDEX_MAX,
                daytimeWindSpeedMaxKph = SunbathingDayScorerFixture.PartiallyWindy.DAYTIME_WIND_SPEED_MAX_KPH,
            ) to DayScore(
                score = SunbathingDayScorerFixture.PartiallyWindy.EXPECTED_SCORE,
                reason = SunbathingDayScorerFixture.PartiallyWindy.EXPECTED_REASON,
            ),
        )

        cases.forEach { (day, expected) ->
            assertEquals(day.uvIndexMax.toString(), expected, scorer.score(day))
        }
    }

    private fun buildDailyForecast(
        temperatureCelsius: Double,
        uvIndexMax: Double,
        daytimeWindSpeedMaxKph: Double = SunbathingDayScorerFixture.DEFAULT_DAYTIME_WIND_SPEED_MAX_KPH,
        precipitationMm: Double = SunbathingDayScorerFixture.DEFAULT_PRECIPITATION_MM,
    ) = DailyForecast(
        date = SunbathingDayScorerFixture.DATE,
        maxTemperatureCelsius = temperatureCelsius,
        minTemperatureCelsius = temperatureCelsius,
        precipitationSumMm = precipitationMm,
        precipitationProbabilityMaxPercent = SunbathingDayScorerFixture.PRECIPITATION_PROBABILITY_PERCENT,
        snowfallSumCm = SunbathingDayScorerFixture.SNOWFALL_SUM_CM,
        windSpeedMaxKph = SunbathingDayScorerFixture.WIND_SPEED_MAX_KPH,
        windGustsMaxKph = SunbathingDayScorerFixture.WIND_GUSTS_KPH,
        uvIndexMax = uvIndexMax,
        daylightDurationHours = SunbathingDayScorerFixture.DAYLIGHT_DURATION_HOURS,
        nightCloudCoverPercent = SunbathingDayScorerFixture.NIGHT_CLOUD_COVER_PERCENT,
        dawnDuskWindSpeedKph = SunbathingDayScorerFixture.DAWN_DUSK_WIND_SPEED_KPH,
        dawnDuskPrecipitationProbabilityPercent = SunbathingDayScorerFixture.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
        daytimeWindSpeedMaxKph = daytimeWindSpeedMaxKph,
        daytimeWindGustsMaxKph = SunbathingDayScorerFixture.DAYTIME_WIND_GUSTS_MAX_KPH,
        condition = SunbathingDayScorerFixture.CONDITION,
    )
}
