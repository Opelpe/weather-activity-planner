package com.pnow.weatheractivityplanner.data.repository

import com.pnow.weatheractivityplanner.data.di.IoDispatcher
import com.pnow.weatheractivityplanner.data.di.TimeSource
import com.pnow.weatheractivityplanner.data.mapper.toDomain
import com.pnow.weatheractivityplanner.data.mapper.toDomainResult
import com.pnow.weatheractivityplanner.data.remote.api.WeatherApi
import com.pnow.weatheractivityplanner.domain.model.Forecast
import com.pnow.weatheractivityplanner.domain.repository.WeatherRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val timeSource: TimeSource,
) : WeatherRepository {

    private val cache = ConcurrentHashMap<Long, CachedForecast>()

    override suspend fun getForecast(
        locationId: Long,
        latitude: Double,
        longitude: Double,
        forceRefresh: Boolean,
    ): Result<Forecast> = withContext(ioDispatcher) {
        if (!forceRefresh) {
            val cached = cache[locationId]
            if (cached != null && timeSource.nowMs() - cached.fetchedAtMs < FORECAST_CACHE_TTL_MS) {
                return@withContext Result.success(cached.forecast)
            }
        }
        runCatching {
            weatherApi.getForecast(latitude = latitude, longitude = longitude).toDomain()
        }.toDomainResult().also { result ->
            result.onSuccess { forecast ->
                cache[locationId] =
                    CachedForecast(forecast = forecast, fetchedAtMs = timeSource.nowMs())
            }
        }
    }

    companion object {

        internal const val FORECAST_CACHE_TTL_MS = 120_000L
    }
}

private data class CachedForecast(
    val forecast: Forecast,
    val fetchedAtMs: Long,
)
