package com.pnow.weatheractivityplanner.data.repository

import com.pnow.weatheractivityplanner.data.di.IoDispatcher
import com.pnow.weatheractivityplanner.data.mapper.toDomain
import com.pnow.weatheractivityplanner.data.mapper.toDomainResult
import com.pnow.weatheractivityplanner.data.remote.api.GeocodingApi
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.repository.GeocodingRepository
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException

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
                try {
                    geocodingApi.searchLocations(
                        query = query,
                        limit = count,
                        acceptLanguage = Locale.getDefault().language,
                    )
                        .map { it.toDomain() }
                } catch (httpException: HttpException) {
                    // LocationIQ returns HTTP 404 ("Unable to geocode") for zero matches, not a real failure.
                    if (httpException.code() == HTTP_NOT_FOUND) emptyList() else throw httpException
                }
            }.toDomainResult()
        }

    override suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
    ): Result<Location> =
        withContext(ioDispatcher) {
            runCatching {
                geocodingApi.reverseGeocode(
                    latitude = latitude,
                    longitude = longitude,
                    acceptLanguage = Locale.getDefault().language,
                ).toDomain()
            }.toDomainResult()
        }

    private companion object {

        const val HTTP_NOT_FOUND = 404
    }
}
