package com.pnow.weatheractivityplanner.data.remote.dto.geocoding

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GeocodingResponseDto(
    @param:Json(name = "results") val results: List<GeocodingResultDto>? = null,
)
