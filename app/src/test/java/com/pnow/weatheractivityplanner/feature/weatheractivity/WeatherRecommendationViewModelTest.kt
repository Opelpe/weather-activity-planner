package com.pnow.weatheractivityplanner.feature.weatheractivity

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.pnow.weatheractivityplanner.domain.model.Activities
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRanking
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.CurrentWeather
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.Forecast
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import com.pnow.weatheractivityplanner.domain.ranking.ActivitiesRankingCalculator
import com.pnow.weatheractivityplanner.domain.ranking.CyclingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.IndoorSightseeingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.OutdoorSightseeingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.SkiingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.SurfingDayScorer
import com.pnow.weatheractivityplanner.domain.repository.ConnectivityRepository
import com.pnow.weatheractivityplanner.domain.repository.WeatherRepository
import com.pnow.weatheractivityplanner.domain.usecase.GetActivityRankingsUseCase
import com.pnow.weatheractivityplanner.domain.usecase.GetForecastUseCase
import com.pnow.weatheractivityplanner.domain.usecase.ObserveConnectivityLossUseCase
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.toUiModel
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.toUiModels
import com.pnow.weatheractivityplanner.navigation.LocationArgs
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

private object WeatherActivityViewModelFixture {

    const val LOADING_DELAY_MS = 1L

    object Paris {

        const val ID = 2L
        const val NAME = "Paris"
        const val COUNTRY = "France"
        const val LATITUDE = 48.85
        const val LONGITUDE = 2.35
        const val TIMEZONE = "Europe/Paris"
        const val TEMPERATURE_CELSIUS = 22.0
        const val REFRESHED_TEMPERATURE_CELSIUS = 25.0
        const val APPARENT_TEMPERATURE_CELSIUS = 21.0
        const val HUMIDITY_PERCENT = 50
        const val PRECIPITATION_MM = 0.0
        const val WIND_SPEED_KPH = 10.0
    }

    object Day1 {

        const val DATE = "2026-06-15"
        const val MAX_TEMPERATURE_CELSIUS = 24.0
        const val MIN_TEMPERATURE_CELSIUS = 14.0
        const val PRECIPITATION_SUM_MM = 0.0
        const val PRECIPITATION_PROBABILITY_PERCENT = 10
        const val SNOWFALL_SUM_CM = 0.0
        const val WIND_SPEED_MAX_KPH = 12.0
        const val WIND_GUSTS_MAX_KPH = 20.0
        const val UV_INDEX_MAX = 5.0
        const val DAYLIGHT_DURATION_HOURS = 15.5
    }

    val RANKING_REASON = ActivitiesRankingReason.OUTDOOR_NONE

    object UniqueTopScore {

        const val HIGHEST = 90f
        const val SECOND = 60f
        const val THIRD = 30f
        const val LOWEST = 10f
    }

    object TiedTopScore {

        const val HIGHEST = 55f
        const val LOWEST = 0f
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRecommendationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val calculator = ActivitiesRankingCalculator(
        skiingDayScorer = SkiingDayScorer(),
        surfingDayScorer = SurfingDayScorer(),
        outdoorSightseeingDayScorer = OutdoorSightseeingDayScorer(),
        indoorSightseeingDayScorer = IndoorSightseeingDayScorer(),
        cyclingDayScorer = CyclingDayScorer(),
    )

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

