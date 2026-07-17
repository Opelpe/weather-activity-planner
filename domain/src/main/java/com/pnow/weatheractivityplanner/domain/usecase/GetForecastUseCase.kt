package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.model.Forecast
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.repository.WeatherRepository
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
) {

    suspend operator fun invoke(
        location: Location,
        forceRefresh: Boolean = false,
    ): Result<Forecast> =
        weatherRepository.getForecast(
            locationId = location.id,
            latitude = location.latitude,
            longitude = location.longitude,
            forceRefresh = forceRefresh,
        )
}
