package com.pnow.weatheractivityplanner.data.repository

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.pnow.weatheractivityplanner.data.location.LocationAvailabilityChecker
import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.pnow.weatheractivityplanner.domain.model.Coordinates
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private object CurrentLocationFixture {

    const val LATITUDE = 51.5
    const val LONGITUDE = -0.1
}

private class FakeLocationAvailabilityChecker(
    private val hasPermission: Boolean = true,
    private val isLocationEnabled: Boolean = true,
) : LocationAvailabilityChecker {

    override fun hasPermission(): Boolean = hasPermission

    override fun isLocationEnabled(): Boolean = isLocationEnabled
}

@OptIn(ExperimentalCoroutinesApi::class)
class CurrentLocationRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fusedLocationProviderClient = mockk<FusedLocationProviderClient>()

    private fun repositoryWith(locationAvailabilityChecker: LocationAvailabilityChecker) =
        CurrentLocationRepositoryImpl(
            locationAvailabilityChecker = locationAvailabilityChecker,
            fusedLocationProviderClient = fusedLocationProviderClient,
            ioDispatcher = testDispatcher,
        )

    @Test
    fun `given checker reports permission granted, when hasPermission, then returns true`() {
        val repository = repositoryWith(FakeLocationAvailabilityChecker(hasPermission = true))

        assertTrue(repository.hasPermission())
    }

    @Test
    fun `given checker reports permission not granted, when hasPermission, then returns false`() {
        val repository = repositoryWith(FakeLocationAvailabilityChecker(hasPermission = false))

        assertFalse(repository.hasPermission())
    }

    @Test
    fun `given permission not granted, when getCurrentLocation, then returns LocationPermissionDenied`() =
        runTest(testDispatcher) {
            val repository = repositoryWith(FakeLocationAvailabilityChecker(hasPermission = false))

            val result = repository.getCurrentLocation()

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is DomainError.LocationPermissionDenied)
        }

    @Test
    fun `given location services disabled, when getCurrentLocation, then returns LocationDisabled`() =
        runTest(testDispatcher) {
            val repository = repositoryWith(FakeLocationAvailabilityChecker(isLocationEnabled = false))

            val result = repository.getCurrentLocation()

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is DomainError.LocationDisabled)
        }

    @Test
    fun `given permission granted and a fix is found, when getCurrentLocation, then returns coordinates`() =
        runTest(testDispatcher) {
            val repository = repositoryWith(FakeLocationAvailabilityChecker())
            val location = mockk<Location> {
                every { latitude } returns CurrentLocationFixture.LATITUDE
                every { longitude } returns CurrentLocationFixture.LONGITUDE
            }
            val successListener = stubGetCurrentLocationTask()

            val deferred = async { repository.getCurrentLocation() }
            advanceUntilIdle()
            successListener.captured.onSuccess(location)
            advanceUntilIdle()
            val result = deferred.await()

            assertTrue(result.isSuccess)
            assertEquals(
                Coordinates(latitude = CurrentLocationFixture.LATITUDE, longitude = CurrentLocationFixture.LONGITUDE),
                result.getOrNull(),
            )
        }

    @Test
    fun `given permission granted but no fix available, when getCurrentLocation, then returns LocationUnavailable`() =
        runTest(testDispatcher) {
            val repository = repositoryWith(FakeLocationAvailabilityChecker())
            val successListener = stubGetCurrentLocationTask()

            val deferred = async { repository.getCurrentLocation() }
            advanceUntilIdle()
            successListener.captured.onSuccess(null)
            advanceUntilIdle()
            val result = deferred.await()

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is DomainError.LocationUnavailable)
        }

    @Test
    fun `given fetching fails with SecurityException, when getCurrentLocation, then returns LocationPermissionDenied`() =
        runTest(testDispatcher) {
            val repository = repositoryWith(FakeLocationAvailabilityChecker())
            val failureListener = stubFailingGetCurrentLocationTask()

            val deferred = async { repository.getCurrentLocation() }
            advanceUntilIdle()
            failureListener.captured.onFailure(SecurityException("Permission revoked"))
            advanceUntilIdle()
            val result = deferred.await()

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is DomainError.LocationPermissionDenied)
        }

    @Test
    fun `given fetching fails with unexpected exception, when getCurrentLocation, then returns Unknown`() =
        runTest(testDispatcher) {
            val repository = repositoryWith(FakeLocationAvailabilityChecker())
            val failureListener = stubFailingGetCurrentLocationTask()
            val cause = RuntimeException("Location fetch failed")

            val deferred = async { repository.getCurrentLocation() }
            advanceUntilIdle()
            failureListener.captured.onFailure(cause)
            advanceUntilIdle()
            val result = deferred.await()

            assertTrue(result.isFailure)
            val error = result.exceptionOrNull() as DomainError.Unknown
            // Coroutine stack-trace recovery copies the exception across the suspension point,
            // so identity isn't preserved here - message is.
            assertEquals(cause.message, error.cause.message)
        }

    @Test(expected = CancellationException::class)
    fun `given fetching fails with CancellationException, when getCurrentLocation, then propagates cancellation`() =
        runTest(testDispatcher) {
            val repository = repositoryWith(FakeLocationAvailabilityChecker())
            val failureListener = stubFailingGetCurrentLocationTask()

            val deferred = async { repository.getCurrentLocation() }
            advanceUntilIdle()
            failureListener.captured.onFailure(CancellationException("Scope cancelled"))
            advanceUntilIdle()
            deferred.await()
        }

    private fun stubGetCurrentLocationTask(): CapturingSlot<OnSuccessListener<Location>> {
        val task = mockk<Task<Location>>(relaxed = true)
        val successListener = slot<OnSuccessListener<Location>>()
        every { task.addOnSuccessListener(capture(successListener)) } returns task
        every { task.addOnFailureListener(any()) } returns task
        every {
            fusedLocationProviderClient.getCurrentLocation(any<Int>(), any<CancellationToken>())
        } returns task
        return successListener
    }

    private fun stubFailingGetCurrentLocationTask(): CapturingSlot<OnFailureListener> {
        val task = mockk<Task<Location>>(relaxed = true)
        val failureListener = slot<OnFailureListener>()
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(capture(failureListener)) } returns task
        every {
            fusedLocationProviderClient.getCurrentLocation(any<Int>(), any<CancellationToken>())
        } returns task
        return failureListener
    }
}
