package com.pnow.weatheractivityplanner.data.mapper

import com.pnow.weatheractivityplanner.data.remote.dto.geocoding.GeocodingResultDto
import com.pnow.weatheractivityplanner.domain.model.Location

internal fun GeocodingResultDto.toDomain(): Location = Location(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    country = country,
    countryCode = countryCode,
    region = admin1,
)
