package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class OutdoorSightseeingDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val isClear = day.condition.isClear()
        val comfortableFraction = day.comfortableTemperatureFraction
        val rainFraction = day.precipitationFraction
        val isFoggy = day.condition.isFoggy()

        val isComfortable = day.isComfortableTemperature
        val hasSignificantPrecipitation = day.hasSignificantPrecipitation

        val score = BASE_SCORE
            .activityBonus(isClear, CLEAR_BONUS)
            .activityBonus(comfortableFraction, COMFORTABLE_BONUS)
            .activityPenalty(rainFraction, RAIN_PENALTY)
            .activityPenalty(isFoggy, FOG_PENALTY)

        val reason = when {
            isClear && isComfortable -> ActivityDailyReason.OutdoorSightseeing.ClearAndComfortable
            hasSignificantPrecipitation -> ActivityDailyReason.OutdoorSightseeing.Rain
            isFoggy -> ActivityDailyReason.OutdoorSightseeing.Fog
            isClear -> ActivityDailyReason.OutdoorSightseeing.ClearOnly
            !isComfortable && comfortableFraction >= PARTIAL_THRESHOLD -> ActivityDailyReason.OutdoorSightseeing.WarmingUp
            else -> ActivityDailyReason.OutdoorSightseeing.None
        }

        return DayScore(score = coerceActivityScore(score), reason = reason)
    }

    private companion object {

        const val BASE_SCORE = 25f
        const val CLEAR_BONUS = 45f
        const val COMFORTABLE_BONUS = 35f
        const val RAIN_PENALTY = 45f
        const val FOG_PENALTY = 25f
        const val PARTIAL_THRESHOLD = 0.3f
    }
}
