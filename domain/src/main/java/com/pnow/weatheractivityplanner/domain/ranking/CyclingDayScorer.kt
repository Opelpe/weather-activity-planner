package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class CyclingDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val isComfortable = day.isComfortableTemperature
        val gustyFraction = fractionBetween(
            day.daytimeWindGustsMaxKph,
            from = GUSTY_START_KPH,
            to = GUSTY_THRESHOLD_KPH,
        )
        val rainFraction = day.precipitationFraction
        val coldFraction = fractionBetween(
            day.maxTemperatureCelsius,
            from = COLD_THRESHOLD_CELSIUS,
            to = COLD_FULL_CELSIUS,
        )

        val isGusty = day.daytimeWindGustsMaxKph >= GUSTY_THRESHOLD_KPH
        val hasSignificantPrecipitation = day.hasSignificantPrecipitation
        val isCold = day.maxTemperatureCelsius < COLD_THRESHOLD_CELSIUS

        val score = BASE_SCORE
            .activityBonus(isComfortable, COMFORTABLE_BONUS)
            .activityPenalty(gustyFraction, GUSTY_PENALTY)
            .activityPenalty(rainFraction, RAIN_PENALTY)
            .activityPenalty(coldFraction, COLD_PENALTY)

        val reason = when {
            hasSignificantPrecipitation -> ActivityDailyReason.Cycling.Rain
            isGusty -> ActivityDailyReason.Cycling.Gusty
            isComfortable -> ActivityDailyReason.Cycling.Comfortable
            isCold -> ActivityDailyReason.Cycling.Cold
            gustyFraction >= PARTIAL_THRESHOLD -> ActivityDailyReason.Cycling.WindPickingUp
            else -> ActivityDailyReason.Cycling.None
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
        const val COLD_FULL_CELSIUS = 0.0
        const val GUSTY_START_KPH = 20.0
        const val GUSTY_THRESHOLD_KPH = 40.0
        const val PARTIAL_THRESHOLD = 0.3f
    }
}
