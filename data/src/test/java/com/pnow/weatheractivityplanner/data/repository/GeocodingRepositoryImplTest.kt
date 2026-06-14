package com.pnow.weatheractivityplanner.data.repository

import com.pnow.weatheractivityplanner.data.remote.api.GeocodingApi
import com.pnow.weatheractivityplanner.data.remote.dto.geocoding.GeocodingResponseDto
import com.pnow.weatheractivityplanner.data.remote.dto.geocoding.GeocodingResultDto
import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.pnow.weatheractivityplanner.domain.model.Location
import io.mockk.coEvery
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

private object GeocodingRepositoryFixture {

    const val SEARCH_QUERY = "London"
    const val UNKNOWN_QUERY = "xyz123"
    const val NETWORK_ERROR_MESSAGE = "No network"
    const val HTTP_ERROR_CODE = 500
    const val HTTP_ERROR_BODY = "Internal Server Error"

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

class GeocodingRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val geocodingApi = mockk<GeocodingApi>()
    private val repository = GeocodingRepositoryImpl(
        geocodingApi = geocodingApi,
        ioDispatcher = testDispatcher,
    )

    @Test
    fun `given results in response, when searchLocations, then returns mapped locations`() =
        runTest(testDispatcher) {
            coEvery { geocodingApi.searchLocations(any(), any(), any(), any()) } returns
                GeocodingResponseDto(
                    results = listOf(
                        GeocodingResultDto(
                            id = GeocodingRepositoryFixture.London.ID,
                            name = GeocodingRepositoryFixture.London.NAME,
                            latitude = GeocodingRepositoryFixture.London.LATITUDE,
                            longitude = GeocodingRepositoryFixture.London.LONGITUDE,
                            country = GeocodingRepositoryFixture.London.COUNTRY,
                            countryCode = GeocodingRepositoryFixture.London.COUNTRY_CODE,
                            admin1 = GeocodingRepositoryFixture.London.REGION,
                        ),
                    ),
                )

            val result = repository.searchLocations(query = GeocodingRepositoryFixture.SEARCH_QUERY)

            val expectedLocations = listOf(
                Location(
                    id = GeocodingRepositoryFixture.London.ID,
                    name = GeocodingRepositoryFixture.London.NAME,
                    latitude = GeocodingRepositoryFixture.London.LATITUDE,
                    longitude = GeocodingRepositoryFixture.London.LONGITUDE,
                    country = GeocodingRepositoryFixture.London.COUNTRY,
                    countryCode = GeocodingRepositoryFixture.London.COUNTRY_CODE,
                    region = GeocodingRepositoryFixture.London.REGION,
                ),
            )

            assertTrue(result.isSuccess)
            assertEquals(expectedLocations, result.getOrNull())
        }

    @Test
    fun `given null results in response, when searchLocations, then returns empty list`() =
        runTest(testDispatcher) {
            coEvery { geocodingApi.searchLocations(any(), any(), any(), any()) } returns
                GeocodingResponseDto(results = null)

            val result =
                repository.searchLocations(query = GeocodingRepositoryFixture.UNKNOWN_QUERY)

            assertTrue(result.isSuccess)
            assertEquals(emptyList<Location>(), result.getOrNull())
        }

    @Test
    fun `given IOException, when searchLocations, then returns NetworkUnavailable`() =
        runTest(testDispatcher) {
            coEvery { geocodingApi.searchLocations(any(), any(), any(), any()) } throws
                IOException(GeocodingRepositoryFixture.NETWORK_ERROR_MESSAGE)

            val result = repository.searchLocations(query = GeocodingRepositoryFixture.SEARCH_QUERY)

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is DomainError.NetworkUnavailable)
        }

    @Test
    fun `given HttpException 500, when searchLocations, then returns HttpError`() =
        runTest(testDispatcher) {
            val httpException = HttpException(
                Response.error<GeocodingResponseDto>(
                    GeocodingRepositoryFixture.HTTP_ERROR_CODE,
                    GeocodingRepositoryFixture.HTTP_ERROR_BODY.toResponseBody(),
                ),
            )
            coEvery { geocodingApi.searchLocations(any(), any(), any(), any()) } throws
                httpException

            val result = repository.searchLocations(query = GeocodingRepositoryFixture.SEARCH_QUERY)

            assertTrue(result.isFailure)
            val error = result.exceptionOrNull() as DomainError.HttpError
            assertEquals(GeocodingRepositoryFixture.HTTP_ERROR_CODE, error.code)
        }
}
