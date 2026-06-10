package com.pnow.weatheractivityplanner.data.remote.dto.geocoding

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingResultDto(
    @param:Json(name = "id") val id: Long,
    @param:Json(name = "name") val name: String,
    @param:Json(name = "latitude") val latitude: Double,
    @param:Json(name = "longitude") val longitude: Double,
    @param:Json(name = "country") val country: String? = null,
    @param:Json(name = "country_code") val countryCode: String? = null,
    @param:Json(name = "admin1") val admin1: String? = null,
)
