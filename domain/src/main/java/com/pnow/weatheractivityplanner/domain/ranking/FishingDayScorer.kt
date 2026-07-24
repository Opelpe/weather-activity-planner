package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class FishingDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val calmFraction =
            fractionBetween(
                day.dawnDuskWindSpeedKph,
                from = WIND_NEUTRAL_KPH,
                to = CALM_THRESHOLD_KPH,
            )
        val dryFraction = fractionBetween(
            day.dawnDuskPrecipitationProbabilityPercent,
            from = HIGH_PRECIP_PROBABILITY_PERCENT,
            to = LOW_PRECIP_PROBABILITY_PERCENT,
        )
        val isThunderstorm = day.condition.isThunderstorm()
        val windyFraction =
            fractionBetween(
                day.dawnDuskWindSpeedKph,
                from = WIND_NEUTRAL_KPH,
                to = WINDY_THRESHOLD_KPH,
            )

        val isCalm = day.dawnDuskWindSpeedKph <= CALM_THRESHOLD_KPH
        val isLikelyDry =
            day.dawnDuskPrecipitationProbabilityPercent <= LOW_PRECIP_PROBABILITY_PERCENT
        val isWindy = day.dawnDuskWindSpeedKph >= WINDY_THRESHOLD_KPH

        val score = BASE_SCORE
            .activityBonus(calmFraction, CALM_BONUS)
            .activityBonus(dryFraction, DRY_BONUS)
            .activityPenalty(isThunderstorm, THUNDERSTORM_PENALTY)
            .activityPenalty(windyFraction, WINDY_PENALTY)

        val reason = when {
            isThunderstorm -> ActivityDailyReason.Fishing.Thunderstorm
            isWindy -> ActivityDailyReason.Fishing.Windy
            isCalm && isLikelyDry -> ActivityDailyReason.Fishing.CalmAndDry
            isCalm -> ActivityDailyReason.Fishing.CalmOnly
            isLikelyDry -> ActivityDailyReason.Fishing.DryOnly
            calmFraction >= PARTIAL_THRESHOLD || dryFraction >= PARTIAL_THRESHOLD -> ActivityDailyReason.Fishing.WatersCalming
            else -> ActivityDailyReason.Fishing.None
        }

        return DayScore(score = coerceActivityScore(score), reason = reason)
    }

    private companion object {

        const val BASE_SCORE = 20f
        const val CALM_BONUS = 30f
        const val DRY_BONUS = 30f
        const val THUNDERSTORM_PENALTY = 60f
        const val WINDY_PENALTY = 35f
        const val WIND_NEUTRAL_KPH = 20.0
        const val CALM_THRESHOLD_KPH = 10.0
        const val WINDY_THRESHOLD_KPH = 30.0
        const val LOW_PRECIP_PROBABILITY_PERCENT = 30.0
        const val HIGH_PRECIP_PROBABILITY_PERCENT = 60.0
        const val PARTIAL_THRESHOLD = 0.3f
    }
}
