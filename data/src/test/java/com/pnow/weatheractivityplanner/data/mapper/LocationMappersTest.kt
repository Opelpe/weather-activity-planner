package com.pnow.weatheractivityplanner.data.mapper

import com.pnow.weatheractivityplanner.data.remote.dto.geocoding.GeocodingResultDto
import com.pnow.weatheractivityplanner.domain.model.Location
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

private object LocationFixture {

    const val PLACEHOLDER_ID = 1L
    const val PLACEHOLDER_LATITUDE = 0.0
    const val PLACEHOLDER_LONGITUDE = 0.0

    object London {

        const val ID = 2643743L
        const val NAME = "London"
        const val LATITUDE = 51.50853
        const val LONGITUDE = -0.12574
        const val COUNTRY = "United Kingdom"
        const val COUNTRY_CODE = "GB"
        const val REGION = "England"
    }

    object MissingRegion {

        const val NAME = "City"
        const val COUNTRY = "Country"
        const val COUNTRY_CODE = "CC"
    }

    object MissingCountry {

        const val NAME = "Atlantis"
    }
}

class LocationMappersTest {

    @Test
    fun `given full dto, when toDomain, then all fields are mapped correctly`() {
        val dto = GeocodingResultDto(
            id = LocationFixture.London.ID,
            name = LocationFixture.London.NAME,
            latitude = LocationFixture.London.LATITUDE,
            longitude = LocationFixture.London.LONGITUDE,
            country = LocationFixture.London.COUNTRY,
            countryCode = LocationFixture.London.COUNTRY_CODE,
            admin1 = LocationFixture.London.REGION,
        )

        val location = dto.toDomain()

        val expectedLocation = Location(
            id = LocationFixture.London.ID,
            name = LocationFixture.London.NAME,
            latitude = LocationFixture.London.LATITUDE,
            longitude = LocationFixture.London.LONGITUDE,
            country = LocationFixture.London.COUNTRY,
            countryCode = LocationFixture.London.COUNTRY_CODE,
            region = LocationFixture.London.REGION,
        )

        assertEquals(expectedLocation, location)
    }

    @Test
    fun `given dto with null admin1, when toDomain, then region is null`() {
        val dto = GeocodingResultDto(
            id = LocationFixture.PLACEHOLDER_ID,
            name = LocationFixture.MissingRegion.NAME,
            latitude = LocationFixture.PLACEHOLDER_LATITUDE,
            longitude = LocationFixture.PLACEHOLDER_LONGITUDE,
            country = LocationFixture.MissingRegion.COUNTRY,
            countryCode = LocationFixture.MissingRegion.COUNTRY_CODE,
            admin1 = null,
        )

        val location = dto.toDomain()

        assertNull(location.region)
    }

    @Test
    fun `given dto with missing country, when toDomain, then country and countryCode are null`() {
        val dto = GeocodingResultDto(
            id = LocationFixture.PLACEHOLDER_ID,
            name = LocationFixture.MissingCountry.NAME,
            latitude = LocationFixture.PLACEHOLDER_LATITUDE,
            longitude = LocationFixture.PLACEHOLDER_LONGITUDE,
            country = null,
            countryCode = null,
            admin1 = null,
        )

        val location = dto.toDomain()

        assertNull(location.country)
        assertNull(location.countryCode)
    }
}
