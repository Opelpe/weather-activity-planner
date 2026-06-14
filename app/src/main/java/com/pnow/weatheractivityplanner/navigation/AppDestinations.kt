package com.pnow.weatheractivityplanner.navigation

import kotlinx.serialization.Serializable

@Serializable
data object LocationSearchRoute

@Serializable
data class WeatherRecommendationRoute(
    val locationId: Long,
    val locationName: String,
    val locationCountry: String,
    val latitude: Double,
    val longitude: Double,
)

@Serializable
data class WeatherForecastRoute(
    val locationName: String,
    val locationCountry: String,
    val latitude: Double,
    val longitude: Double,
)

object RouteArgKeys {

    const val LOCATION_ID = "locationId"
    const val LOCATION_NAME = "locationName"
    const val LOCATION_COUNTRY = "locationCountry"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
}
