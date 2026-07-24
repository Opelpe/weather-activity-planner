package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject

class StargazingDayScorer @Inject constructor() : ActivityDayScorer {

    override fun score(day: DailyForecast): DayScore {
        val clearSkyFraction = fractionBetween(
            day.nightCloudCoverPercent,
            from = CLOUDY_THRESHOLD_PERCENT,
            to = CLEAR_THRESHOLD_PERCENT,
        )
        val longNightFraction = fractionBetween(
            day.daylightDurationHours,
            from = LONG_NIGHT_THRESHOLD_HOURS,
            to = LONG_NIGHT_FULL_HOURS,
        )
        val rainFraction = day.precipitationFraction
        val isFoggy = day.condition.isFoggy()

        val isClearSky = day.nightCloudCoverPercent <= CLEAR_THRESHOLD_PERCENT
        val isLongNight = day.daylightDurationHours <= LONG_NIGHT_THRESHOLD_HOURS
        val hasSignificantPrecipitation = day.hasSignificantPrecipitation

        val score = BASE_SCORE
            .activityBonus(clearSkyFraction, CLEAR_BONUS)
            .activityBonus(longNightFraction, LONG_NIGHT_BONUS)
            .activityPenalty(rainFraction, RAIN_PENALTY)
            .activityPenalty(isFoggy, FOG_PENALTY)

        val reason = when {
            hasSignificantPrecipitation -> ActivityDailyReason.Stargazing.Rain
            isFoggy -> ActivityDailyReason.Stargazing.Fog
            isClearSky && isLongNight -> ActivityDailyReason.Stargazing.ClearAndLongNight
            isClearSky -> ActivityDailyReason.Stargazing.ClearOnly
            isLongNight -> ActivityDailyReason.Stargazing.LongNightOnly
            else -> ActivityDailyReason.Stargazing.None
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
        const val LONG_NIGHT_FULL_HOURS = 8.0
        const val CLEAR_THRESHOLD_PERCENT = 20.0
        const val CLOUDY_THRESHOLD_PERCENT = 70.0
    }
}
