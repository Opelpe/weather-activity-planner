package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.repository.GeocodingRepository
import javax.inject.Inject

class SearchLocationsUseCase @Inject constructor(
    private val geocodingRepository: GeocodingRepository,
) {

    suspend operator fun invoke(
        query: String,
        count: Int = 10,
    ): Result<List<Location>> =
        geocodingRepository.searchLocations(query = query, count = count)
}
