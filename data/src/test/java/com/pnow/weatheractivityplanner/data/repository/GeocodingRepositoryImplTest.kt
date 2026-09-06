package com.pnow.weatheractivityplanner.data.repository

import com.pnow.weatheractivityplanner.data.remote.api.GeocodingApi
import com.pnow.weatheractivityplanner.data.remote.dto.geocoding.GeocodingAddressDto
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
    const val HTTP_NOT_FOUND_CODE = 404
    const val HTTP_NOT_FOUND_BODY = """{"error":"Unable to geocode"}"""

    object London {

        const val PLACE_ID = "1"
        const val NAME = "London"
        const val LATITUDE = "51.5"
        const val LONGITUDE = "-0.1"
        const val DISPLAY_NAME = "London, England, United Kingdom"
        const val COUNTRY = "United Kingdom"
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
            stubSearchLocations(
                returns = listOf(
                    GeocodingResultDto(
                        placeId = GeocodingRepositoryFixture.London.PLACE_ID,
                        latitude = GeocodingRepositoryFixture.London.LATITUDE,
                        longitude = GeocodingRepositoryFixture.London.LONGITUDE,
                        displayName = GeocodingRepositoryFixture.London.DISPLAY_NAME,
                        address = GeocodingAddressDto(
                            name = GeocodingRepositoryFixture.London.NAME,
                            state = GeocodingRepositoryFixture.London.REGION,
                            country = GeocodingRepositoryFixture.London.COUNTRY,
                        ),
                    ),
                ),
            )

            val result = repository.searchLocations(query = GeocodingRepositoryFixture.SEARCH_QUERY)

            val expectedLocations = listOf(
                Location(
                    id = GeocodingRepositoryFixture.London.PLACE_ID.toLong(),
                    name = GeocodingRepositoryFixture.London.NAME,
                    latitude = GeocodingRepositoryFixture.London.LATITUDE.toDouble(),
                    longitude = GeocodingRepositoryFixture.London.LONGITUDE.toDouble(),
                    country = GeocodingRepositoryFixture.London.COUNTRY,
                    region = GeocodingRepositoryFixture.London.REGION,
                ),
            )

            assertTrue(result.isSuccess)
            assertEquals(expectedLocations, result.getOrNull())
        }

    @Test
    fun `given empty response, when searchLocations, then returns empty list`() =
        runTest(testDispatcher) {
            stubSearchLocations(returns = emptyList())

            val result =
                repository.searchLocations(query = GeocodingRepositoryFixture.UNKNOWN_QUERY)

            assertTrue(result.isSuccess)
            assertEquals(emptyList<Location>(), result.getOrNull())
        }

    @Test
    fun `given IOException, when searchLocations, then returns NetworkUnavailable`() =
        runTest(testDispatcher) {
            stubSearchLocations(throws = IOException(GeocodingRepositoryFixture.NETWORK_ERROR_MESSAGE))

            val result = repository.searchLocations(query = GeocodingRepositoryFixture.SEARCH_QUERY)

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is DomainError.NetworkUnavailable)
        }

    @Test
    fun `given HttpException 404, when searchLocations, then returns empty list`() =
        runTest(testDispatcher) {
            val httpException = HttpException(
                Response.error<List<GeocodingResultDto>>(
                    GeocodingRepositoryFixture.HTTP_NOT_FOUND_CODE,
                    GeocodingRepositoryFixture.HTTP_NOT_FOUND_BODY.toResponseBody(),
                ),
            )
            stubSearchLocations(throws = httpException)

            val result =
                repository.searchLocations(query = GeocodingRepositoryFixture.UNKNOWN_QUERY)

            assertTrue(result.isSuccess)
            assertEquals(emptyList<Location>(), result.getOrNull())
        }

    @Test
    fun `given HttpException 500, when searchLocations, then returns HttpError`() =
        runTest(testDispatcher) {
            val httpException = HttpException(
                Response.error<List<GeocodingResultDto>>(
                    GeocodingRepositoryFixture.HTTP_ERROR_CODE,
                    GeocodingRepositoryFixture.HTTP_ERROR_BODY.toResponseBody(),
                ),
            )
            stubSearchLocations(throws = httpException)

            val result = repository.searchLocations(query = GeocodingRepositoryFixture.SEARCH_QUERY)

            assertTrue(result.isFailure)
            val error = result.exceptionOrNull() as DomainError.HttpError
            assertEquals(GeocodingRepositoryFixture.HTTP_ERROR_CODE, error.code)
        }

    @Test
    fun `given result in response, when reverseGeocode, then returns mapped location`() =
        runTest(testDispatcher) {
            stubReverseGeocode(
                returns = GeocodingResultDto(
                    placeId = GeocodingRepositoryFixture.London.PLACE_ID,
                    latitude = GeocodingRepositoryFixture.London.LATITUDE,
                    longitude = GeocodingRepositoryFixture.London.LONGITUDE,
                    displayName = GeocodingRepositoryFixture.London.DISPLAY_NAME,
                    address = GeocodingAddressDto(
                        name = GeocodingRepositoryFixture.London.NAME,
                        state = GeocodingRepositoryFixture.London.REGION,
                        country = GeocodingRepositoryFixture.London.COUNTRY,
                    ),
                ),
            )

            val result = repository.reverseGeocode(
                latitude = GeocodingRepositoryFixture.London.LATITUDE.toDouble(),
                longitude = GeocodingRepositoryFixture.London.LONGITUDE.toDouble(),
            )

            val expectedLocation = Location(
                id = GeocodingRepositoryFixture.London.PLACE_ID.toLong(),
                name = GeocodingRepositoryFixture.London.NAME,
                latitude = GeocodingRepositoryFixture.London.LATITUDE.toDouble(),
                longitude = GeocodingRepositoryFixture.London.LONGITUDE.toDouble(),
                country = GeocodingRepositoryFixture.London.COUNTRY,
                region = GeocodingRepositoryFixture.London.REGION,
            )

            assertTrue(result.isSuccess)
            assertEquals(expectedLocation, result.getOrNull())
        }

    @Test
    fun `given IOException, when reverseGeocode, then returns NetworkUnavailable`() =
        runTest(testDispatcher) {
            stubReverseGeocode(throws = IOException(GeocodingRepositoryFixture.NETWORK_ERROR_MESSAGE))

            val result = repository.reverseGeocode(
                latitude = GeocodingRepositoryFixture.London.LATITUDE.toDouble(),
                longitude = GeocodingRepositoryFixture.London.LONGITUDE.toDouble(),
            )

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is DomainError.NetworkUnavailable)
        }

    @Test
    fun `given HttpException 500, when reverseGeocode, then returns HttpError`() =
        runTest(testDispatcher) {
            val httpException = HttpException(
                Response.error<GeocodingResultDto>(
                    GeocodingRepositoryFixture.HTTP_ERROR_CODE,
                    GeocodingRepositoryFixture.HTTP_ERROR_BODY.toResponseBody(),
                ),
            )
            stubReverseGeocode(throws = httpException)

            val result = repository.reverseGeocode(
                latitude = GeocodingRepositoryFixture.London.LATITUDE.toDouble(),
                longitude = GeocodingRepositoryFixture.London.LONGITUDE.toDouble(),
            )

            assertTrue(result.isFailure)
            val error = result.exceptionOrNull() as DomainError.HttpError
            assertEquals(GeocodingRepositoryFixture.HTTP_ERROR_CODE, error.code)
        }

    private fun stubSearchLocations(returns: List<GeocodingResultDto>) {
        coEvery {
            geocodingApi.searchLocations(
                query = any(),
                limit = any(),
                acceptLanguage = any(),
                tag = any(),
                format = any(),
            )
        } returns returns
    }

    private fun stubSearchLocations(throws: Throwable) {
        coEvery {
            geocodingApi.searchLocations(
                query = any(),
                limit = any(),
                acceptLanguage = any(),
                tag = any(),
                format = any(),
            )
        } throws throws
    }

    private fun stubReverseGeocode(returns: GeocodingResultDto) {
        coEvery {
            geocodingApi.reverseGeocode(latitude = any(), longitude = any(), acceptLanguage = any(), format = any())
        } returns returns
    }

    private fun stubReverseGeocode(throws: Throwable) {
        coEvery {
            geocodingApi.reverseGeocode(latitude = any(), longitude = any(), acceptLanguage = any(), format = any())
        } throws throws
    }
}
