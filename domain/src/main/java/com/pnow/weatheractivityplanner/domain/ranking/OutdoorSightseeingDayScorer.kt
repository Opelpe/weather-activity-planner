package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class OutdoorSightseeingDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val isClear = day.condition.isClear()
        val isComfortable = day.isComfortableTemperature
        val hasSignificantPrecipitation = day.hasSignificantPrecipitation
        val isFoggy = day.condition.isFoggy()

        val score = BASE_SCORE
            .activityBonus(isClear, CLEAR_BONUS)
            .activityBonus(isComfortable, COMFORTABLE_BONUS)
            .activityPenalty(hasSignificantPrecipitation, RAIN_PENALTY)
            .activityPenalty(isFoggy, FOG_PENALTY)

        val reason = when {
            isClear && isComfortable -> ActivitiesRankingReason.OUTDOOR_CLEAR_AND_COMFORTABLE
            hasSignificantPrecipitation -> ActivitiesRankingReason.OUTDOOR_RAIN
            isFoggy -> ActivitiesRankingReason.OUTDOOR_FOG
            isClear -> ActivitiesRankingReason.OUTDOOR_CLEAR_ONLY
            else -> ActivitiesRankingReason.OUTDOOR_NONE
        }

        return DayScore(score = coerceActivityScore(score), reason = reason)
    }

    private companion object {

        const val BASE_SCORE = 30f
        const val CLEAR_BONUS = 45f
        const val COMFORTABLE_BONUS = 35f
        const val RAIN_PENALTY = 45f
        const val FOG_PENALTY = 25f
    }
}
