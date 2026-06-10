package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.model.Forecast
import com.pnow.weatheractivityplanner.domain.repository.WeatherRepository
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
) {

    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
    ): Result<Forecast> =
        weatherRepository.getForecast(latitude = latitude, longitude = longitude)
}
