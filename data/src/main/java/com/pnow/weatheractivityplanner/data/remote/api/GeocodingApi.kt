package com.pnow.weatheractivityplanner.data.remote.api

import com.pnow.weatheractivityplanner.data.remote.dto.geocoding.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

internal interface GeocodingApi {

    @GET("v1/search")
    suspend fun searchLocations(
        @Query("name") name: String,
        @Query("count") count: Int,
        @Query("language") language: String = LANGUAGE_EN,
        @Query("format") format: String = FORMAT_JSON,
    ): GeocodingResponseDto

    companion object {

        private const val LANGUAGE_EN = "en"
        private const val FORMAT_JSON = "json"
    }
}
