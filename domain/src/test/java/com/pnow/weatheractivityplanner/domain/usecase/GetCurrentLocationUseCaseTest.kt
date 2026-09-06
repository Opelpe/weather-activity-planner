package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.pnow.weatheractivityplanner.domain.model.Coordinates
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.repository.CurrentLocationRepository
import com.pnow.weatheractivityplanner.domain.repository.GeocodingRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private object GetCurrentLocationFixture {

    object London {
        const val ID = 1L
        const val NAME = "London"
        const val LATITUDE = 51.5
        const val LONGITUDE = -0.1
        const val COUNTRY = "United Kingdom"
        const val REGION = "England"
    }
}

class GetCurrentLocationUseCaseTest {

    @Test
    fun `given coordinates and reverse geocode succeed, when invoked, then returns location`() =
        runTest {
            val coordinates = Coordinates(
                latitude = GetCurrentLocationFixture.London.LATITUDE,
                longitude = GetCurrentLocationFixture.London.LONGITUDE,
            )
            val location = Location(
                id = GetCurrentLocationFixture.London.ID,
                name = GetCurrentLocationFixture.London.NAME,
                latitude = GetCurrentLocationFixture.London.LATITUDE,
                longitude = GetCurrentLocationFixture.London.LONGITUDE,
                country = GetCurrentLocationFixture.London.COUNTRY,
                region = GetCurrentLocationFixture.London.REGION,
            )
            val useCase = GetCurrentLocationUseCase(
                currentLocationRepository = FakeCurrentLocationRepository(Result.success(coordinates)),
                geocodingRepository = FakeGeocodingRepository(reverseGeocodeResult = Result.success(location)),
            )

            val result = useCase()

            assertEquals(location, result.getOrNull())
        }

    @Test
    fun `given getting coordinates fails, when invoked, then returns failure without reverse geocoding`() =
        runTest {
            val error = DomainError.LocationPermissionDenied()
            val geocodingRepository = FakeGeocodingRepository(reverseGeocodeResult = Result.success(mockLocation()))
            val useCase = GetCurrentLocationUseCase(
                currentLocationRepository = FakeCurrentLocationRepository(Result.failure(error)),
                geocodingRepository = geocodingRepository,
            )

            val result = useCase()

            assertTrue(result.isFailure)
            assertEquals(error, result.exceptionOrNull())
            assertFalse(geocodingRepository.reverseGeocodeCalled)
        }

    @Test
    fun `given reverse geocoding fails, when invoked, then returns failure`() = runTest {
        val coordinates = Coordinates(
            latitude = GetCurrentLocationFixture.London.LATITUDE,
            longitude = GetCurrentLocationFixture.London.LONGITUDE,
        )
        val error = DomainError.NetworkUnavailable()
        val useCase = GetCurrentLocationUseCase(
            currentLocationRepository = FakeCurrentLocationRepository(Result.success(coordinates)),
            geocodingRepository = FakeGeocodingRepository(reverseGeocodeResult = Result.failure(error)),
        )

        val result = useCase()

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    private fun mockLocation() = Location(
        id = GetCurrentLocationFixture.London.ID,
        name = GetCurrentLocationFixture.London.NAME,
        latitude = GetCurrentLocationFixture.London.LATITUDE,
        longitude = GetCurrentLocationFixture.London.LONGITUDE,
        country = GetCurrentLocationFixture.London.COUNTRY,
        region = GetCurrentLocationFixture.London.REGION,
    )

    private class FakeCurrentLocationRepository(
        private val result: Result<Coordinates>,
    ) : CurrentLocationRepository {
        override suspend fun getCurrentLocation(): Result<Coordinates> = result

        override fun hasPermission(): Boolean = true
    }

    private class FakeGeocodingRepository(
        private val reverseGeocodeResult: Result<Location>,
    ) : GeocodingRepository {
        var reverseGeocodeCalled = false
            private set

        override suspend fun searchLocations(query: String, count: Int): Result<List<Location>> =
            Result.success(emptyList())

        override suspend fun reverseGeocode(latitude: Double, longitude: Double): Result<Location> {
            reverseGeocodeCalled = true
            return reverseGeocodeResult
        }
    }
}
