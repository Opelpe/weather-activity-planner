package com.pnow.weatheractivityplanner.feature.common.effect

import android.Manifest
import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

private const val LOCATION_SETTINGS_REQUEST_INTERVAL_MS = 10_000L

@Composable
fun rememberRequestCurrentLocationAccess(
    hasPermission: () -> Boolean,
    onAccessGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onSettingsUnavailable: () -> Unit,
): () -> Unit {
    val context = LocalContext.current

    val resolutionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            onAccessGranted()
        } else {
            onSettingsUnavailable()
        }
    }

    fun checkLocationSettingsAndResolve() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            LOCATION_SETTINGS_REQUEST_INTERVAL_MS,
        ).build()
        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        LocationServices.getSettingsClient(context)
            .checkLocationSettings(settingsRequest)
            .addOnSuccessListener { onAccessGranted() }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    resolutionLauncher.launch(
                        IntentSenderRequest.Builder(exception.resolution).build(),
                    )
                } else {
                    onSettingsUnavailable()
                }
            }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { grantResults ->
        val granted = grantResults[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grantResults[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            checkLocationSettingsAndResolve()
        } else {
            onPermissionDenied()
        }
    }

    return {
        if (hasPermission()) {
            checkLocationSettingsAndResolve()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }
}
