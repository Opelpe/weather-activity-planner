package com.pnow.weatheractivityplanner.data.remote.dto.geocoding

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GeocodingResultDto(
    @param:Json(name = "place_id") val placeId: String,
    @param:Json(name = "lat") val latitude: String,
    @param:Json(name = "lon") val longitude: String,
    @param:Json(name = "display_name") val displayName: String,
    @param:Json(name = "address") val address: GeocodingAddressDto? = null,
)
