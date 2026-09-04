package com.pnow.weatheractivityplanner.data.remote.dto.geocoding

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GeocodingAddressDto(
    @param:Json(name = "name") val name: String? = null,
    @param:Json(name = "state") val state: String? = null,
    @param:Json(name = "country") val country: String? = null,
)
