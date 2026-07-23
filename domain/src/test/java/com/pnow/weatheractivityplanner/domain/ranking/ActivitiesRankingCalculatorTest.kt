package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.Activities
import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.ActivityWeeklyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private object ActivityRankingCalculatorFixture {

    const val DEFAULT_DATE = "2026-06-15"
    const val DEFAULT_TEMPERATURE_CELSIUS = 20.0
    const val DEFAULT_PRECIPITATION_MM = 0.0
    const val DEFAULT_WIND_SPEED_KPH = 5.0
    const val DEFAULT_PRECIPITATION_PROBABILITY_PERCENT = 0
    const val DEFAULT_SNOWFALL_SUM_CM = 0.0
    const val DEFAULT_WIND_GUSTS_KPH = 10.0
    const val DEFAULT_UV_INDEX_MAX = 5.0
    const val DEFAULT_DAYLIGHT_DURATION_HOURS = 12.0
    const val DEFAULT_NIGHT_CLOUD_COVER_PERCENT = 50.0
    const val DEFAULT_DAWN_DUSK_WIND_SPEED_KPH = 15.0
    const val DEFAULT_DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 30.0
    const val DEFAULT_DAYTIME_WIND_SPEED_MAX_KPH = 15.0
    const val DEFAULT_DAYTIME_WIND_GUSTS_MAX_KPH = 25.0
    const val GENERIC_ACTIVITY_BONUS = 10f

    object SnowyAndFreezing {

        const val TEMPERATURE_CELSIUS = -5.0
        val CONDITION = WeatherCondition.HeavySnow
        const val EXPECTED_SKIING_SCORE = 100f
    }

    object SnowOnly {

        const val TEMPERATURE_CELSIUS = 0.0
        val CONDITION = WeatherCondition.LightSnow
        const val EXPECTED_SKIING_SCORE = 80f
    }

    object TiedFishingAndSurfing {

        const val TEMPERATURE_CELSIUS = 24.0
        const val DAYTIME_WIND_SPEED_MAX_KPH = 25.0
        const val DAWN_DUSK_WIND_SPEED_KPH = 5.0
        const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 10.0
        val CONDITION = WeatherCondition.Overcast
        const val EXPECTED_TIED_SCORE = 80f
    }

    object GenericActivityPromotion {

        const val TEMPERATURE_CELSIUS = 20.0
        val CONDITION = WeatherCondition.Overcast
    }

    object ClearAndComfortable {

        const val TEMPERATURE_CELSIUS = 22.0
        val CONDITION = WeatherCondition.Clear
    }

    object MildAndDry {

        const val TEMPERATURE_CELSIUS = 20.0
        val CONDITION = WeatherCondition.PartlyCloudy
        const val EXPECTED_SKIING_SCORE = 0f
    }

    object MixedSkiWeek {

        const val EXPECTED_WEIGHTED_AVERAGE_SKIING_SCORE =
            (SnowyAndFreezing.EXPECTED_SKIING_SCORE * 3f + SnowOnly.EXPECTED_SKIING_SCORE * 2f + MildAndDry.EXPECTED_SKIING_SCORE * 1f) / 6f

        val EXPECTED_SPECIFIC_REASON = ActivityDailyReason.Skiing.SnowOnly
    }

    object ConsistentSkiWeek {

        object Day1 {

            const val TEMPERATURE_CELSIUS = -1.0
            val CONDITION = WeatherCondition.Clear
            const val EXPECTED_SKIING_SCORE = 17f
        }

        object Day2 {

            const val TEMPERATURE_CELSIUS = -1.5
            val CONDITION = WeatherCondition.Clear
            const val EXPECTED_SKIING_SCORE = 20.5f
        }

        object Day3 {

            const val TEMPERATURE_CELSIUS = -2.0
            val CONDITION = WeatherCondition.Clear
            const val EXPECTED_SKIING_SCORE = 24f
        }

        const val EXPECTED_WEIGHTED_AVERAGE_SKIING_SCORE =
            (Day1.EXPECTED_SKIING_SCORE * 3f + Day2.EXPECTED_SKIING_SCORE * 2f + Day3.EXPECTED_SKIING_SCORE * 1f) / 6f
        val EXPECTED_REASON = ActivityWeeklyReason.CONSISTENTLY_TERRIBLE

        // Day2's raw score (20.5) is closest to the weighted average (~19.33), so its reason represents the week.
        val EXPECTED_SPECIFIC_REASON = ActivityDailyReason.Skiing.GettingColder
    }

    object ImprovingSkiWeek {

        object Day1 {

            const val TEMPERATURE_CELSIUS = 15.0
            val CONDITION = WeatherCondition.Clear
            const val EXPECTED_SKIING_SCORE = 0f
        }

        object Day2 {

            const val TEMPERATURE_CELSIUS = -1.0
            val CONDITION = WeatherCondition.Clear
            const val EXPECTED_SKIING_SCORE = 17f
        }

        object Day3 {

            const val TEMPERATURE_CELSIUS = -3.0
            val CONDITION = WeatherCondition.Clear
            const val EXPECTED_SKIING_SCORE = 31f
        }

        const val EXPECTED_WEIGHTED_AVERAGE_SKIING_SCORE =
            (Day1.EXPECTED_SKIING_SCORE * 3f + Day2.EXPECTED_SKIING_SCORE * 2f + Day3.EXPECTED_SKIING_SCORE * 1f) / 6f

        // For an improving trend, the last day's reason represents the week, not the closest-to-average day.
        val EXPECTED_SPECIFIC_REASON = ActivityDailyReason.Skiing.GettingColder
    }

    object DecliningSkiWeek {

        object Day1 {

            const val TEMPERATURE_CELSIUS = -3.0
            val CONDITION = WeatherCondition.Clear
            const val EXPECTED_SKIING_SCORE = 31f
        }

        object Day2 {

            const val TEMPERATURE_CELSIUS = -1.0
            val CONDITION = WeatherCondition.Clear
            const val EXPECTED_SKIING_SCORE = 17f
        }

        object Day3 {

            const val TEMPERATURE_CELSIUS = 15.0
            val CONDITION = WeatherCondition.Clear
            const val EXPECTED_SKIING_SCORE = 0f
        }

        const val EXPECTED_WEIGHTED_AVERAGE_SKIING_SCORE =
            (Day1.EXPECTED_SKIING_SCORE * 3f + Day2.EXPECTED_SKIING_SCORE * 2f + Day3.EXPECTED_SKIING_SCORE * 1f) / 6f

        // For a declining trend, the last day's reason represents the week, not the closest-to-average day.
        val EXPECTED_SPECIFIC_REASON = ActivityDailyReason.Skiing.TooWarm
    }
}

