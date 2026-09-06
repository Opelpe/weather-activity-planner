package com.pnow.weatheractivityplanner.data.location

internal interface LocationAvailabilityChecker {

    fun hasPermission(): Boolean

    fun isLocationEnabled(): Boolean
}
