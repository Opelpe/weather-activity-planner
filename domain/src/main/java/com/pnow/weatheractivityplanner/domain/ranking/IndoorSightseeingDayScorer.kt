package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class IndoorSightseeingDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val isClear = day.condition.isClear()
        val isComfortable = day.isComfortableTemperature
        val isGreatOutdoor = isClear && isComfortable
        val isPoorOutdoor = day.hasSignificantPrecipitation ||
            day.condition.isRainy() ||
            day.condition.isThunderstorm() ||
            day.condition.isFoggy() ||
            day.condition.isSnowy()
        val isExtremeTemp = day.maxTemperatureCelsius > EXTREME_HOT_CELSIUS ||
            day.minTemperatureCelsius < EXTREME_COLD_CELSIUS

        val score = BASE_SCORE
            .activityBonus(isPoorOutdoor, POOR_OUTDOOR_BONUS)
            .activityBonus(isExtremeTemp, EXTREME_TEMP_BONUS)
            .activityPenalty(isGreatOutdoor, GREAT_OUTDOOR_PENALTY)

        val reason = when {
            isPoorOutdoor -> ActivitiesRankingReason.IndoorSightseeing.PoorOutdoor
            isExtremeTemp -> ActivitiesRankingReason.IndoorSightseeing.ExtremeTemp
            isGreatOutdoor -> ActivitiesRankingReason.IndoorSightseeing.GreatOutdoor
            else -> ActivitiesRankingReason.IndoorSightseeing.None
        }

        return DayScore(score = coerceActivityScore(score), reason = reason)
    }

    private companion object {

        const val BASE_SCORE = 40f
        const val POOR_OUTDOOR_BONUS = 40f
        const val EXTREME_TEMP_BONUS = 15f
        const val GREAT_OUTDOOR_PENALTY = 25f
        const val EXTREME_COLD_CELSIUS = 5.0
        const val EXTREME_HOT_CELSIUS = 28.0
    }
}