            viewModel.state.test {
                assertEquals(buildInitialState(), awaitItem())
                assertTrue(awaitItem().isLoading)

                val success = awaitItem()
                assertFalse(success.isLoading)
                assertEquals(forecast.current.toUiModel(), success.currentWeather)
                assertEquals(
                    calculator.calculate(forecast.daily).toUiModels(),
                    success.ranking,
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given cached forecast returned, when initialized, then state shows content and emits cached data notice`() =
        runTest(testDispatcher) {
            val cachedForecast = buildForecast().copy(isCached = true)
            val viewModel = buildViewModel(forecastResults = listOf(Result.success(cachedForecast)))

            viewModel.cachedDataNotices.test {
                viewModel.state.test {
                    assertEquals(buildInitialState(), awaitItem())
                    assertTrue(awaitItem().isLoading)

                    val success = awaitItem()
                    assertFalse(success.isLoading)
                    assertEquals(cachedForecast.current.toUiModel(), success.currentWeather)
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

            viewModel.state.test {
                assertEquals(buildInitialState(), awaitItem())
                assertTrue(awaitItem().isLoading)

                val error = awaitItem()
                assertFalse(error.isLoading)
                assertEquals(UiError.NetworkUnavailable, error.error)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given missing navigation arguments, when initialized, then state shows invalid navigation arguments error`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel(savedStateHandle = SavedStateHandle())

            viewModel.state.test {
                assertEquals(
                    WeatherRecommendationUiState(error = UiError.InvalidNavigationArguments),
                    awaitItem(),
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given rankings failed, when onRetry is called, then state emits loading then success`() =
        runTest(testDispatcher) {
            val forecast = buildForecast()
            val viewModel = buildViewModel(
                forecastResults = listOf(
                    Result.failure(DomainError.NetworkUnavailable()),
                    Result.success(forecast),
                ),
            )

            viewModel.state.test {
                awaitItem() // initial state
                awaitItem() // loading
                assertEquals(UiError.NetworkUnavailable, awaitItem().error)

                viewModel.onRetry()

                assertTrue(awaitItem().isLoading)
                val success = awaitItem()
                assertFalse(success.isLoading)
                assertEquals(forecast.current.toUiModel(), success.currentWeather)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given successful forecast, when onRefresh is called, then state emits refreshing then success without loading`() =
        runTest(testDispatcher) {
            val firstForecast = buildForecast()
            val refreshedForecast = buildForecast(
                temperatureCelsius = WeatherActivityViewModelFixture.Paris.REFRESHED_TEMPERATURE_CELSIUS,
            )
            val viewModel = buildViewModel(
                forecastResults = listOf(
                    Result.success(firstForecast),
                    Result.success(refreshedForecast),
                ),
            )

            viewModel.state.test {
                awaitItem() // initial state
                awaitItem() // loading
                awaitItem() // first success

                viewModel.onRefresh()

                val refreshing = awaitItem()
                assertTrue(refreshing.isRefreshing)
                assertFalse(refreshing.isLoading)

                val refreshed = awaitItem()
                assertFalse(refreshed.isRefreshing)
                assertFalse(refreshed.isLoading)
                assertEquals(refreshedForecast.current.toUiModel(), refreshed.currentWeather)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given rankings failed, when onRefresh is called, then state emits refreshing then error`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel(
                forecastResults = listOf(
                    Result.success(buildForecast()),
                    Result.failure(DomainError.NetworkUnavailable()),
                ),
            )

            viewModel.state.test {
                awaitItem() // initial state
                awaitItem() // loading
                awaitItem() // first success

                viewModel.onRefresh()

                val refreshing = awaitItem()
                assertTrue(refreshing.isRefreshing)

                val error = awaitItem()
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
                viewModel.state.test {
                    awaitItem() // initial state
                    awaitItem() // loading
                    awaitItem() // first success

                    viewModel.onRefresh()

                    val refreshing = awaitItem()
                    assertTrue(refreshing.isRefreshing)

                    val refreshed = awaitItem()
                    assertFalse(refreshed.isRefreshing)
                    assertEquals(cachedForecast.current.toUiModel(), refreshed.currentWeather)
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
                viewModel.state.test {
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
                viewModel.state.test {
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
                viewModel.state.test {
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

    @Test
    fun `given rankings with a unique top score, when toUiModels, then only the highest scoring activity is top ranked`() {
        val rankings = listOf(
            buildRanking(
                Activities.OUTDOOR_SIGHTSEEING,
                WeatherActivityViewModelFixture.UniqueTopScore.HIGHEST,
            ),
            buildRanking(
                Activities.INDOOR_SIGHTSEEING,
                WeatherActivityViewModelFixture.UniqueTopScore.SECOND,
            ),
            buildRanking(Activities.SURFING, WeatherActivityViewModelFixture.UniqueTopScore.THIRD),
            buildRanking(Activities.SKIING, WeatherActivityViewModelFixture.UniqueTopScore.LOWEST),
        )

        val uiModels = rankings.toUiModels()

        assertEquals(listOf(true, false, false, false), uiModels.map { it.isTopRanked })
    }

    @Test
    fun `given rankings with a tie for the top score, when toUiModels, then all tied activities are top ranked`() {
        val rankings = listOf(
            buildRanking(Activities.SURFING, WeatherActivityViewModelFixture.TiedTopScore.HIGHEST),
            buildRanking(
                Activities.INDOOR_SIGHTSEEING,
                WeatherActivityViewModelFixture.TiedTopScore.HIGHEST,
            ),
            buildRanking(Activities.SKIING, WeatherActivityViewModelFixture.TiedTopScore.LOWEST),
        )

        val uiModels = rankings.toUiModels()

        assertEquals(listOf(true, true, false), uiModels.map { it.isTopRanked })
    }

    private fun buildRanking(
        activities: Activities,
        score: Float,
    ) = ActivitiesRanking(
        activities = activities,
        score = score,
        reason = WeatherActivityViewModelFixture.RANKING_REASON,
    )

    private fun buildInitialState() = WeatherRecommendationUiState(
        locationName = WeatherActivityViewModelFixture.Paris.NAME,
        locationCountry = WeatherActivityViewModelFixture.Paris.COUNTRY,
    )

    private fun buildViewModel(
        forecastResults: List<Result<Forecast>> = listOf(Result.success(buildForecast())),
        savedStateHandle: SavedStateHandle = buildSavedStateHandle(),
        connectivityRepository: ConnectivityRepository = FakeConnectivityRepository(),
    ) = WeatherRecommendationViewModel(
        savedStateHandle = savedStateHandle,
        getActivityRankingsUseCase = GetActivityRankingsUseCase(
            getForecastUseCase = GetForecastUseCase(FakeWeatherRepository(forecastResults)),
            activitiesRankingCalculator = calculator,
        ),
        observeConnectivityLossUseCase = ObserveConnectivityLossUseCase(connectivityRepository),
    )

    private fun buildSavedStateHandle() = SavedStateHandle(
        mapOf(
            LocationArgs::locationId.name to WeatherActivityViewModelFixture.Paris.ID,
            LocationArgs::locationName.name to WeatherActivityViewModelFixture.Paris.NAME,
            LocationArgs::locationCountry.name to WeatherActivityViewModelFixture.Paris.COUNTRY,
            LocationArgs::latitude.name to WeatherActivityViewModelFixture.Paris.LATITUDE,
            LocationArgs::longitude.name to WeatherActivityViewModelFixture.Paris.LONGITUDE,
        ),
    )

    private fun buildForecast(
        temperatureCelsius: Double = WeatherActivityViewModelFixture.Paris.TEMPERATURE_CELSIUS,
    ) = Forecast(
        latitude = WeatherActivityViewModelFixture.Paris.LATITUDE,
        longitude = WeatherActivityViewModelFixture.Paris.LONGITUDE,
        timezone = WeatherActivityViewModelFixture.Paris.TIMEZONE,
        current = CurrentWeather(
            temperatureCelsius = temperatureCelsius,
            apparentTemperatureCelsius = WeatherActivityViewModelFixture.Paris.APPARENT_TEMPERATURE_CELSIUS,
            relativeHumidityPercent = WeatherActivityViewModelFixture.Paris.HUMIDITY_PERCENT,
            precipitationMm = WeatherActivityViewModelFixture.Paris.PRECIPITATION_MM,
            windSpeedKph = WeatherActivityViewModelFixture.Paris.WIND_SPEED_KPH,
            condition = WeatherCondition.Clear,
            isDay = true,
        ),
        daily = listOf(
            DailyForecast(
                date = WeatherActivityViewModelFixture.Day1.DATE,
                maxTemperatureCelsius = WeatherActivityViewModelFixture.Day1.MAX_TEMPERATURE_CELSIUS,
                minTemperatureCelsius = WeatherActivityViewModelFixture.Day1.MIN_TEMPERATURE_CELSIUS,
                precipitationSumMm = WeatherActivityViewModelFixture.Day1.PRECIPITATION_SUM_MM,
                precipitationProbabilityMaxPercent = WeatherActivityViewModelFixture.Day1.PRECIPITATION_PROBABILITY_PERCENT,
                snowfallSumCm = WeatherActivityViewModelFixture.Day1.SNOWFALL_SUM_CM,
                windSpeedMaxKph = WeatherActivityViewModelFixture.Day1.WIND_SPEED_MAX_KPH,
                windGustsMaxKph = WeatherActivityViewModelFixture.Day1.WIND_GUSTS_MAX_KPH,
                uvIndexMax = WeatherActivityViewModelFixture.Day1.UV_INDEX_MAX,
                daylightDurationHours = WeatherActivityViewModelFixture.Day1.DAYLIGHT_DURATION_HOURS,
                condition = WeatherCondition.Clear,
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
            delay(WeatherActivityViewModelFixture.LOADING_DELAY_MS.milliseconds)
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
