package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingsResult
import com.pnow.weatheractivityplanner.domain.model.Location
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetActivityRankingsUseCase @Inject constructor(
    private val getForecastUseCase: GetForecastUseCase,
    private val activitiesRankingCalculator: ActivitiesRankingCalculator,
) {

    operator fun invoke(location: Location): Flow<ActivitiesRankingsResult> = flow {
        val forecast = getForecastUseCase(
            latitude = location.latitude,
            longitude = location.longitude,
        ).getOrThrow()

        emit(
            ActivitiesRankingsResult(
                currentWeather = forecast.current,
                rankings = activitiesRankingCalculator.calculate(forecast.current),
            ),
        )
    }
}
