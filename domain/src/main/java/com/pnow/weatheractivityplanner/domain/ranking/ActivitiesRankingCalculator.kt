package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.Activities
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRanking
import com.pnow.weatheractivityplanner.domain.model.ActivityDailyReason
import com.pnow.weatheractivityplanner.domain.model.ActivityWeeklyReason
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class ActivitiesRankingCalculator @Inject constructor(
    private val skiingDayScorer: SkiingDayScorer,
    private val surfingDayScorer: SurfingDayScorer,
    private val outdoorSightseeingDayScorer: OutdoorSightseeingDayScorer,
    private val indoorSightseeingDayScorer: IndoorSightseeingDayScorer,
    private val cyclingDayScorer: CyclingDayScorer,
    private val sunbathingDayScorer: SunbathingDayScorer,
    private val stargazingDayScorer: StargazingDayScorer,
    private val fishingDayScorer: FishingDayScorer,
) {

    fun calculate(daily: List<DailyForecast>): List<ActivitiesRanking> {
        if (daily.isEmpty()) return emptyList()

        return Activities.entries
            .map { activity -> activity.toRanking(daily) }
            .sortedWith(
                compareByDescending<ActivitiesRanking> { it.score }.thenBy { it.activity.name },
            )
    }

    private fun Activities.toRanking(daily: List<DailyForecast>): ActivitiesRanking {
        val scorer = scorerFor(this)
        val dayScores = daily.map { day -> scorer.score(day) }
        val weights = dayScores.indices.map { index -> DAY_WEIGHT_DECAY_RATE.pow(index) }
        val weightedAverage = dayScores.zip(weights) { dayScore, weight -> dayScore.score * weight }
            .sum() / weights.sum()
        val promotedAverage = if (this in GENERIC_ACTIVITIES) {
            (weightedAverage + GENERIC_ACTIVITY_BONUS).coerceAtMost(MAX_RANKING_SCORE)
        } else {
            weightedAverage
        }
        val weekReason = weekReasonFor(dayScores, promotedAverage)
        val reason = dayScores.representativeReason(weightedAverage)

        return ActivitiesRanking(
            activity = this,
            score = promotedAverage,
            weeklyReason = weekReason,
            dailyReason = reason,
        )
    }

    private fun List<DayScore>.representativeReason(weightedAverage: Float): ActivityDailyReason {
        if (size == 1) return single().reason

        val reasonCounts = groupingBy { it.reason }.eachCount()
        val maxCount = reasonCounts.values.max()
        val mostCommonReasons = reasonCounts.filterValues { it == maxCount }.keys

        return filter { it.reason in mostCommonReasons }
            .minBy { abs(it.score - weightedAverage) }
            .reason
    }

    private fun weekReasonFor(
        dayScores: List<DayScore>,
        weightedAverage: Float,
    ): ActivityWeeklyReason {
        val variability = dayScores.scoreVariability()
        if (variability >= MIXED_WEEK_VARIABILITY_THRESHOLD) {
            return ActivityWeeklyReason.MIXED
        }

        val trend = dayScores.scoreTrend()
        return when {
            trend >= TREND_THRESHOLD -> ActivityWeeklyReason.IMPROVING
            trend <= -TREND_THRESHOLD -> ActivityWeeklyReason.DECLINING
            weightedAverage >= GREAT_SCORE_THRESHOLD -> ActivityWeeklyReason.CONSISTENTLY_GREAT
            weightedAverage >= GOOD_SCORE_THRESHOLD -> ActivityWeeklyReason.CONSISTENTLY_GOOD
            weightedAverage >= AVERAGE_SCORE_THRESHOLD -> ActivityWeeklyReason.CONSISTENTLY_AVERAGE
            weightedAverage >= POOR_SCORE_THRESHOLD -> ActivityWeeklyReason.CONSISTENTLY_POOR
            else -> ActivityWeeklyReason.CONSISTENTLY_TERRIBLE
        }
    }

    private fun List<DayScore>.scoreVariability(): Float {
        val mean = map { it.score }.average()
        val variance = map { (it.score - mean) * (it.score - mean) }.average()
        return sqrt(variance).toFloat()
    }

    private fun List<DayScore>.scoreTrend(): Float {
        val halfSize = size / 2
        if (halfSize == 0) return 0f

        val firstHalfAverage = take(halfSize).map { it.score }.average()
        val secondHalfAverage = takeLast(halfSize).map { it.score }.average()
        return (secondHalfAverage - firstHalfAverage).toFloat()
    }

    private fun scorerFor(activities: Activities): ActivityDayScorer = when (activities) {
        Activities.SKIING -> skiingDayScorer
        Activities.SURFING -> surfingDayScorer
        Activities.OUTDOOR_SIGHTSEEING -> outdoorSightseeingDayScorer
        Activities.INDOOR_SIGHTSEEING -> indoorSightseeingDayScorer
        Activities.CYCLING -> cyclingDayScorer
        Activities.SUNBATHING -> sunbathingDayScorer
        Activities.STARGAZING -> stargazingDayScorer
        Activities.FISHING -> fishingDayScorer
    }

    private companion object {

        const val DAY_WEIGHT_DECAY_RATE = 0.85f
        const val MIXED_WEEK_VARIABILITY_THRESHOLD = 25f
        const val TREND_THRESHOLD = 15f
        const val GREAT_SCORE_THRESHOLD = 80f
        const val GOOD_SCORE_THRESHOLD = 60f
        const val AVERAGE_SCORE_THRESHOLD = 40f
        const val POOR_SCORE_THRESHOLD = 20f
        const val MAX_RANKING_SCORE = 100f

        const val GENERIC_ACTIVITY_BONUS = 10f
        val GENERIC_ACTIVITIES =
            setOf(Activities.OUTDOOR_SIGHTSEEING, Activities.INDOOR_SIGHTSEEING)
    }
}
