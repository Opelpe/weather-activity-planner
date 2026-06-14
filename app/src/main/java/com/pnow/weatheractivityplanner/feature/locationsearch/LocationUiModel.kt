package com.pnow.weatheractivityplanner.feature.locationsearch

import com.pnow.weatheractivityplanner.domain.model.Location

data class LocationUiModel(
    val id: Long,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
)

internal fun Location.toUiModel(): LocationUiModel = LocationUiModel(
    id = id,
    name = name,
    country = country.orEmpty(),
    latitude = latitude,
    longitude = longitude,
)
