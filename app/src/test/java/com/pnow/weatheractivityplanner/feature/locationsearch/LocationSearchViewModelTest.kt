package com.pnow.weatheractivityplanner.feature.locationsearch

import app.cash.turbine.test
import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.pnow.weatheractivityplanner.domain.model.Coordinates
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.repository.CurrentLocationRepository
import com.pnow.weatheractivityplanner.domain.repository.GeocodingRepository
import com.pnow.weatheractivityplanner.domain.usecase.GetCurrentLocationUseCase
import com.pnow.weatheractivityplanner.domain.usecase.SearchLocationsUseCase
import com.pnow.weatheractivityplanner.feature.common.UiError
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
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
                results = listOf(
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

    @Test
    fun `given current location resolves successfully, when onCurrentLocationAccessGranted, then state emits loading then clears it`() =
        runTest(testDispatcher) {
            val london = buildLondon()
            val viewModel = buildViewModel(
                currentLocationResult = Result.success(
                    Coordinates(latitude = london.latitude, longitude = london.longitude),
                ),
                reverseGeocodeResult = Result.success(london),
            )

            viewModel.searchState.test {
                assertEquals(LocationSearchUiState(), awaitItem())

                viewModel.startCurrentLocationRequest()
                assertTrue(awaitItem().isResolvingCurrentLocation)

                viewModel.onCurrentLocationAccessGranted()
                assertFalse(awaitItem().isResolvingCurrentLocation)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given current location resolves successfully, when onCurrentLocationAccessGranted, then resolved location is emitted`() =
        runTest(testDispatcher) {
            val london = buildLondon()
            val viewModel = buildViewModel(
                currentLocationResult = Result.success(
                    Coordinates(latitude = london.latitude, longitude = london.longitude),
                ),
                reverseGeocodeResult = Result.success(london),
            )

            viewModel.currentLocationResolved.test {
                viewModel.startCurrentLocationRequest()
                viewModel.onCurrentLocationAccessGranted()

                assertEquals(london.toUiModel(), awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given current location fails, when onCurrentLocationAccessGranted, then state clears isResolvingCurrentLocation and emits error message`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel(
                currentLocationResult = Result.failure(DomainError.LocationUnavailable()),
            )

            viewModel.searchState.test {
                assertEquals(LocationSearchUiState(), awaitItem())

                viewModel.startCurrentLocationRequest()
                assertTrue(awaitItem().isResolvingCurrentLocation)

                viewModel.onCurrentLocationAccessGranted()
                assertFalse(awaitItem().isResolvingCurrentLocation)

                cancelAndIgnoreRemainingEvents()
            }

            viewModel.locationErrorMessages.test {
                assertEquals(UiError.LocationUnavailable, awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given not already resolving, when startCurrentLocationRequest, then returns true and sets isResolvingCurrentLocation`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel()

            viewModel.searchState.test {
                assertEquals(LocationSearchUiState(), awaitItem())

                assertTrue(viewModel.startCurrentLocationRequest())

                assertTrue(awaitItem().isResolvingCurrentLocation)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given already resolving, when startCurrentLocationRequest, then returns false and ignores duplicate tap`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel()

            viewModel.searchState.test {
                awaitItem() // initial state

                assertTrue(viewModel.startCurrentLocationRequest())
                awaitItem() // isResolvingCurrentLocation becomes true

                assertFalse(viewModel.startCurrentLocationRequest())

                expectNoEvents()
            }
        }

    @Test
    fun `given a request in flight, when cancelCurrentLocationRequest, then clears isResolvingCurrentLocation and emits no result or error`() =
        runTest(testDispatcher) {
            val currentLocationRepository = FakeCurrentLocationRepository(
                result = Result.success(Coordinates(latitude = 0.0, longitude = 0.0)),
                hasPermission = true,
            )
            val geocodingRepository =
                FakeGeocodingRepository(results = listOf(Result.success(emptyList())))
            val viewModel = LocationSearchViewModel(
                searchLocationsUseCase = SearchLocationsUseCase(geocodingRepository),
                getCurrentLocationUseCase = GetCurrentLocationUseCase(
                    currentLocationRepository = NeverCompletingCurrentLocationRepository(),
                    geocodingRepository = geocodingRepository,
                ),
                currentLocationRepository = currentLocationRepository,
                defaultDispatcher = testDispatcher,
            )

            viewModel.startCurrentLocationRequest()
            viewModel.onCurrentLocationAccessGranted()
            advanceUntilIdle()

            assertTrue(viewModel.searchState.value.isResolvingCurrentLocation)

            viewModel.cancelCurrentLocationRequest()
            advanceUntilIdle()

            assertFalse(viewModel.searchState.value.isResolvingCurrentLocation)
            viewModel.currentLocationResolved.test { expectNoEvents() }
            viewModel.locationErrorMessages.test { expectNoEvents() }
        }

    @Test
    fun `given a request in flight, when onQueryChanged, then cancels it and emits no result or error`() =
        runTest(testDispatcher) {
            val currentLocationRepository = FakeCurrentLocationRepository(
                result = Result.success(Coordinates(latitude = 0.0, longitude = 0.0)),
                hasPermission = true,
            )
            val geocodingRepository =
                FakeGeocodingRepository(results = listOf(Result.success(emptyList())))
            val viewModel = LocationSearchViewModel(
                searchLocationsUseCase = SearchLocationsUseCase(geocodingRepository),
                getCurrentLocationUseCase = GetCurrentLocationUseCase(
                    currentLocationRepository = NeverCompletingCurrentLocationRepository(),
                    geocodingRepository = geocodingRepository,
                ),
                currentLocationRepository = currentLocationRepository,
                defaultDispatcher = testDispatcher,
            )

            viewModel.startCurrentLocationRequest()
            viewModel.onCurrentLocationAccessGranted()
            advanceUntilIdle()

            assertTrue(viewModel.searchState.value.isResolvingCurrentLocation)

            viewModel.onQueryChanged(LocationSearchViewModelFixture.SEARCH_QUERY)
            advanceUntilIdle()

            assertFalse(viewModel.searchState.value.isResolvingCurrentLocation)
            assertEquals(
                LocationSearchViewModelFixture.SEARCH_QUERY,
                viewModel.searchState.value.searchQuery,
            )
            viewModel.currentLocationResolved.test { expectNoEvents() }
            viewModel.locationErrorMessages.test { expectNoEvents() }
        }

    @Test
    fun `given permission granted, when hasLocationPermission, then returns true`() {
        val viewModel = buildViewModel(hasLocationPermission = true)

        assertTrue(viewModel.hasLocationPermission())
    }

    @Test
    fun `given permission not granted, when hasLocationPermission, then returns false`() {
        val viewModel = buildViewModel(hasLocationPermission = false)

        assertFalse(viewModel.hasLocationPermission())
    }

    @Test
    fun `given a request in flight, when onCurrentLocationPermissionDenied, then clears isResolvingCurrentLocation and emits error message`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel()

            viewModel.searchState.test {
                awaitItem() // initial state

                viewModel.startCurrentLocationRequest()
                awaitItem() // isResolvingCurrentLocation becomes true

                viewModel.onCurrentLocationPermissionDenied()
                assertFalse(awaitItem().isResolvingCurrentLocation)

                cancelAndIgnoreRemainingEvents()
            }

            viewModel.locationErrorMessages.test {
                assertEquals(UiError.LocationPermissionDenied, awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given a request in flight, when onCurrentLocationSettingsUnavailable, then clears isResolvingCurrentLocation and emits error message`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel()

            viewModel.searchState.test {
                awaitItem() // initial state

                viewModel.startCurrentLocationRequest()
                awaitItem() // isResolvingCurrentLocation becomes true

                viewModel.onCurrentLocationSettingsUnavailable()
                assertFalse(awaitItem().isResolvingCurrentLocation)

                cancelAndIgnoreRemainingEvents()
            }

            viewModel.locationErrorMessages.test {
                assertEquals(UiError.LocationDisabled, awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun buildViewModel(
        locationResults: List<Result<List<Location>>> = listOf(Result.success(emptyList())),
        currentLocationResult: Result<Coordinates> = Result.success(
            Coordinates(
                latitude = 0.0,
                longitude = 0.0,
            ),
        ),
        reverseGeocodeResult: Result<Location>? = null,
        hasLocationPermission: Boolean = true,
    ) = buildViewModel(
        repository = FakeGeocodingRepository(
            results = locationResults,
            reverseGeocodeResult = reverseGeocodeResult,
        ),
        currentLocationResult = currentLocationResult,
        hasLocationPermission = hasLocationPermission,
    )

    private fun buildViewModel(
        repository: GeocodingRepository,
        currentLocationResult: Result<Coordinates> = Result.success(
            Coordinates(
                latitude = 0.0,
                longitude = 0.0,
            ),
        ),
        hasLocationPermission: Boolean = true,
    ): LocationSearchViewModel {
        val currentLocationRepository = FakeCurrentLocationRepository(
            result = currentLocationResult,
            hasPermission = hasLocationPermission,
        )
        return LocationSearchViewModel(
            searchLocationsUseCase = SearchLocationsUseCase(repository),
            getCurrentLocationUseCase = GetCurrentLocationUseCase(
                currentLocationRepository = currentLocationRepository,
                geocodingRepository = repository,
            ),
            currentLocationRepository = currentLocationRepository,
            defaultDispatcher = testDispatcher,
        )
    }

    private fun buildLondon() = Location(
        id = LocationSearchViewModelFixture.London.ID,
        name = LocationSearchViewModelFixture.London.NAME,
        latitude = LocationSearchViewModelFixture.London.LATITUDE,
        longitude = LocationSearchViewModelFixture.London.LONGITUDE,
        country = LocationSearchViewModelFixture.London.COUNTRY,
        region = LocationSearchViewModelFixture.London.REGION,
    )

    private class FakeGeocodingRepository(
        private val results: List<Result<List<Location>>>,
        private val reverseGeocodeResult: Result<Location>? = null,
    ) : GeocodingRepository {

        private var callIndex = 0
        val searchedQueries = mutableListOf<String>()

        override suspend fun searchLocations(
            query: String,
            count: Int,
        ): Result<List<Location>> {
            searchedQueries.add(query)
            delay(LocationSearchViewModelFixture.LOADING_DELAY_MS.milliseconds)
            val result = results[callIndex]
            callIndex = minOf(callIndex + 1, results.size - 1)
            return result
        }

        override suspend fun reverseGeocode(
            latitude: Double,
            longitude: Double,
        ): Result<Location> =
            reverseGeocodeResult ?: error("Not used in this test")
    }

    private class FakeCurrentLocationRepository(
        private val result: Result<Coordinates>,
        private val hasPermission: Boolean = true,
    ) : CurrentLocationRepository {

        override suspend fun getCurrentLocation(): Result<Coordinates> = result

        override fun hasPermission(): Boolean = hasPermission
    }

    private class NeverCompletingCurrentLocationRepository : CurrentLocationRepository {

        override suspend fun getCurrentLocation(): Result<Coordinates> = awaitCancellation()

        override fun hasPermission(): Boolean = true
    }
}
