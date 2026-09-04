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

    companion object {

        private val PLACE_TAGS = listOf(
            "place:city",
            "place:town",
            "place:village",
            "place:state",
            "place:country",
            "natural:peak",
        ).joinToString(separator = ",")

        private const val FORMAT_JSON = "json"
    }
}
