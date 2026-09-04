package com.pnow.weatheractivityplanner.data.mapper

import com.pnow.weatheractivityplanner.data.remote.dto.geocoding.GeocodingResultDto
import com.pnow.weatheractivityplanner.domain.model.Location

internal fun GeocodingResultDto.toDomain(): Location = Location(
    id = placeId.toLong(),
    name = address?.name ?: displayName.substringBefore(","),
    latitude = latitude.toDouble(),
    longitude = longitude.toDouble(),
    country = address?.country,
    region = address?.state,
)
