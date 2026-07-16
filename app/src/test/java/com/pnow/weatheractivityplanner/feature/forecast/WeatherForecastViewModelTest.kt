package com.pnow.weatheractivityplanner.feature.forecast

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.pnow.weatheractivityplanner.domain.model.CurrentWeather
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.Forecast
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import com.pnow.weatheractivityplanner.domain.repository.ConnectivityRepository
import com.pnow.weatheractivityplanner.domain.repository.WeatherRepository
import com.pnow.weatheractivityplanner.domain.usecase.GetForecastUseCase
import com.pnow.weatheractivityplanner.feature.common.UiError
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private object WeatherForecastViewModelFixture {

    const val LOADING_DELAY_MS = 1L

    object Paris {

        const val ID = 3L
        const val NAME = "Paris"
        const val COUNTRY = "France"
        const val LATITUDE = 48.85
        const val LONGITUDE = 2.35
        const val TIMEZONE = "Europe/Paris"
        const val TEMPERATURE_CELSIUS = 22.0
        const val APPARENT_TEMPERATURE_CELSIUS = 21.0
        const val HUMIDITY_PERCENT = 50
        const val PRECIPITATION_MM = 0.0
        const val WIND_SPEED_KPH = 10.0
    }

    object Day1 {

        const val DATE = "2026-06-12"
        const val MAX_TEMPERATURE_CELSIUS = 24.0
        const val REFRESHED_MAX_TEMPERATURE_CELSIUS = 27.0
        const val MIN_TEMPERATURE_CELSIUS = 14.0
        const val PRECIPITATION_SUM_MM = 0.0
        const val PRECIPITATION_PROBABILITY_PERCENT = 10
        const val SNOWFALL_SUM_CM = 0.0
        const val WIND_SPEED_MAX_KPH = 12.0
        const val WIND_GUSTS_MAX_KPH = 20.0
        const val UV_INDEX_MAX = 5.0
        const val DAYLIGHT_DURATION_HOURS = 15.5
    }

    object Day2 {

        const val DATE = "2026-06-13"
        const val MAX_TEMPERATURE_CELSIUS = 21.0
        const val MIN_TEMPERATURE_CELSIUS = 12.0
        const val PRECIPITATION_SUM_MM = 2.0
        const val PRECIPITATION_PROBABILITY_PERCENT = 40
        const val SNOWFALL_SUM_CM = 0.0
        const val WIND_SPEED_MAX_KPH = 18.0
        const val WIND_GUSTS_MAX_KPH = 30.0
        const val UV_INDEX_MAX = 3.0
        const val DAYLIGHT_DURATION_HOURS = 15.4
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherForecastViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given successful forecast, when initialized, then state emits loading then success`() =
        runTest(testDispatcher) {
            val forecast = buildForecast()
            val viewModel = buildViewModel(forecastResults = listOf(Result.success(forecast)))

            viewModel.forecastState.test {
                assertEquals(buildInitialState(), awaitItem()) // initial state
                assertTrue(awaitItem().isLoading) // loading

                val success = awaitItem() // success
                assertFalse(success.isLoading)
                assertEquals(forecast.daily.map { it.toUiModel() }, success.dailyForecast)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given cached forecast returned, when initialized, then state shows content and emits cached data notice`() =
        runTest(testDispatcher) {
            val cachedForecast = buildForecast().copy(isCached = true)
            val viewModel = buildViewModel(forecastResults = listOf(Result.success(cachedForecast)))

            viewModel.cachedDataNotices.test {
                viewModel.forecastState.test {
                    assertEquals(buildInitialState(), awaitItem()) // initial state
                    assertTrue(awaitItem().isLoading) // loading

                    val success = awaitItem() // success
                    assertFalse(success.isLoading)
                    assertEquals(cachedForecast.daily.map { it.toUiModel() }, success.dailyForecast)
                    assertEquals(null, success.error)

                    cancelAndIgnoreRemainingEvents()
                }

                assertEquals(Unit, awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given network error, when initialized, then state emits loading then error`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel(
                forecastResults = listOf(Result.failure(DomainError.NetworkUnavailable())),
            )

            viewModel.forecastState.test {
                assertEquals(buildInitialState(), awaitItem()) // initial state
                assertTrue(awaitItem().isLoading) // loading

                val error = awaitItem() // error
                assertFalse(error.isLoading)
                assertEquals(UiError.NetworkUnavailable, error.error)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given missing navigation arguments, when initialized, then state shows invalid navigation arguments error`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel(savedStateHandle = SavedStateHandle())

            viewModel.forecastState.test {
                assertEquals(
                    WeatherForecastUiState(error = UiError.InvalidNavigationArguments),
                    awaitItem(), // initial state (invalid navigation arguments)
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given forecast failed, when onRetry is called, then state emits loading then success`() =
        runTest(testDispatcher) {
            val forecast = buildForecast()
            val viewModel = buildViewModel(
                forecastResults = listOf(
                    Result.failure(DomainError.NetworkUnavailable()),
                    Result.success(forecast),
                ),
            )

            viewModel.forecastState.test {
                awaitItem() // initial state
                awaitItem() // loading
                assertEquals(
                    UiError.NetworkUnavailable,
                    awaitItem().error,
                ) // error after first failed load

                viewModel.onRetry()

                assertTrue(awaitItem().isLoading) // loading after retry
                val success = awaitItem() // success after retry
                assertFalse(success.isLoading)
                assertEquals(forecast.daily.map { it.toUiModel() }, success.dailyForecast)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given successful forecast, when onRefresh is called, then state emits refreshing then success without loading`() =
        runTest(testDispatcher) {
            val firstForecast = buildForecast()
            val refreshedForecast = buildForecast(
                day1MaxTemperatureCelsius = WeatherForecastViewModelFixture.Day1.REFRESHED_MAX_TEMPERATURE_CELSIUS,
            )
            val viewModel = buildViewModel(
                forecastResults = listOf(
                    Result.success(firstForecast),
                    Result.success(refreshedForecast),
                ),
            )

            viewModel.forecastState.test {
                awaitItem() // initial state
                awaitItem() // loading
                awaitItem() // first success

                viewModel.onRefresh()

                val refreshing = awaitItem() // refreshing
                assertTrue(refreshing.isRefreshing)
                assertFalse(refreshing.isLoading)

                val refreshed = awaitItem() // success after refresh
                assertFalse(refreshed.isRefreshing)
                assertFalse(refreshed.isLoading)
                assertEquals(
                    refreshedForecast.daily.map { it.toUiModel() },
                    refreshed.dailyForecast,
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given forecast failed, when onRefresh is called, then state emits refreshing then error`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel(
                forecastResults = listOf(
                    Result.success(buildForecast()),
                    Result.failure(DomainError.NetworkUnavailable()),
                ),
            )

            viewModel.forecastState.test {
                awaitItem() // initial state
                awaitItem() // loading
                awaitItem() // first success

                viewModel.onRefresh()

                val refreshing = awaitItem() // refreshing
                assertTrue(refreshing.isRefreshing)

                val error = awaitItem() // error after refresh
                assertFalse(error.isRefreshing)
                assertEquals(UiError.NetworkUnavailable, error.error)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given cached forecast returned, when onRefresh is called, then state keeps content and emits cached data notice`() =
        runTest(testDispatcher) {
            val firstForecast = buildForecast()
            val cachedForecast = firstForecast.copy(isCached = true)
            val viewModel = buildViewModel(
                forecastResults = listOf(
                    Result.success(firstForecast),
                    Result.success(cachedForecast),
                ),
            )

            viewModel.cachedDataNotices.test {
                viewModel.forecastState.test {
                    awaitItem() // initial state
                    awaitItem() // loading
                    awaitItem() // first success

                    viewModel.onRefresh()

                    val refreshing = awaitItem() // refreshing
                    assertTrue(refreshing.isRefreshing)

                    val refreshed = awaitItem() // success after refresh
                    assertFalse(refreshed.isRefreshing)
                    assertEquals(
                        cachedForecast.daily.map { it.toUiModel() },
                        refreshed.dailyForecast,
                    )
                    assertEquals(null, refreshed.error)

                    cancelAndIgnoreRemainingEvents()
                }

                assertEquals(Unit, awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given cached forecast returned on consecutive refreshes, when onRefresh is called each time, then a new cached data notice is emitted every time`() =
        runTest(testDispatcher) {
            val firstForecast = buildForecast()
            val cachedForecast = firstForecast.copy(isCached = true)
            val viewModel = buildViewModel(
                forecastResults = listOf(
                    Result.success(firstForecast),
                    Result.success(cachedForecast),
                    Result.success(cachedForecast),
                ),
            )

            viewModel.cachedDataNotices.test {
                viewModel.forecastState.test {
                    awaitItem() // initial state
                    awaitItem() // loading
                    awaitItem() // first success

                    viewModel.onRefresh()
                    awaitItem() // refreshing
                    awaitItem() // first cached success

                    viewModel.onRefresh()
                    awaitItem() // refreshing
                    awaitItem() // second cached success

                    cancelAndIgnoreRemainingEvents()
                }

                assertEquals(Unit, awaitItem())
                assertEquals(Unit, awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given content already shown, when connectivity is lost, then a cached data notice is emitted`() =
        runTest(testDispatcher) {
            val connectivity = MutableStateFlow(true)
            val viewModel = buildViewModel(
                forecastResults = listOf(Result.success(buildForecast())),
                connectivityRepository = FakeConnectivityRepository(connectivity),
            )

            viewModel.cachedDataNotices.test {
                viewModel.forecastState.test {
                    awaitItem() // initial state
                    awaitItem() // loading
                    awaitItem() // success

                    connectivity.value = false

                    cancelAndIgnoreRemainingEvents()
                }

                assertEquals(Unit, awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given no content yet, when connectivity is lost, then no cached data notice is emitted`() =
        runTest(testDispatcher) {
            val connectivity = MutableStateFlow(true)
            val viewModel = buildViewModel(
                forecastResults = listOf(Result.failure(DomainError.NetworkUnavailable())),
                connectivityRepository = FakeConnectivityRepository(connectivity),
            )

            viewModel.cachedDataNotices.test {
                viewModel.forecastState.test {
                    awaitItem() // initial state
                    awaitItem() // loading
                    awaitItem() // error, no content

                    connectivity.value = false

                    cancelAndIgnoreRemainingEvents()
                }

                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun buildInitialState() = WeatherForecastUiState(
        locationName = WeatherForecastViewModelFixture.Paris.NAME,
        locationCountry = WeatherForecastViewModelFixture.Paris.COUNTRY,
    )

    private fun buildViewModel(
        forecastResults: List<Result<Forecast>> = listOf(Result.success(buildForecast())),
        savedStateHandle: SavedStateHandle = buildSavedStateHandle(),
        connectivityRepository: ConnectivityRepository = FakeConnectivityRepository(),
    ) = WeatherForecastViewModel(
        savedStateHandle = savedStateHandle,
        getForecastUseCase = GetForecastUseCase(FakeWeatherRepository(forecastResults)),
        connectivityRepository = connectivityRepository,
    )

    private fun buildSavedStateHandle() = SavedStateHandle(
        mapOf(
            "locationId" to WeatherForecastViewModelFixture.Paris.ID,
            "locationName" to WeatherForecastViewModelFixture.Paris.NAME,
            "locationCountry" to WeatherForecastViewModelFixture.Paris.COUNTRY,
            "latitude" to WeatherForecastViewModelFixture.Paris.LATITUDE,
            "longitude" to WeatherForecastViewModelFixture.Paris.LONGITUDE,
        ),
    )

    private fun buildForecast(
        day1MaxTemperatureCelsius: Double = WeatherForecastViewModelFixture.Day1.MAX_TEMPERATURE_CELSIUS,
    ) = Forecast(
        latitude = WeatherForecastViewModelFixture.Paris.LATITUDE,
        longitude = WeatherForecastViewModelFixture.Paris.LONGITUDE,
        timezone = WeatherForecastViewModelFixture.Paris.TIMEZONE,
        current = CurrentWeather(
            temperatureCelsius = WeatherForecastViewModelFixture.Paris.TEMPERATURE_CELSIUS,
            apparentTemperatureCelsius = WeatherForecastViewModelFixture.Paris.APPARENT_TEMPERATURE_CELSIUS,
            relativeHumidityPercent = WeatherForecastViewModelFixture.Paris.HUMIDITY_PERCENT,
            precipitationMm = WeatherForecastViewModelFixture.Paris.PRECIPITATION_MM,
            windSpeedKph = WeatherForecastViewModelFixture.Paris.WIND_SPEED_KPH,
            condition = WeatherCondition.Clear,
            isDay = true,
        ),
        daily = listOf(
            DailyForecast(
                date = WeatherForecastViewModelFixture.Day1.DATE,
                maxTemperatureCelsius = day1MaxTemperatureCelsius,
                minTemperatureCelsius = WeatherForecastViewModelFixture.Day1.MIN_TEMPERATURE_CELSIUS,
                precipitationSumMm = WeatherForecastViewModelFixture.Day1.PRECIPITATION_SUM_MM,
                precipitationProbabilityMaxPercent = WeatherForecastViewModelFixture.Day1.PRECIPITATION_PROBABILITY_PERCENT,
                snowfallSumCm = WeatherForecastViewModelFixture.Day1.SNOWFALL_SUM_CM,
                windSpeedMaxKph = WeatherForecastViewModelFixture.Day1.WIND_SPEED_MAX_KPH,
                windGustsMaxKph = WeatherForecastViewModelFixture.Day1.WIND_GUSTS_MAX_KPH,
                uvIndexMax = WeatherForecastViewModelFixture.Day1.UV_INDEX_MAX,
                daylightDurationHours = WeatherForecastViewModelFixture.Day1.DAYLIGHT_DURATION_HOURS,
                condition = WeatherCondition.Clear,
            ),
            DailyForecast(
                date = WeatherForecastViewModelFixture.Day2.DATE,
                maxTemperatureCelsius = WeatherForecastViewModelFixture.Day2.MAX_TEMPERATURE_CELSIUS,
                minTemperatureCelsius = WeatherForecastViewModelFixture.Day2.MIN_TEMPERATURE_CELSIUS,
                precipitationSumMm = WeatherForecastViewModelFixture.Day2.PRECIPITATION_SUM_MM,
                precipitationProbabilityMaxPercent = WeatherForecastViewModelFixture.Day2.PRECIPITATION_PROBABILITY_PERCENT,
                snowfallSumCm = WeatherForecastViewModelFixture.Day2.SNOWFALL_SUM_CM,
                windSpeedMaxKph = WeatherForecastViewModelFixture.Day2.WIND_SPEED_MAX_KPH,
                windGustsMaxKph = WeatherForecastViewModelFixture.Day2.WIND_GUSTS_MAX_KPH,
                uvIndexMax = WeatherForecastViewModelFixture.Day2.UV_INDEX_MAX,
                daylightDurationHours = WeatherForecastViewModelFixture.Day2.DAYLIGHT_DURATION_HOURS,
                condition = WeatherCondition.LightRain,
            ),
        ),
    )

    private class FakeWeatherRepository(
        private val results: List<Result<Forecast>>,
    ) : WeatherRepository {

        private var callIndex = 0

        override suspend fun getForecast(
            locationId: Long,
            latitude: Double,
            longitude: Double,
            forceRefresh: Boolean,
        ): Result<Forecast> {
            delay(WeatherForecastViewModelFixture.LOADING_DELAY_MS.milliseconds)
            val result = results[callIndex]
            callIndex = minOf(callIndex + 1, results.size - 1)
            return result
        }
    }

    private class FakeConnectivityRepository(
        private val connectivityFlow: Flow<Boolean> = MutableStateFlow(true),
    ) : ConnectivityRepository {

        override fun isConnected(): Flow<Boolean> = connectivityFlow
    }
}
