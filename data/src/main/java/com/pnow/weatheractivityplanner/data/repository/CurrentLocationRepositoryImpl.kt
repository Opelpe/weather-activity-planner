package com.pnow.weatheractivityplanner.data.repository

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.pnow.weatheractivityplanner.data.di.IoDispatcher
import com.pnow.weatheractivityplanner.data.location.LocationAvailabilityChecker
import com.pnow.weatheractivityplanner.data.mapper.toLocationDomainError
import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.pnow.weatheractivityplanner.domain.model.Coordinates
import com.pnow.weatheractivityplanner.domain.repository.CurrentLocationRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

internal class CurrentLocationRepositoryImpl @Inject constructor(
    private val locationAvailabilityChecker: LocationAvailabilityChecker,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CurrentLocationRepository {

    override fun hasPermission(): Boolean = locationAvailabilityChecker.hasPermission()

    override suspend fun getCurrentLocation(): Result<Coordinates> =
        withContext(ioDispatcher) {
            if (!hasPermission()) {
                return@withContext Result.failure(DomainError.LocationPermissionDenied())
            }
            if (!locationAvailabilityChecker.isLocationEnabled()) {
                return@withContext Result.failure(DomainError.LocationDisabled())
            }

            runCatching { awaitLocationFix() }.fold(
                onSuccess = { Result.success(it) },
                onFailure = { e ->
                    if (e is CancellationException) {
                        throw e
                    } else {
                        Result.failure(e.toLocationDomainError())
                    }
                },
            )
        }

    // Lint can't trace the permission check across getCurrentLocation()/hasPermission();
    // it is verified by the caller before this is invoked.
    @SuppressLint("MissingPermission")
    private suspend fun awaitLocationFix(): Coordinates {
        val cancellationTokenSource = CancellationTokenSource()
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation { cancellationTokenSource.cancel() }
            fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token,
            )
                .addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(
                            Coordinates(
                                latitude = location.latitude,
                                longitude = location.longitude,
                            ),
                        )
                    } else {
                        continuation.resumeWithException(DomainError.LocationUnavailable())
                    }
                }
                .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
        }
    }
}
