package com.pnow.weatheractivityplanner.data.remote.api

import com.pnow.weatheractivityplanner.data.remote.dto.geocoding.GeocodingResultDto
import retrofit2.http.GET
import retrofit2.http.Query

internal interface GeocodingApi {

    @GET("v1/autocomplete")
    suspend fun searchLocations(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("accept-language") acceptLanguage: String,
        @Query("tag") tag: String = PLACE_TAGS,
        @Query("format") format: String = FORMAT_JSON,
    ): List<GeocodingResultDto>

    @GET("v1/reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("accept-language") acceptLanguage: String,
        @Query("format") format: String = FORMAT_JSON,
    ): GeocodingResultDto

    companion object {

        private const val NAMESPACE_PLACE = "place"
        private const val NAMESPACE_NATURAL = "natural"
        private const val FORMAT_JSON = "json"

        private const val VALUE_CITY = "city"
        private const val VALUE_TOWN = "town"
        private const val VALUE_VILLAGE = "village"
        private const val VALUE_STATE = "state"
        private const val VALUE_COUNTRY = "country"
        private const val VALUE_PEAK = "peak"

        private val PLACE_TAGS = listOf(
            "$NAMESPACE_PLACE:$VALUE_CITY",
            "$NAMESPACE_PLACE:$VALUE_TOWN",
            "$NAMESPACE_PLACE:$VALUE_VILLAGE",
            "$NAMESPACE_PLACE:$VALUE_STATE",
            "$NAMESPACE_PLACE:$VALUE_COUNTRY",
            "$NAMESPACE_NATURAL:$VALUE_PEAK",
        ).joinToString(separator = ",")
    }
}
