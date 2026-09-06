package com.pnow.weatheractivityplanner.data.location

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private object LocationAvailabilityCheckerImplFixture {

    const val PACKAGE_NAME = "com.pnow.weatheractivityplanner"
}

class LocationAvailabilityCheckerImplTest {

    private val packageManager = mockk<PackageManager>()
    private val locationManager = mockk<LocationManager>()
    private val checker = LocationAvailabilityCheckerImpl(
        packageManager = packageManager,
        packageName = LocationAvailabilityCheckerImplFixture.PACKAGE_NAME,
        locationManager = locationManager,
    )

    @Test
    fun `given fine location permission granted, when hasPermission, then returns true`() {
        stubPermission(fineGranted = true, coarseGranted = false)

        assertTrue(checker.hasPermission())
    }

    @Test
    fun `given only coarse location permission granted, when hasPermission, then returns true`() {
        stubPermission(fineGranted = false, coarseGranted = true)

        assertTrue(checker.hasPermission())
    }

    @Test
    fun `given no location permission granted, when hasPermission, then returns false`() {
        stubPermission(fineGranted = false, coarseGranted = false)

        assertFalse(checker.hasPermission())
    }

    @Test
    fun `given location services enabled, when isLocationEnabled, then returns true`() {
        every { locationManager.isLocationEnabled } returns true

        assertTrue(checker.isLocationEnabled())
    }

    @Test
    fun `given location services disabled, when isLocationEnabled, then returns false`() {
        every { locationManager.isLocationEnabled } returns false

        assertFalse(checker.isLocationEnabled())
    }

    private fun stubPermission(
        fineGranted: Boolean,
        coarseGranted: Boolean,
    ) {
        every {
            packageManager.checkPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                LocationAvailabilityCheckerImplFixture.PACKAGE_NAME,
            )
        } returns if (fineGranted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
        every {
            packageManager.checkPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                LocationAvailabilityCheckerImplFixture.PACKAGE_NAME,
            )
        } returns if (coarseGranted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
    }
}
