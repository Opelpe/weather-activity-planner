package com.pnow.weatheractivityplanner.data.location

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import com.pnow.weatheractivityplanner.data.di.ApplicationPackageName
import javax.inject.Inject

internal class LocationAvailabilityCheckerImpl @Inject constructor(
    private val packageManager: PackageManager,
    @param:ApplicationPackageName private val packageName: String,
    private val locationManager: LocationManager,
) : LocationAvailabilityChecker {

    override fun hasPermission(): Boolean =
        packageManager.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, packageName) ==
            PackageManager.PERMISSION_GRANTED ||
            packageManager.checkPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                packageName,
            ) ==
            PackageManager.PERMISSION_GRANTED

    override fun isLocationEnabled(): Boolean = locationManager.isLocationEnabled
}
