package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class SkiingDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val freezingFraction = fractionBetween(
            day.minTemperatureCelsius,
            from = FREEZING_START_CELSIUS,
            to = FREEZING_THRESHOLD_CELSIUS,
        )
        val tooWarmFraction = fractionBetween(
            day.maxTemperatureCelsius,
            from = TOO_WARM_THRESHOLD_CELSIUS,
            to = TOO_WARM_FULL_CELSIUS,
        )
        val isSnowy = day.condition.isSnowy()
        val isRainy = day.condition.isRainy()
        val isFreezing = day.minTemperatureCelsius <= FREEZING_THRESHOLD_CELSIUS
        val isTooWarm = !isFreezing && day.maxTemperatureCelsius > TOO_WARM_THRESHOLD_CELSIUS

        val score = BASE_SCORE
            .activityBonus(isSnowy, SNOW_BONUS)
            .activityBonus(freezingFraction, FREEZING_BONUS)
            .activityPenalty(isRainy, RAIN_PENALTY)
            .activityPenalty(tooWarmFraction, TOO_WARM_PENALTY)

        val reason = when {
            isSnowy && isFreezing -> ActivityDailyReason.Skiing.SnowAndFreezing
            isFreezing -> ActivityDailyReason.Skiing.FreezingOnly
            isSnowy -> ActivityDailyReason.Skiing.SnowOnly
            isRainy -> ActivityDailyReason.Skiing.Rain
            isTooWarm -> ActivityDailyReason.Skiing.TooWarm
            freezingFraction >= PARTIAL_THRESHOLD -> ActivityDailyReason.Skiing.GettingColder
            else -> ActivityDailyReason.Skiing.None
        }

        return DayScore(score = coerceActivityScore(score), reason = reason)
    }

    private companion object {

        const val BASE_SCORE = 10f
        const val SNOW_BONUS = 70f
        const val FREEZING_BONUS = 35f
        const val RAIN_PENALTY = 40f
        const val TOO_WARM_PENALTY = 25f
        const val FREEZING_START_CELSIUS = 0.0
        const val FREEZING_THRESHOLD_CELSIUS = -5.0
        const val TOO_WARM_THRESHOLD_CELSIUS = 10.0
        const val TOO_WARM_FULL_CELSIUS = 20.0
        const val PARTIAL_THRESHOLD = 0.3f
    }
}
