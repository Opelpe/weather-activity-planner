package com.pnow.weatheractivityplanner.data.repository

import com.pnow.weatheractivityplanner.data.di.IoDispatcher
import com.pnow.weatheractivityplanner.data.mapper.toDomain
import com.pnow.weatheractivityplanner.data.mapper.toDomainResult
import com.pnow.weatheractivityplanner.data.remote.api.WeatherApi
import com.pnow.weatheractivityplanner.domain.model.Forecast
import com.pnow.weatheractivityplanner.domain.repository.WeatherRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : WeatherRepository {

    override suspend fun getForecast(
        latitude: Double,
        longitude: Double,
    ): Result<Forecast> =
        withContext(ioDispatcher) {
            runCatching {
                weatherApi.getForecast(latitude = latitude, longitude = longitude).toDomain()
            }.toDomainResult()
        }
}
