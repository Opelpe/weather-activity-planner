package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class StargazingDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val isClear = day.condition.isClear()
        val isLongNight = day.daylightDurationHours <= LONG_NIGHT_THRESHOLD_HOURS
        val hasSignificantPrecipitation = day.hasSignificantPrecipitation
        val isFoggy = day.condition.isFoggy()

        val score = BASE_SCORE
            .activityBonus(isClear, CLEAR_BONUS)
            .activityBonus(isLongNight, LONG_NIGHT_BONUS)
            .activityPenalty(hasSignificantPrecipitation, RAIN_PENALTY)
            .activityPenalty(isFoggy, FOG_PENALTY)

        val reason = when {
            hasSignificantPrecipitation -> ActivitiesRankingReason.Stargazing.Rain
            isFoggy -> ActivitiesRankingReason.Stargazing.Fog
            isClear && isLongNight -> ActivitiesRankingReason.Stargazing.ClearAndLongNight
            isClear -> ActivitiesRankingReason.Stargazing.ClearOnly
            isLongNight -> ActivitiesRankingReason.Stargazing.LongNightOnly
            else -> ActivitiesRankingReason.Stargazing.None
        }

        return DayScore(score = coerceActivityScore(score), reason = reason)
    }

    private companion object {

        const val BASE_SCORE = 20f
        const val CLEAR_BONUS = 45f
        const val LONG_NIGHT_BONUS = 25f
        const val RAIN_PENALTY = 40f
        const val FOG_PENALTY = 35f
        const val LONG_NIGHT_THRESHOLD_HOURS = 10.0
    }
}