class ActivitiesRankingCalculatorTest {

    private val calculator = ActivitiesRankingCalculator(
        skiingDayScorer = SkiingDayScorer(),
        surfingDayScorer = SurfingDayScorer(),
        outdoorSightseeingDayScorer = OutdoorSightseeingDayScorer(),
        indoorSightseeingDayScorer = IndoorSightseeingDayScorer(),
        cyclingDayScorer = CyclingDayScorer(),
        sunbathingDayScorer = SunbathingDayScorer(),
        stargazingDayScorer = StargazingDayScorer(),
        fishingDayScorer = FishingDayScorer(),
    )

    @Test
    fun `given any forecast, when calculate, then returns all activities sorted by score descending`() {
        val daily = listOf(buildDailyForecast())

        val rankings = calculator.calculate(daily)

        assertEquals(Activities.entries.size, rankings.size)
        assertEquals(Activities.entries.toSet(), rankings.map { it.activity }.toSet())
        assertEquals(rankings.sortedByDescending { it.score }, rankings)
    }

    @Test
    fun `given an empty forecast, when calculate, then returns an empty list`() {
        val rankings = calculator.calculate(emptyList())

        assertTrue(rankings.isEmpty())
    }

    @Test
    fun `given forecast that ties fishing and surfing, when calculate, then tie is broken alphabetically by activity name`() {
        val daily = listOf(
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.TiedFishingAndSurfing.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.TiedFishingAndSurfing.CONDITION,
                daytimeWindSpeedMaxKph = ActivityRankingCalculatorFixture.TiedFishingAndSurfing.DAYTIME_WIND_SPEED_MAX_KPH,
                dawnDuskWindSpeedKph = ActivityRankingCalculatorFixture.TiedFishingAndSurfing.DAWN_DUSK_WIND_SPEED_KPH,
                dawnDuskPrecipitationProbabilityPercent =
                    ActivityRankingCalculatorFixture.TiedFishingAndSurfing.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
            ),
        )

        val rankings = calculator.calculate(daily)
        val surfing = rankings.first { it.activity == Activities.SURFING }
        val fishing = rankings.first { it.activity == Activities.FISHING }

