package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class SunbathingDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val warmFraction = fractionBetween(
            day.maxTemperatureCelsius,
            from = WARM_START_CELSIUS,
            to = WARM_THRESHOLD_CELSIUS,
        )
        val sunnyFraction =
            fractionBetween(day.uvIndexMax, from = SUNNY_START_UV, to = SUNNY_UV_THRESHOLD)
        val windyFraction = fractionBetween(
            day.daytimeWindSpeedMaxKph,
            from = WINDY_START_KPH,
            to = WINDY_THRESHOLD_KPH,
        )
        val rainFraction = day.precipitationFraction

        val isWarm = day.maxTemperatureCelsius >= WARM_THRESHOLD_CELSIUS
        val isSunny = day.uvIndexMax >= SUNNY_UV_THRESHOLD
        val isWindy = day.daytimeWindSpeedMaxKph >= WINDY_THRESHOLD_KPH
        val hasSignificantPrecipitation = day.hasSignificantPrecipitation

        val score = BASE_SCORE
            .activityBonus(warmFraction, WARM_BONUS)
            .activityBonus(sunnyFraction, SUNNY_BONUS)
            .activityPenalty(windyFraction, WINDY_PENALTY)
            .activityPenalty(rainFraction, RAIN_PENALTY)

        val reason = when {
            hasSignificantPrecipitation -> ActivityDailyReason.Sunbathing.Rain
            isWindy -> ActivityDailyReason.Sunbathing.Windy
            isWarm && isSunny -> ActivityDailyReason.Sunbathing.WarmAndSunny
            isSunny -> ActivityDailyReason.Sunbathing.SunnyOnly
            isWarm -> ActivityDailyReason.Sunbathing.WarmOnly
            warmFraction >= PARTIAL_THRESHOLD || sunnyFraction >= PARTIAL_THRESHOLD -> ActivityDailyReason.Sunbathing.WarmingUp
            else -> ActivityDailyReason.Sunbathing.None
        }

        return DayScore(score = coerceActivityScore(score), reason = reason)
    }

    private companion object {

        const val BASE_SCORE = 10f
        const val WARM_BONUS = 35f
        const val SUNNY_BONUS = 35f
        const val WINDY_PENALTY = 30f
        const val RAIN_PENALTY = 45f
        const val WARM_START_CELSIUS = 18.0
        const val WARM_THRESHOLD_CELSIUS = 24.0
        const val SUNNY_START_UV = 3.0
        const val SUNNY_UV_THRESHOLD = 6.0
        const val WINDY_START_KPH = 15.0
        const val WINDY_THRESHOLD_KPH = 25.0
        const val PARTIAL_THRESHOLD = 0.3f
    }
}
