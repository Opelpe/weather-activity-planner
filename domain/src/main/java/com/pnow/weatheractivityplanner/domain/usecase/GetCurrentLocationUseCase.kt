package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.repository.CurrentLocationRepository
import com.pnow.weatheractivityplanner.domain.repository.GeocodingRepository
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val currentLocationRepository: CurrentLocationRepository,
    private val geocodingRepository: GeocodingRepository,
) {

    suspend operator fun invoke(): Result<Location> =
        currentLocationRepository.getCurrentLocation().fold(
            onSuccess = { coordinates ->
                geocodingRepository.reverseGeocode(
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude,
                )
            },
            onFailure = { Result.failure(it) },
        )
}
