package com.pnow.weatheractivityplanner.data.repository

import com.pnow.weatheractivityplanner.data.di.TimeSource
import com.pnow.weatheractivityplanner.data.remote.api.WeatherApi
import com.pnow.weatheractivityplanner.data.remote.dto.forecast.CurrentWeatherDto
import com.pnow.weatheractivityplanner.data.remote.dto.forecast.DailyDataDto
import com.pnow.weatheractivityplanner.data.remote.dto.forecast.ForecastResponseDto
import com.pnow.weatheractivityplanner.data.remote.dto.forecast.HourlyDataDto
import com.pnow.weatheractivityplanner.domain.error.DomainError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

private object WeatherRepositoryFixture {

    const val LOCATION_ID = 1L
    const val OTHER_LOCATION_ID = 2L
    const val LATITUDE = 51.5
    const val LONGITUDE = -0.1
    const val OTHER_LATITUDE = 48.85
    const val TIMEZONE = "Europe/London"
    const val NETWORK_ERROR_MESSAGE = "No network"
    const val HTTP_ERROR_CODE = 429
    const val HTTP_ERROR_BODY = "Too Many Requests"
    const val UNEXPECTED_ERROR_MESSAGE = "Unexpected"

    object Current {

        const val TIME = "2024-01-01T12:00"
        const val WEATHER_CODE = 1
        const val IS_DAY = 1
        const val TEMPERATURE_CELSIUS = 18.0
        const val APPARENT_TEMPERATURE_CELSIUS = 17.0
        const val HUMIDITY_PERCENT = 65
        const val PRECIPITATION_MM = 0.0
        const val WIND_SPEED_KPH = 15.0
    }

    object Day {

        const val DATE = "2024-01-01"
        const val WEATHER_CODE = 1
        const val MAX_TEMPERATURE_CELSIUS = 20.0
        const val MIN_TEMPERATURE_CELSIUS = 14.0
        const val PRECIPITATION_SUM_MM = 0.0
        const val PRECIPITATION_PROBABILITY_PERCENT = 10
        const val SNOWFALL_SUM_CM = 0.0
        const val WIND_SPEED_MAX_KPH = 15.0
        const val WIND_GUSTS_MAX_KPH = 25.0
        const val UV_INDEX_MAX = 4.0
        const val DAYLIGHT_DURATION_SECONDS = 32_400.0
    }

    object Hourly {

        val TIME = listOf("2024-01-01T00:00", "2024-01-01T12:00", "2024-01-01T22:00")
        val CLOUD_COVER_PERCENT = listOf(20, 50, 30)
        val IS_DAY = listOf(0, 1, 0)
        val WIND_SPEED_KPH = listOf(10.0, 15.0, 12.0)
        val PRECIPITATION_PROBABILITY_PERCENT = listOf(10, 20, 15)
        val WIND_GUSTS_KPH = listOf(15.0, 20.0, 18.0)
    }
}

class WeatherRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val weatherApi = mockk<WeatherApi>()
    private val repository = WeatherRepositoryImpl(
        weatherApi = weatherApi,
        ioDispatcher = testDispatcher,
        timeSource = TimeSource { System.currentTimeMillis() },
    )

    @Test
    fun `given successful api response, when getForecast, then returns mapped forecast`() =
        runTest(testDispatcher) {
            stubForecastApiSuccess()

            val result = repository.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )

            assertTrue(result.isSuccess)
            assertEquals(WeatherRepositoryFixture.LATITUDE, result.getOrNull()!!.latitude, 0.0)
        }

    @Test
    fun `given IOException, when getForecast, then returns NetworkUnavailable`() =
        runTest(testDispatcher) {
            stubForecastApiFailure(IOException(WeatherRepositoryFixture.NETWORK_ERROR_MESSAGE))

            val result = repository.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is DomainError.NetworkUnavailable)
        }

    @Test
    fun `given HttpException 429, when getForecast, then returns HttpError with correct code`() =
        runTest(testDispatcher) {
            val httpException = HttpException(
                Response.error<ForecastResponseDto>(
                    WeatherRepositoryFixture.HTTP_ERROR_CODE,
                    WeatherRepositoryFixture.HTTP_ERROR_BODY.toResponseBody(),
                ),
            )
            stubForecastApiFailure(httpException)

            val result = repository.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )

            assertTrue(result.isFailure)
            val error = result.exceptionOrNull() as DomainError.HttpError
            assertEquals(WeatherRepositoryFixture.HTTP_ERROR_CODE, error.code)
        }

    @Test
    fun `given unexpected exception, when getForecast, then returns Unknown error`() =
        runTest(testDispatcher) {
            stubForecastApiFailure(RuntimeException(WeatherRepositoryFixture.UNEXPECTED_ERROR_MESSAGE))

            val result = repository.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is DomainError.Unknown)
        }

    @Test
    fun `given fresh cache, when getForecast called twice with same location, then api called only once`() =
        runTest(testDispatcher) {
            stubForecastApiSuccess()

            repository.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )
            repository.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )

            verifyForecastApiCalled(times = 1)
        }

    @Test
    fun `given fresh cache, when getForecast called with forceRefresh true, then api called again`() =
        runTest(testDispatcher) {
            stubForecastApiSuccess()

            repository.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )
            repository.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
                forceRefresh = true,
            )

            verifyForecastApiCalled(times = 2)
        }

    @Test
    fun `given different location ids, when getForecast called, then api called for each`() =
        runTest(testDispatcher) {
            stubForecastApiSuccess()

            repository.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )
            repository.getForecast(
                locationId = WeatherRepositoryFixture.OTHER_LOCATION_ID,
                latitude = WeatherRepositoryFixture.OTHER_LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )

            verifyForecastApiCalled(times = 2)
        }

    @Test
    fun `given expired cache and network error, when getForecast called, then returns cached forecast marked as cached`() =
        runTest(testDispatcher) {
            var now = 0L
            val repositoryWithTimeSource = WeatherRepositoryImpl(
                weatherApi = weatherApi,
                ioDispatcher = testDispatcher,
                timeSource = TimeSource { now },
            )
            stubForecastApiSuccess()

            val firstResult = repositoryWithTimeSource.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )
            now = WeatherRepositoryImpl.FORECAST_CACHE_TTL_MS + 1L
            stubForecastApiFailure(IOException(WeatherRepositoryFixture.NETWORK_ERROR_MESSAGE))

            val cachedResult = repositoryWithTimeSource.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )

            assertTrue(cachedResult.isSuccess)
            assertTrue(cachedResult.getOrNull()!!.isCached)
            assertEquals(
                firstResult.getOrNull()!!.copy(isCached = true),
                cachedResult.getOrNull(),
            )
        }

    @Test
    fun `given cache older than max staleness and network error, when getForecast called, then returns failure`() =
        runTest(testDispatcher) {
            var now = 0L
            val repositoryWithTimeSource = WeatherRepositoryImpl(
                weatherApi = weatherApi,
                ioDispatcher = testDispatcher,
                timeSource = TimeSource { now },
            )
            stubForecastApiSuccess()

            repositoryWithTimeSource.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )
            now = WeatherRepositoryImpl.FORECAST_CACHE_MAX_STALE_MS + 1L
            stubForecastApiFailure(IOException(WeatherRepositoryFixture.NETWORK_ERROR_MESSAGE))

            val result = repositoryWithTimeSource.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is DomainError.NetworkUnavailable)
        }

    @Test
    fun `given cached forecast served after failure, when getForecast called again, then api is retried`() =
        runTest(testDispatcher) {
            var now = 0L
            val repositoryWithTimeSource = WeatherRepositoryImpl(
                weatherApi = weatherApi,
                ioDispatcher = testDispatcher,
                timeSource = TimeSource { now },
            )
            stubForecastApiSuccess()

            repositoryWithTimeSource.getForecast(
                // populates the cache
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )
            now = WeatherRepositoryImpl.FORECAST_CACHE_TTL_MS + 1L
            stubForecastApiFailure(IOException(WeatherRepositoryFixture.NETWORK_ERROR_MESSAGE))
            repositoryWithTimeSource.getForecast(
                // expired cache, served stale from failed refetch
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )
            stubForecastApiSuccess()
            repositoryWithTimeSource.getForecast(
                // still expired, api retried and succeeds
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )

            verifyForecastApiCalled(times = 3)
        }

    @Test
    fun `given expired cache, when getForecast called, then api called again`() =
        runTest(testDispatcher) {
            var now = 0L
            val repositoryWithTimeSource = WeatherRepositoryImpl(
                weatherApi = weatherApi,
                ioDispatcher = testDispatcher,
                timeSource = TimeSource { now },
            )
            stubForecastApiSuccess()

            repositoryWithTimeSource.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )
            now = WeatherRepositoryImpl.FORECAST_CACHE_TTL_MS + 1L
            repositoryWithTimeSource.getForecast(
                locationId = WeatherRepositoryFixture.LOCATION_ID,
                latitude = WeatherRepositoryFixture.LATITUDE,
                longitude = WeatherRepositoryFixture.LONGITUDE,
            )

            verifyForecastApiCalled(times = 2)
        }

    private fun stubForecastApiSuccess(dto: ForecastResponseDto = buildForecastDto()) {
        coEvery {
            weatherApi.getForecast(
                latitude = any(),
                longitude = any(),
                current = any(),
                daily = any(),
                forecastDays = any(),
                timezone = any(),
            )
        } returns dto
    }

    private fun stubForecastApiFailure(throwable: Throwable) {
        coEvery {
            weatherApi.getForecast(
                latitude = any(),
                longitude = any(),
                current = any(),
                daily = any(),
                forecastDays = any(),
                timezone = any(),
            )
        } throws throwable
    }

    private fun verifyForecastApiCalled(times: Int) {
        coVerify(exactly = times) {
            weatherApi.getForecast(
                latitude = any(),
                longitude = any(),
                current = any(),
                daily = any(),
                forecastDays = any(),
                timezone = any(),
            )
        }
    }

    private fun buildForecastDto() = ForecastResponseDto(
        latitude = WeatherRepositoryFixture.LATITUDE,
        longitude = WeatherRepositoryFixture.LONGITUDE,
        timezone = WeatherRepositoryFixture.TIMEZONE,
        current = CurrentWeatherDto(
            time = WeatherRepositoryFixture.Current.TIME,
            temperatureCelsius = WeatherRepositoryFixture.Current.TEMPERATURE_CELSIUS,
            relativeHumidityPercent = WeatherRepositoryFixture.Current.HUMIDITY_PERCENT,
            apparentTemperatureCelsius = WeatherRepositoryFixture.Current.APPARENT_TEMPERATURE_CELSIUS,
            precipitation = WeatherRepositoryFixture.Current.PRECIPITATION_MM,
            weatherCode = WeatherRepositoryFixture.Current.WEATHER_CODE,
            windSpeedKph = WeatherRepositoryFixture.Current.WIND_SPEED_KPH,
            isDay = WeatherRepositoryFixture.Current.IS_DAY,
        ),
        daily = DailyDataDto(
            time = listOf(WeatherRepositoryFixture.Day.DATE),
            weatherCode = listOf(WeatherRepositoryFixture.Day.WEATHER_CODE),
            maxTemperatureCelsius = listOf(WeatherRepositoryFixture.Day.MAX_TEMPERATURE_CELSIUS),
            minTemperatureCelsius = listOf(WeatherRepositoryFixture.Day.MIN_TEMPERATURE_CELSIUS),
            precipitationSumMm = listOf(WeatherRepositoryFixture.Day.PRECIPITATION_SUM_MM),
            precipitationProbabilityMaxPercent = listOf(WeatherRepositoryFixture.Day.PRECIPITATION_PROBABILITY_PERCENT),
            snowfallSumCm = listOf(WeatherRepositoryFixture.Day.SNOWFALL_SUM_CM),
            windSpeedMaxKph = listOf(WeatherRepositoryFixture.Day.WIND_SPEED_MAX_KPH),
            windGustsMaxKph = listOf(WeatherRepositoryFixture.Day.WIND_GUSTS_MAX_KPH),
            uvIndexMax = listOf(WeatherRepositoryFixture.Day.UV_INDEX_MAX),
            daylightDurationSeconds = listOf(WeatherRepositoryFixture.Day.DAYLIGHT_DURATION_SECONDS),
        ),
        hourly = HourlyDataDto(
            time = WeatherRepositoryFixture.Hourly.TIME,
            cloudCoverPercent = WeatherRepositoryFixture.Hourly.CLOUD_COVER_PERCENT,
            isDay = WeatherRepositoryFixture.Hourly.IS_DAY,
            windSpeedKph = WeatherRepositoryFixture.Hourly.WIND_SPEED_KPH,
            precipitationProbabilityPercent = WeatherRepositoryFixture.Hourly.PRECIPITATION_PROBABILITY_PERCENT,
            windGustsKph = WeatherRepositoryFixture.Hourly.WIND_GUSTS_KPH,
        ),
    )
}
