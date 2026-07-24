package com.pnow.weatheractivityplanner.domain.model

data class ActivitiesRanking(
    val activity: Activities,
    val score: Float,
    val weeklyReason: ActivityWeeklyReason,
    val dailyReason: ActivityDailyReason,
)
