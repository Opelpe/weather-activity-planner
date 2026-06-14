package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingsResult
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.ranking.ActivitiesRankingCalculator
import javax.inject.Inject

class GetActivityRankingsUseCase @Inject constructor(
    private val getForecastUseCase: GetForecastUseCase,
    private val activitiesRankingCalculator: ActivitiesRankingCalculator,
) {

    suspend operator fun invoke(location: Location): Result<ActivitiesRankingsResult> =
        getForecastUseCase(
            latitude = location.latitude,
            longitude = location.longitude,
        ).map { forecast ->
            ActivitiesRankingsResult(
                currentWeather = forecast.current,
                rankings = activitiesRankingCalculator.calculate(forecast.daily),
            )
        }
}
