package com.pnow.weatheractivityplanner.feature.weatheractivity

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.pnow.weatheractivityplanner.domain.model.Activities
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRanking
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.CurrentWeather
import com.pnow.weatheractivityplanner.domain.model.Forecast
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import com.pnow.weatheractivityplanner.domain.repository.WeatherRepository
import com.pnow.weatheractivityplanner.domain.usecase.ActivitiesRankingCalculator
import com.pnow.weatheractivityplanner.domain.usecase.GetActivityRankingsUseCase
import com.pnow.weatheractivityplanner.domain.usecase.GetForecastUseCase
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.toUiModel
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.toUiModels
import com.pnow.weatheractivityplanner.navigation.RouteArgKeys
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
        const val APPARENT_TEMPERATURE_CELSIUS = 21.0
        const val HUMIDITY_PERCENT = 50
        const val PRECIPITATION_MM = 0.0
        const val WIND_SPEED_KPH = 10.0
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
    private val calculator = ActivitiesRankingCalculator()

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
                    calculator.calculate(forecast.current).toUiModels(),
                    success.ranking,
                )

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
    fun `given rankings with a unique top score, when toUiModels, then only the highest scoring activity is top ranked`() {
        val rankings = listOf(
            buildRanking(Activities.OUTDOOR_SIGHTSEEING, WeatherActivityViewModelFixture.UniqueTopScore.HIGHEST),
            buildRanking(Activities.INDOOR_SIGHTSEEING, WeatherActivityViewModelFixture.UniqueTopScore.SECOND),
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
            buildRanking(Activities.INDOOR_SIGHTSEEING, WeatherActivityViewModelFixture.TiedTopScore.HIGHEST),
            buildRanking(Activities.SKIING, WeatherActivityViewModelFixture.TiedTopScore.LOWEST),
        )

        val uiModels = rankings.toUiModels()

        assertEquals(listOf(true, true, false), uiModels.map { it.isTopRanked })
    }

    private fun buildRanking(activities: Activities, score: Float) = ActivitiesRanking(
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
    ) = WeatherRecommendationViewModel(
        savedStateHandle = savedStateHandle,
        getActivityRankingsUseCase = GetActivityRankingsUseCase(
            getForecastUseCase = GetForecastUseCase(FakeWeatherRepository(forecastResults)),
            activitiesRankingCalculator = calculator,
        ),
    )

    private fun buildSavedStateHandle() = SavedStateHandle(
        mapOf(
            RouteArgKeys.LOCATION_ID to WeatherActivityViewModelFixture.Paris.ID,
            RouteArgKeys.LOCATION_NAME to WeatherActivityViewModelFixture.Paris.NAME,
            RouteArgKeys.LOCATION_COUNTRY to WeatherActivityViewModelFixture.Paris.COUNTRY,
            RouteArgKeys.LATITUDE to WeatherActivityViewModelFixture.Paris.LATITUDE,
            RouteArgKeys.LONGITUDE to WeatherActivityViewModelFixture.Paris.LONGITUDE,
        ),
    )

    private fun buildForecast() = Forecast(
        latitude = WeatherActivityViewModelFixture.Paris.LATITUDE,
        longitude = WeatherActivityViewModelFixture.Paris.LONGITUDE,
        timezone = WeatherActivityViewModelFixture.Paris.TIMEZONE,
        current = CurrentWeather(
            temperatureCelsius = WeatherActivityViewModelFixture.Paris.TEMPERATURE_CELSIUS,
            apparentTemperatureCelsius = WeatherActivityViewModelFixture.Paris.APPARENT_TEMPERATURE_CELSIUS,
            relativeHumidityPercent = WeatherActivityViewModelFixture.Paris.HUMIDITY_PERCENT,
            precipitationMm = WeatherActivityViewModelFixture.Paris.PRECIPITATION_MM,
            windSpeedKph = WeatherActivityViewModelFixture.Paris.WIND_SPEED_KPH,
            condition = WeatherCondition.Clear,
            isDay = true,
        ),
        daily = emptyList(),
    )

    private class FakeWeatherRepository(
        private val results: List<Result<Forecast>>,
    ) : WeatherRepository {
        private var callIndex = 0

        override suspend fun getForecast(latitude: Double, longitude: Double): Result<Forecast> {
            delay(WeatherActivityViewModelFixture.LOADING_DELAY_MS.milliseconds)
            val result = results[callIndex]
            callIndex = minOf(callIndex + 1, results.size - 1)
            return result
        }
    }
}
