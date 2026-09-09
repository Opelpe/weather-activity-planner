package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingsResult
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.ranking.ActivitiesRankingCalculator
import javax.inject.Inject

class GetActivityRankingsUseCase @Inject constructor(
    private val getForecastUseCase: GetForecastUseCase,
    private val activitiesRankingCalculator: ActivitiesRankingCalculator,
) {

    suspend operator fun invoke(
        location: Location,
        days: Int = ActivityRankingDayRange.DEFAULT_DAY_COUNT,
        forceRefresh: Boolean = false,
    ): Result<ActivitiesRankingsResult> =
        getForecastUseCase(
            location = location,
            forceRefresh = forceRefresh,
        ).map { forecast ->
            val coercedDays = ActivityRankingDayRange.coerce(days)
            val scoredDays = forecast.daily.scoringWindow(coercedDays)
            ActivitiesRankingsResult(
                currentWeather = forecast.current,
                rankings = activitiesRankingCalculator.calculate(scoredDays),
                isCached = forecast.isCached,
                isIncomplete = scoredDays.size < coercedDays,
            )
        }

    private fun List<DailyForecast>.scoringWindow(days: Int): List<DailyForecast> =
        if (days <= ActivityRankingDayRange.MIN_DAY_COUNT) {
            take(ActivityRankingDayRange.MIN_DAY_COUNT)
        } else {
            drop(ActivityRankingDayRange.MIN_DAY_COUNT).take(days)
        }
}
