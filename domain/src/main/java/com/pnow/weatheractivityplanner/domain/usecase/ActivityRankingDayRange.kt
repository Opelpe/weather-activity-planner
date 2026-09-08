package com.pnow.weatheractivityplanner.domain.usecase

object ActivityRankingDayRange {

    const val MIN_DAY_COUNT = 1
    const val MAX_DAY_COUNT = 7
    const val DEFAULT_DAY_COUNT = 1

    fun coerce(days: Int): Int = days.coerceIn(MIN_DAY_COUNT, MAX_DAY_COUNT)
}
