package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class BeachDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val isWarm = day.maxTemperatureCelsius >= WARM_THRESHOLD_CELSIUS
        val isSunny = day.uvIndexMax >= SUNNY_UV_THRESHOLD
        val isWindy = day.windSpeedMaxKph >= WINDY_THRESHOLD_KPH
        val hasSignificantPrecipitation = day.hasSignificantPrecipitation

        val score = BASE_SCORE
            .activityBonus(isWarm, WARM_BONUS)
            .activityBonus(isSunny, SUNNY_BONUS)
            .activityPenalty(isWindy, WINDY_PENALTY)
            .activityPenalty(hasSignificantPrecipitation, RAIN_PENALTY)

        val reason = when {
            hasSignificantPrecipitation -> ActivitiesRankingReason.BEACH_DAY_RAIN
            isWindy -> ActivitiesRankingReason.BEACH_DAY_WINDY
            isWarm && isSunny -> ActivitiesRankingReason.BEACH_DAY_WARM_AND_SUNNY
            isSunny -> ActivitiesRankingReason.BEACH_DAY_SUNNY_ONLY
            isWarm -> ActivitiesRankingReason.BEACH_DAY_WARM_ONLY
            else -> ActivitiesRankingReason.BEACH_DAY_NONE
        }

        return DayScore(score = coerceActivityScore(score), reason = reason)
    }

    private companion object {

        const val BASE_SCORE = 10f
        const val WARM_BONUS = 35f
        const val SUNNY_BONUS = 35f
        const val WINDY_PENALTY = 30f
        const val RAIN_PENALTY = 45f
        const val WARM_THRESHOLD_CELSIUS = 24.0
        const val SUNNY_UV_THRESHOLD = 6.0
        const val WINDY_THRESHOLD_KPH = 25.0
    }
}
