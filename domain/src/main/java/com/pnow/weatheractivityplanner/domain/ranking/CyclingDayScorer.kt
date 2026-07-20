package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class CyclingDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val isComfortable = day.isComfortableTemperature
        val isGusty = day.windGustsMaxKph >= GUSTY_THRESHOLD_KPH
        val hasSignificantPrecipitation = day.hasSignificantPrecipitation
        val isCold = day.maxTemperatureCelsius < COLD_THRESHOLD_CELSIUS

        val score = BASE_SCORE
            .activityBonus(isComfortable, COMFORTABLE_BONUS)
            .activityPenalty(isGusty, GUSTY_PENALTY)
            .activityPenalty(hasSignificantPrecipitation, RAIN_PENALTY)
            .activityPenalty(isCold, COLD_PENALTY)

        val reason = when {
            hasSignificantPrecipitation -> ActivitiesRankingReason.Cycling.Rain
            isGusty -> ActivitiesRankingReason.Cycling.Gusty
            isComfortable -> ActivitiesRankingReason.Cycling.Comfortable
            isCold -> ActivitiesRankingReason.Cycling.Cold
            else -> ActivitiesRankingReason.Cycling.None
        }

        return DayScore(score = coerceActivityScore(score), reason = reason)
    }

    private companion object {

        const val BASE_SCORE = 25f
        const val COMFORTABLE_BONUS = 45f
        const val GUSTY_PENALTY = 40f
        const val RAIN_PENALTY = 35f
        const val COLD_PENALTY = 20f
        const val COLD_THRESHOLD_CELSIUS = 5.0
        const val GUSTY_THRESHOLD_KPH = 40.0
    }
}
