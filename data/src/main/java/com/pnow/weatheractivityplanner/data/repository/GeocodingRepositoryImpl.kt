package com.pnow.weatheractivityplanner.data.repository

import com.pnow.weatheractivityplanner.data.di.IoDispatcher
import com.pnow.weatheractivityplanner.data.mapper.toDomain
import com.pnow.weatheractivityplanner.data.mapper.toDomainResult
import com.pnow.weatheractivityplanner.data.remote.api.GeocodingApi
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.repository.GeocodingRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class GeocodingRepositoryImpl @Inject constructor(
    private val geocodingApi: GeocodingApi,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : GeocodingRepository {

    override suspend fun searchLocations(
        query: String,
        count: Int,
    ): Result<List<Location>> =
        withContext(ioDispatcher) {
            runCatching {
                geocodingApi.searchLocations(name = query, count = count)
                    .results
                    .orEmpty()
                    .map { it.toDomain() }
            }.toDomainResult()
        }
}
