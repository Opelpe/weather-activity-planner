package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class FishingDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val isCalm = day.windSpeedMaxKph <= CALM_THRESHOLD_KPH
        val isLikelyDry = day.precipitationProbabilityMaxPercent <= LOW_PRECIP_PROBABILITY_PERCENT
        val isThunderstorm = day.condition.isThunderstorm()
        val isWindy = day.windSpeedMaxKph >= WINDY_THRESHOLD_KPH

        val score = BASE_SCORE
            .activityBonus(isCalm, CALM_BONUS)
            .activityBonus(isLikelyDry, DRY_BONUS)
            .activityPenalty(isThunderstorm, THUNDERSTORM_PENALTY)
            .activityPenalty(isWindy, WINDY_PENALTY)

        val reason = when {
            isThunderstorm -> ActivitiesRankingReason.FISHING_THUNDERSTORM
            isWindy -> ActivitiesRankingReason.FISHING_WINDY
            isCalm && isLikelyDry -> ActivitiesRankingReason.FISHING_CALM_AND_DRY
            isCalm -> ActivitiesRankingReason.FISHING_CALM_ONLY
            isLikelyDry -> ActivitiesRankingReason.FISHING_DRY_ONLY
            else -> ActivitiesRankingReason.FISHING_NONE
        }

        return DayScore(score = coerceActivityScore(score), reason = reason)
    }

    private companion object {

        const val BASE_SCORE = 20f
        const val CALM_BONUS = 30f
        const val DRY_BONUS = 30f
        const val THUNDERSTORM_PENALTY = 60f
        const val WINDY_PENALTY = 35f
        const val CALM_THRESHOLD_KPH = 10.0
        const val WINDY_THRESHOLD_KPH = 30.0
        const val LOW_PRECIP_PROBABILITY_PERCENT = 30
    }
}
