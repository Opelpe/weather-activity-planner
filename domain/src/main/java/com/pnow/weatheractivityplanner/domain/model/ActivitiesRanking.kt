package com.pnow.weatheractivityplanner.domain.model

data class ActivitiesRanking(
    val activities: Activities,
    val score: Float,
    val reason: ActivitiesRankingReason,
)
