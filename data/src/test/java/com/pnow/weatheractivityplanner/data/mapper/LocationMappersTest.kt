package com.pnow.weatheractivityplanner.data.mapper

import com.pnow.weatheractivityplanner.data.remote.dto.geocoding.GeocodingAddressDto
import com.pnow.weatheractivityplanner.data.remote.dto.geocoding.GeocodingResultDto
import com.pnow.weatheractivityplanner.domain.model.Location
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

private object LocationFixture {

    const val PLACEHOLDER_PLACE_ID = "1"
    const val PLACEHOLDER_LATITUDE = "0.0"
    const val PLACEHOLDER_LONGITUDE = "0.0"

    object London {

        const val PLACE_ID = "2643743"
        const val NAME = "London"
        const val LATITUDE = "51.50853"
        const val LONGITUDE = "-0.12574"
        const val DISPLAY_NAME = "London, Greater London, England, United Kingdom"
        const val COUNTRY = "United Kingdom"
        const val REGION = "England"
    }

    object MissingRegion {

        const val DISPLAY_NAME = "City, Country"
        const val NAME = "City"
        const val COUNTRY = "Country"
    }

    object MissingCountry {

        const val DISPLAY_NAME = "Atlantis"
    }

    object MissingAddress {

        const val DISPLAY_NAME = "Unnamed Place, Nowhere"
        const val FALLBACK_NAME = "Unnamed Place"
    }
}

class LocationMappersTest {

    @Test
    fun `given full dto, when toDomain, then all fields are mapped correctly`() {
        val dto = GeocodingResultDto(
            placeId = LocationFixture.London.PLACE_ID,
            latitude = LocationFixture.London.LATITUDE,
            longitude = LocationFixture.London.LONGITUDE,
            displayName = LocationFixture.London.DISPLAY_NAME,
            address = GeocodingAddressDto(
                name = LocationFixture.London.NAME,
                state = LocationFixture.London.REGION,
                country = LocationFixture.London.COUNTRY,
            ),
        )

        val location = dto.toDomain()

        val expectedLocation = Location(
            id = LocationFixture.London.PLACE_ID.toLong(),
            name = LocationFixture.London.NAME,
            latitude = LocationFixture.London.LATITUDE.toDouble(),
            longitude = LocationFixture.London.LONGITUDE.toDouble(),
            country = LocationFixture.London.COUNTRY,
            region = LocationFixture.London.REGION,
        )

        assertEquals(expectedLocation, location)
    }

    @Test
    fun `given dto with null state, when toDomain, then region is null`() {
        val dto = GeocodingResultDto(
            placeId = LocationFixture.PLACEHOLDER_PLACE_ID,
            latitude = LocationFixture.PLACEHOLDER_LATITUDE,
            longitude = LocationFixture.PLACEHOLDER_LONGITUDE,
            displayName = LocationFixture.MissingRegion.DISPLAY_NAME,
            address = GeocodingAddressDto(
                name = LocationFixture.MissingRegion.NAME,
                state = null,
                country = LocationFixture.MissingRegion.COUNTRY,
            ),
        )

        val location = dto.toDomain()

        assertNull(location.region)
    }

    @Test
    fun `given dto with missing country, when toDomain, then country is null`() {
        val dto = GeocodingResultDto(
            placeId = LocationFixture.PLACEHOLDER_PLACE_ID,
            latitude = LocationFixture.PLACEHOLDER_LATITUDE,
            longitude = LocationFixture.PLACEHOLDER_LONGITUDE,
            displayName = LocationFixture.MissingCountry.DISPLAY_NAME,
            address = null,
        )

        val location = dto.toDomain()

        assertNull(location.country)
    }

    @Test
    fun `given dto with no address, when toDomain, then name falls back to first display name segment`() {
        val dto = GeocodingResultDto(
            placeId = LocationFixture.PLACEHOLDER_PLACE_ID,
            latitude = LocationFixture.PLACEHOLDER_LATITUDE,
            longitude = LocationFixture.PLACEHOLDER_LONGITUDE,
            displayName = LocationFixture.MissingAddress.DISPLAY_NAME,
            address = null,
        )

        val location = dto.toDomain()

        assertEquals(LocationFixture.MissingAddress.FALLBACK_NAME, location.name)
    }
}
