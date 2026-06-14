package com.pnow.weatheractivityplanner.domain.ranking

import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.DayScore

interface ActivityDayScorer {

    fun score(day: DailyForecast): DayScore
}
