package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class SurfingDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val isWarm = day.maxTemperatureCelsius >= WARM_THRESHOLD_CELSIUS
        val isWindy = day.windSpeedMaxKph >= WINDY_THRESHOLD_KPH
        val isThunderstorm = day.condition.isThunderstorm()
        val isCold = day.maxTemperatureCelsius < COLD_THRESHOLD_CELSIUS
        val hasSignificantPrecipitation = day.hasSignificantPrecipitation

        val score = BASE_SCORE
            .activityBonus(isWarm, WARM_BONUS)
            .activityBonus(isWindy, WIND_BONUS)
            .activityPenalty(isThunderstorm, THUNDERSTORM_PENALTY)
            .activityPenalty(isCold, COLD_PENALTY)
            .activityPenalty(hasSignificantPrecipitation, RAIN_PENALTY)

        val reason = when {
            isThunderstorm -> ActivitiesRankingReason.SURFING_THUNDERSTORM
            hasSignificantPrecipitation -> ActivitiesRankingReason.SURFING_RAIN
            isWarm && isWindy -> ActivitiesRankingReason.SURFING_WARM_AND_WINDY
            isWindy -> ActivitiesRankingReason.SURFING_WINDY_ONLY
            isWarm -> ActivitiesRankingReason.SURFING_WARM_ONLY
            else -> ActivitiesRankingReason.SURFING_NONE
        }

        return DayScore(score = coerceActivityScore(score), reason = reason)
    }

    private companion object {

        const val BASE_SCORE = 10f
        const val WARM_BONUS = 35f
        const val WIND_BONUS = 35f
        const val THUNDERSTORM_PENALTY = 70f
        const val COLD_PENALTY = 20f
        const val RAIN_PENALTY = 25f
        const val WARM_THRESHOLD_CELSIUS = 20.0
        const val COLD_THRESHOLD_CELSIUS = 10.0
        const val WINDY_THRESHOLD_KPH = 15.0
    }
}
