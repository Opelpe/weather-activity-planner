package com.pnow.weatheractivityplanner.feature.locationsearch

import app.cash.turbine.test
import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.repository.GeocodingRepository
import com.pnow.weatheractivityplanner.domain.usecase.SearchLocationsUseCase
import com.pnow.weatheractivityplanner.feature.common.UiError
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private object LocationSearchViewModelFixture {

    const val SEARCH_QUERY = "Lon"
    const val OTHER_SEARCH_QUERY = "Par"
    const val LOADING_DELAY_MS = 1L

    object London {
        const val ID = 1L
        const val NAME = "London"
        const val COUNTRY = "United Kingdom"
        const val COUNTRY_CODE = "GB"
        const val REGION = "England"
        const val LATITUDE = 51.5
        const val LONGITUDE = -0.1
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LocationSearchViewModelTest {

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
    fun `given matching locations, when query changes, then state emits loading then success`() =
        runTest(testDispatcher) {
            val london = buildLondon()
            val viewModel = buildViewModel(locationResults = listOf(Result.success(listOf(london))))

            viewModel.searchState.test {
                assertEquals(LocationSearchUiState(), awaitItem())

                viewModel.onQueryChanged(LocationSearchViewModelFixture.SEARCH_QUERY)

                assertEquals(LocationSearchViewModelFixture.SEARCH_QUERY, awaitItem().searchQuery)
                assertTrue(awaitItem().isLoading)

                val success = awaitItem()
                assertFalse(success.isLoading)
                assertEquals(listOf(london.toUiModel()), success.locations)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given repository error, when query changes, then state emits loading then error`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel(
                locationResults = listOf(Result.failure(DomainError.NetworkUnavailable())),
            )

            viewModel.searchState.test {
                assertEquals(LocationSearchUiState(), awaitItem())

                viewModel.onQueryChanged(LocationSearchViewModelFixture.SEARCH_QUERY)

                assertEquals(LocationSearchViewModelFixture.SEARCH_QUERY, awaitItem().searchQuery)
                assertTrue(awaitItem().isLoading)

                val error = awaitItem()
                assertFalse(error.isLoading)
                assertEquals(UiError.NetworkUnavailable, error.error)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given results are showing, when query is cleared, then locations and loading are reset`() =
        runTest(testDispatcher) {
            val london = buildLondon()
            val viewModel = buildViewModel(locationResults = listOf(Result.success(listOf(london))))

            viewModel.searchState.test {
                awaitItem() // initial state

                viewModel.onQueryChanged(LocationSearchViewModelFixture.SEARCH_QUERY)
                awaitItem() // searchQuery updated
                awaitItem() // loading
                awaitItem() // success

                viewModel.onQueryChanged("")

                assertEquals("", awaitItem().searchQuery)
                val cleared = awaitItem()
                assertFalse(cleared.isLoading)
                assertEquals(emptyList<LocationUiModel>(), cleared.locations)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given search failed, when onRetry is called, then state emits loading then success`() =
        runTest(testDispatcher) {
            val london = buildLondon()
            val viewModel = buildViewModel(
                locationResults = listOf(
                    Result.failure(DomainError.NetworkUnavailable()),
                    Result.success(listOf(london)),
                ),
            )

            viewModel.searchState.test {
                awaitItem() // initial state

                viewModel.onQueryChanged(LocationSearchViewModelFixture.SEARCH_QUERY)
                awaitItem() // searchQuery updated
                awaitItem() // loading
                assertEquals(UiError.NetworkUnavailable, awaitItem().error)

                viewModel.onRetry()

                assertTrue(awaitItem().isLoading)
                val success = awaitItem()
                assertFalse(success.isLoading)
                assertEquals(listOf(london.toUiModel()), success.locations)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given query already searched successfully, when same query is searched again, then searchLocations is not called again`() =
        runTest(testDispatcher) {
            val london = buildLondon()
            val repository = FakeGeocodingRepository(
                listOf(
                    Result.success(listOf(london)),
                    Result.failure(DomainError.NetworkUnavailable()),
                ),
            )
            val viewModel = buildViewModel(repository)

            viewModel.onQueryChanged(LocationSearchViewModelFixture.SEARCH_QUERY)
            advanceUntilIdle()

            viewModel.onQueryChanged(LocationSearchViewModelFixture.OTHER_SEARCH_QUERY)
            advanceUntilIdle()

            viewModel.onQueryChanged(LocationSearchViewModelFixture.SEARCH_QUERY)
            advanceUntilIdle()

            assertEquals(
                listOf(
                    LocationSearchViewModelFixture.SEARCH_QUERY,
                    LocationSearchViewModelFixture.OTHER_SEARCH_QUERY,
                ),
                repository.searchedQueries,
            )
        }

    private fun buildViewModel(
        locationResults: List<Result<List<Location>>> = listOf(Result.success(emptyList())),
    ) = buildViewModel(FakeGeocodingRepository(locationResults))

    private fun buildViewModel(
        repository: GeocodingRepository,
    ) = LocationSearchViewModel(
        searchLocationsUseCase = SearchLocationsUseCase(repository),
        defaultDispatcher = testDispatcher,
    )

    private fun buildLondon() = Location(
        id = LocationSearchViewModelFixture.London.ID,
        name = LocationSearchViewModelFixture.London.NAME,
        latitude = LocationSearchViewModelFixture.London.LATITUDE,
        longitude = LocationSearchViewModelFixture.London.LONGITUDE,
        country = LocationSearchViewModelFixture.London.COUNTRY,
        countryCode = LocationSearchViewModelFixture.London.COUNTRY_CODE,
        region = LocationSearchViewModelFixture.London.REGION,
    )

    private class FakeGeocodingRepository(
        private val results: List<Result<List<Location>>>,
    ) : GeocodingRepository {
        private var callIndex = 0
        val searchedQueries = mutableListOf<String>()

        override suspend fun searchLocations(query: String, count: Int): Result<List<Location>> {
            searchedQueries.add(query)
            delay(LocationSearchViewModelFixture.LOADING_DELAY_MS.milliseconds)
            val result = results[callIndex]
            callIndex = minOf(callIndex + 1, results.size - 1)
            return result
        }
    }
}