        assertEquals(
            ActivityRankingCalculatorFixture.TiedFishingAndSurfing.EXPECTED_TIED_SCORE,
            surfing.score,
        )
        assertEquals(
            ActivityRankingCalculatorFixture.TiedFishingAndSurfing.EXPECTED_TIED_SCORE,
            fishing.score,
        )
        assertTrue(rankings.indexOf(fishing) < rankings.indexOf(surfing))
    }

    @Test
    fun `given any forecast, when calculate, then indoor and outdoor sightseeing scores are promoted but other activities are not`() {
        val day = buildDailyForecast(
            temperatureCelsius = ActivityRankingCalculatorFixture.GenericActivityPromotion.TEMPERATURE_CELSIUS,
            condition = ActivityRankingCalculatorFixture.GenericActivityPromotion.CONDITION,
        )
        val rawOutdoorScore = OutdoorSightseeingDayScorer().score(day).score
        val rawIndoorScore = IndoorSightseeingDayScorer().score(day).score
        val rawSkiingScore = SkiingDayScorer().score(day).score

        val rankings = calculator.calculate(listOf(day))

        assertEquals(
            rawOutdoorScore + ActivityRankingCalculatorFixture.GENERIC_ACTIVITY_BONUS,
            rankings.first { it.activity == Activities.OUTDOOR_SIGHTSEEING }.score,
        )
        assertEquals(
            rawIndoorScore + ActivityRankingCalculatorFixture.GENERIC_ACTIVITY_BONUS,
            rankings.first { it.activity == Activities.INDOOR_SIGHTSEEING }.score,
        )
        assertEquals(rawSkiingScore, rankings.first { it.activity == Activities.SKIING }.score)
    }

    @Test
    fun `given a forecast that scores 100 before promotion, when calculate, then the promoted score is capped at 100`() {
        val daily = listOf(
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.ClearAndComfortable.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.ClearAndComfortable.CONDITION,
            ),
        )

        val rankings = calculator.calculate(daily)

        assertEquals(100f, rankings.first { it.activity == Activities.OUTDOOR_SIGHTSEEING }.score)
    }

    @Test
    fun `given a week mixing great, good and poor skiing days, when calculate, then score is a soonest-weighted average and reason reflects the mixed week`() {
        val daily = listOf(
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.SnowyAndFreezing.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.SnowyAndFreezing.CONDITION,
            ),
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.SnowOnly.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.SnowOnly.CONDITION,
            ),
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.MildAndDry.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.MildAndDry.CONDITION,
            ),
        )

        val skiing = calculator.calculate(daily).first { it.activity == Activities.SKIING }

        assertEquals(
            ActivityRankingCalculatorFixture.MixedSkiWeek.EXPECTED_WEIGHTED_AVERAGE_SKIING_SCORE,
            skiing.score,
        )
        assertEquals(ActivityWeeklyReason.MIXED, skiing.weeklyReason)
        assertEquals(
            ActivityRankingCalculatorFixture.MixedSkiWeek.EXPECTED_SPECIFIC_REASON,
            skiing.dailyReason,
        )
    }

    @Test
    fun `given a week of consistently low-scoring skiing days, when calculate, then reason reflects the consistently terrible week`() {
        val daily = listOf(
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.ConsistentSkiWeek.Day1.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.ConsistentSkiWeek.Day1.CONDITION,
            ),
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.ConsistentSkiWeek.Day2.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.ConsistentSkiWeek.Day2.CONDITION,
            ),
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.ConsistentSkiWeek.Day3.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.ConsistentSkiWeek.Day3.CONDITION,
            ),
        )

        val skiing = calculator.calculate(daily).first { it.activity == Activities.SKIING }

        assertEquals(
            ActivityRankingCalculatorFixture.ConsistentSkiWeek.EXPECTED_WEIGHTED_AVERAGE_SKIING_SCORE,
            skiing.score,
        )
        assertEquals(
            ActivityRankingCalculatorFixture.ConsistentSkiWeek.EXPECTED_REASON,
            skiing.weeklyReason,
        )
        assertEquals(
            ActivityRankingCalculatorFixture.ConsistentSkiWeek.EXPECTED_SPECIFIC_REASON,
            skiing.dailyReason,
        )
    }

    @Test
    fun `given a week of improving skiing days, when calculate, then reason reflects the improving trend`() {
        val daily = listOf(
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.ImprovingSkiWeek.Day1.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.ImprovingSkiWeek.Day1.CONDITION,
            ),
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.ImprovingSkiWeek.Day2.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.ImprovingSkiWeek.Day2.CONDITION,
            ),
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.ImprovingSkiWeek.Day3.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.ImprovingSkiWeek.Day3.CONDITION,
            ),
        )

        val skiing = calculator.calculate(daily).first { it.activity == Activities.SKIING }

        assertEquals(
            ActivityRankingCalculatorFixture.ImprovingSkiWeek.EXPECTED_WEIGHTED_AVERAGE_SKIING_SCORE,
            skiing.score,
        )
        assertEquals(ActivityWeeklyReason.IMPROVING, skiing.weeklyReason)
        assertEquals(
            ActivityRankingCalculatorFixture.ImprovingSkiWeek.EXPECTED_SPECIFIC_REASON,
            skiing.dailyReason,
        )
    }

    @Test
    fun `given a week of declining skiing days, when calculate, then reason reflects the declining trend`() {
        val daily = listOf(
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.DecliningSkiWeek.Day1.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.DecliningSkiWeek.Day1.CONDITION,
            ),
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.DecliningSkiWeek.Day2.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.DecliningSkiWeek.Day2.CONDITION,
            ),
            buildDailyForecast(
                temperatureCelsius = ActivityRankingCalculatorFixture.DecliningSkiWeek.Day3.TEMPERATURE_CELSIUS,
                condition = ActivityRankingCalculatorFixture.DecliningSkiWeek.Day3.CONDITION,
            ),
        )

        val skiing = calculator.calculate(daily).first { it.activity == Activities.SKIING }

        assertEquals(
            ActivityRankingCalculatorFixture.DecliningSkiWeek.EXPECTED_WEIGHTED_AVERAGE_SKIING_SCORE,
            skiing.score,
        )
        assertEquals(ActivityWeeklyReason.DECLINING, skiing.weeklyReason)
        assertEquals(
            ActivityRankingCalculatorFixture.DecliningSkiWeek.EXPECTED_SPECIFIC_REASON,
            skiing.dailyReason,
        )
    }

    private fun buildDailyForecast(
        date: String = ActivityRankingCalculatorFixture.DEFAULT_DATE,
        temperatureCelsius: Double = ActivityRankingCalculatorFixture.DEFAULT_TEMPERATURE_CELSIUS,
        precipitationMm: Double = ActivityRankingCalculatorFixture.DEFAULT_PRECIPITATION_MM,
        windSpeedKph: Double = ActivityRankingCalculatorFixture.DEFAULT_WIND_SPEED_KPH,
        dawnDuskWindSpeedKph: Double = ActivityRankingCalculatorFixture.DEFAULT_DAWN_DUSK_WIND_SPEED_KPH,
        dawnDuskPrecipitationProbabilityPercent: Double = ActivityRankingCalculatorFixture.DEFAULT_DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
        daytimeWindSpeedMaxKph: Double = ActivityRankingCalculatorFixture.DEFAULT_DAYTIME_WIND_SPEED_MAX_KPH,
        condition: WeatherCondition = WeatherCondition.PartlyCloudy,
    ) = DailyForecast(
        date = date,
        maxTemperatureCelsius = temperatureCelsius,
        minTemperatureCelsius = temperatureCelsius,
        precipitationSumMm = precipitationMm,
        precipitationProbabilityMaxPercent = ActivityRankingCalculatorFixture.DEFAULT_PRECIPITATION_PROBABILITY_PERCENT,
        snowfallSumCm = ActivityRankingCalculatorFixture.DEFAULT_SNOWFALL_SUM_CM,
        windSpeedMaxKph = windSpeedKph,
        windGustsMaxKph = ActivityRankingCalculatorFixture.DEFAULT_WIND_GUSTS_KPH,
        uvIndexMax = ActivityRankingCalculatorFixture.DEFAULT_UV_INDEX_MAX,
        daylightDurationHours = ActivityRankingCalculatorFixture.DEFAULT_DAYLIGHT_DURATION_HOURS,
        nightCloudCoverPercent = ActivityRankingCalculatorFixture.DEFAULT_NIGHT_CLOUD_COVER_PERCENT,
        dawnDuskWindSpeedKph = dawnDuskWindSpeedKph,
        dawnDuskPrecipitationProbabilityPercent = dawnDuskPrecipitationProbabilityPercent,
        daytimeWindSpeedMaxKph = daytimeWindSpeedMaxKph,
        daytimeWindGustsMaxKph = ActivityRankingCalculatorFixture.DEFAULT_DAYTIME_WIND_GUSTS_MAX_KPH,
        condition = condition,
    )
}
