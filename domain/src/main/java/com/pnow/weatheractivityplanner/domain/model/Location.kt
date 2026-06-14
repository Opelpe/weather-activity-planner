package com.pnow.weatheractivityplanner.domain.model

data class Location(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val countryCode: String?,
    val region: String?,
)
