package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.repository.GeocodingRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private object SearchLocationsFixture {
    const val SEARCH_QUERY = "London"
    const val UNKNOWN_QUERY = "xyz123"
    const val CUSTOM_QUERY = "test"
    const val CUSTOM_COUNT = 5

    object London {
        const val ID = 1L
        const val NAME = "London"
        const val LATITUDE = 51.5
        const val LONGITUDE = -0.1
        const val COUNTRY = "United Kingdom"
        const val COUNTRY_CODE = "GB"
        const val REGION = "England"
    }
}

class SearchLocationsUseCaseTest {

    @Test
    fun `given successful response, when invoked, then returns location list`() = runTest {
        val locations = listOf(
            Location(
                id = SearchLocationsFixture.London.ID,
                name = SearchLocationsFixture.London.NAME,
                latitude = SearchLocationsFixture.London.LATITUDE,
                longitude = SearchLocationsFixture.London.LONGITUDE,
                country = SearchLocationsFixture.London.COUNTRY,
                countryCode = SearchLocationsFixture.London.COUNTRY_CODE,
                region = SearchLocationsFixture.London.REGION,
            ),
        )
        val useCase = SearchLocationsUseCase(FakeGeocodingRepository(Result.success(locations)))

        val result = useCase(query = SearchLocationsFixture.SEARCH_QUERY)

        assertEquals(locations, result.getOrNull())
    }

    @Test
    fun `given network error, when invoked, then returns failure`() = runTest {
        val error = DomainError.NetworkUnavailable()
        val useCase = SearchLocationsUseCase(FakeGeocodingRepository(Result.failure(error)))

        val result = useCase(query = SearchLocationsFixture.SEARCH_QUERY)

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `given empty results, when invoked, then returns empty list`() = runTest {
        val useCase = SearchLocationsUseCase(FakeGeocodingRepository(Result.success(emptyList())))

        val result = useCase(query = SearchLocationsFixture.UNKNOWN_QUERY)

        assertEquals(emptyList<Location>(), result.getOrNull())
    }

    @Test
    fun `given count parameter, when invoked, then passes count to repository`() = runTest {
        val fake = FakeGeocodingRepository(Result.success(emptyList()))
        val useCase = SearchLocationsUseCase(fake)

        useCase(query = SearchLocationsFixture.CUSTOM_QUERY, count = SearchLocationsFixture.CUSTOM_COUNT)

        assertEquals(SearchLocationsFixture.CUSTOM_COUNT, fake.lastCount)
    }

    private class FakeGeocodingRepository(
        private val result: Result<List<Location>>,
    ) : GeocodingRepository {
        var lastCount: Int = -1

        override suspend fun searchLocations(query: String, count: Int): Result<List<Location>> {
            lastCount = count
            return result
        }
    }
}