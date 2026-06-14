package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class SkiingDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val isSnowy = day.condition.isSnowy()
        val isFreezing = day.minTemperatureCelsius <= FREEZING_THRESHOLD_CELSIUS
        val isRainy = day.condition.isRainy()
        val isTooWarm = !isFreezing && day.maxTemperatureCelsius > TOO_WARM_THRESHOLD_CELSIUS

        val score = BASE_SCORE
            .activityBonus(isSnowy, SNOW_BONUS)
            .activityBonus(isFreezing, FREEZING_BONUS)
            .activityPenalty(isRainy, RAIN_PENALTY)
            .activityPenalty(isTooWarm, TOO_WARM_PENALTY)

        val reason = when {
            isSnowy && isFreezing -> ActivitiesRankingReason.SKIING_SNOW_AND_FREEZING
            isFreezing -> ActivitiesRankingReason.SKIING_FREEZING_ONLY
            isSnowy -> ActivitiesRankingReason.SKIING_SNOW_ONLY
            isRainy -> ActivitiesRankingReason.SKIING_RAIN
            else -> ActivitiesRankingReason.SKIING_NONE
        }

        return DayScore(score = coerceActivityScore(score), reason = reason)
    }

    private companion object {

        const val BASE_SCORE = 10f
        const val SNOW_BONUS = 70f
        const val FREEZING_BONUS = 35f
        const val RAIN_PENALTY = 40f
        const val TOO_WARM_PENALTY = 25f
        const val FREEZING_THRESHOLD_CELSIUS = -5.0
        const val TOO_WARM_THRESHOLD_CELSIUS = 10.0
    }
}
