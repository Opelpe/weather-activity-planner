package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.Activities
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRanking
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import javax.inject.Inject
import kotlin.math.abs

class ActivitiesRankingCalculator @Inject constructor(
    private val skiingDayScorer: SkiingDayScorer,
    private val surfingDayScorer: SurfingDayScorer,
    private val outdoorSightseeingDayScorer: OutdoorSightseeingDayScorer,
    private val indoorSightseeingDayScorer: IndoorSightseeingDayScorer,
) {

    fun calculate(daily: List<DailyForecast>): List<ActivitiesRanking> =
        Activities.entries
            .map { activity -> activity.toRanking(daily) }
            .sortedWith(
                compareByDescending<ActivitiesRanking> { it.score }.thenBy { it.activities.name },
            )

    private fun Activities.toRanking(daily: List<DailyForecast>): ActivitiesRanking {
        val scorer = scorerFor(this)
        val dayScores = daily.map { day -> scorer.score(day) }
        val averageScore = dayScores.map { it.score }.average().toFloat()
        val representativeReason = dayScores.minBy { abs(it.score - averageScore) }.reason

        return ActivitiesRanking(
            activities = this,
            score = averageScore,
            reason = representativeReason,
        )
    }

    private fun scorerFor(activities: Activities): ActivityDayScorer = when (activities) {
        Activities.SKIING -> skiingDayScorer
        Activities.SURFING -> surfingDayScorer
        Activities.OUTDOOR_SIGHTSEEING -> outdoorSightseeingDayScorer
        Activities.INDOOR_SIGHTSEEING -> indoorSightseeingDayScorer
    }
}
