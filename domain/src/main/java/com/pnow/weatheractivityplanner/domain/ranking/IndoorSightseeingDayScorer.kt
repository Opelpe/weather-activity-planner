package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
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

        val poorOutdoorFraction = listOf(
            day.precipitationFraction,
            if (day.condition.isRainy()) 1f else 0f,
            if (day.condition.isThunderstorm()) 1f else 0f,
            if (day.condition.isFoggy()) 1f else 0f,
            if (day.condition.isSnowy()) 1f else 0f,
        ).max()
        val extremeTempFraction = maxOf(
            fractionBetween(
                day.maxTemperatureCelsius,
                from = EXTREME_HOT_CELSIUS,
                to = EXTREME_HOT_FULL_CELSIUS,
            ),
            fractionBetween(
                day.minTemperatureCelsius,
                from = EXTREME_COLD_CELSIUS,
                to = EXTREME_COLD_FULL_CELSIUS,
            ),
        )

        val score = BASE_SCORE
            .activityBonus(poorOutdoorFraction, POOR_OUTDOOR_BONUS)
            .activityBonus(extremeTempFraction, EXTREME_TEMP_BONUS)
            .activityPenalty(isGreatOutdoor, GREAT_OUTDOOR_PENALTY)

        val reason = when {
            isPoorOutdoor -> ActivityDailyReason.IndoorSightseeing.PoorOutdoor
            isExtremeTemp -> ActivityDailyReason.IndoorSightseeing.ExtremeTemp
            isGreatOutdoor -> ActivityDailyReason.IndoorSightseeing.GreatOutdoor
            else -> ActivityDailyReason.IndoorSightseeing.None
        }

        return DayScore(score = coerceActivityScore(score), reason = reason)
    }

    private companion object {

        const val BASE_SCORE = 35f
        const val POOR_OUTDOOR_BONUS = 40f
        const val EXTREME_TEMP_BONUS = 15f
        const val GREAT_OUTDOOR_PENALTY = 25f
        const val EXTREME_COLD_CELSIUS = 5.0
        const val EXTREME_COLD_FULL_CELSIUS = -2.0
        const val EXTREME_HOT_CELSIUS = 28.0
        const val EXTREME_HOT_FULL_CELSIUS = 35.0
    }
}
