package com.pnow.weatheractivityplanner.navigation

import androidx.lifecycle.SavedStateHandle
import com.pnow.weatheractivityplanner.domain.model.Location
import kotlinx.serialization.Serializable

interface LocationArgs {

    val locationId: Long
    val locationName: String
    val locationCountry: String
    val latitude: Double
    val longitude: Double
}

@Serializable
data object LocationSearchRoute

@Serializable
data class WeatherRecommendationRoute(
    override val locationId: Long,
    override val locationName: String,
    override val locationCountry: String,
    override val latitude: Double,
    override val longitude: Double,
) : LocationArgs

@Serializable
data class WeatherForecastRoute(
    override val locationId: Long,
    override val locationName: String,
    override val locationCountry: String,
    override val latitude: Double,
    override val longitude: Double,
) : LocationArgs

fun SavedStateHandle.toLocationOrNull(): Location? {
    val locationId: Long = get(LocationArgs::locationId.name) ?: return null
    val locationName: String = get(LocationArgs::locationName.name) ?: return null
    val locationCountry: String = get(LocationArgs::locationCountry.name) ?: return null
    val latitude: Double = get(LocationArgs::latitude.name) ?: return null
    val longitude: Double = get(LocationArgs::longitude.name) ?: return null
    return Location(
        id = locationId,
        name = locationName,
        latitude = latitude,
        longitude = longitude,
        country = locationCountry,
        countryCode = null,
        region = null,
    )
}
