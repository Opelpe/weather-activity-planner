package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class SurfingDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val warmFraction = fractionBetween(
            day.maxTemperatureCelsius,
            from = WARM_START_CELSIUS,
            to = WARM_THRESHOLD_CELSIUS,
        )
        val windyFraction = fractionBetween(
            day.daytimeWindSpeedMaxKph,
            from = WINDY_START_KPH,
            to = WINDY_THRESHOLD_KPH,
        )
        val coldFraction = fractionBetween(
            day.maxTemperatureCelsius,
            from = COLD_START_CELSIUS,
            to = COLD_THRESHOLD_CELSIUS,
        )
        val rainFraction =
            fractionBetween(day.precipitationSumMm, from = RAIN_START_MM, to = RAIN_FULL_MM)
        val isThunderstorm = day.condition.isThunderstorm()

        val isWarm = day.maxTemperatureCelsius >= WARM_THRESHOLD_CELSIUS
        val isWindy = day.daytimeWindSpeedMaxKph >= WINDY_THRESHOLD_KPH
        val hasSignificantPrecipitation = day.hasSignificantPrecipitation

        val score = BASE_SCORE
            .activityBonus(warmFraction, WARM_BONUS)
            .activityBonus(windyFraction, WIND_BONUS)
            .activityPenalty(isThunderstorm, THUNDERSTORM_PENALTY)
            .activityPenalty(coldFraction, COLD_PENALTY)
            .activityPenalty(rainFraction, RAIN_PENALTY)

        val reason = when {
            isThunderstorm -> ActivityDailyReason.Surfing.Thunderstorm
            hasSignificantPrecipitation -> ActivityDailyReason.Surfing.Rain
            isWarm && isWindy -> ActivityDailyReason.Surfing.WarmAndWindy
            isWindy -> ActivityDailyReason.Surfing.WindyOnly
            isWarm -> ActivityDailyReason.Surfing.WarmOnly
            warmFraction >= PARTIAL_THRESHOLD || windyFraction >= PARTIAL_THRESHOLD -> ActivityDailyReason.Surfing.BuildingConditions
            else -> ActivityDailyReason.Surfing.None
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
        const val WARM_START_CELSIUS = 10.0
        const val WARM_THRESHOLD_CELSIUS = 20.0
        const val COLD_START_CELSIUS = 10.0
        const val COLD_THRESHOLD_CELSIUS = 0.0
        const val WINDY_START_KPH = 5.0
        const val WINDY_THRESHOLD_KPH = 15.0
        const val RAIN_START_MM = 0.5
        const val RAIN_FULL_MM = 5.0
        const val PARTIAL_THRESHOLD = 0.3f
    }
}
