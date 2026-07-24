package com.pnow.weatheractivityplanner.data.repository

import android.net.ConnectivityManager
import android.net.Network
import app.cash.turbine.test
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConnectivityRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val connectivityManager = mockk<ConnectivityManager>()
    private val network = mockk<Network>()

    private val repository = ConnectivityRepositoryImpl(
        connectivityManager = connectivityManager,
        ioDispatcher = testDispatcher,
    )

    @Test
    fun `given active network present, when isConnected collected, then emits true first`() =
        runTest(testDispatcher) {
            every { connectivityManager.activeNetwork } returns network
            stubCallbackRegistration()

            repository.isConnected().test {
                assertTrue(awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given no active network, when isConnected collected, then emits false first`() =
        runTest(testDispatcher) {
            every { connectivityManager.activeNetwork } returns null
            stubCallbackRegistration()

            repository.isConnected().test {
                assertFalse(awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given network lost callback, when isConnected collected, then emits false after true`() =
        runTest(testDispatcher) {
            every { connectivityManager.activeNetwork } returns network
            val callback = stubCallbackRegistration()

            repository.isConnected().test {
                assertTrue(awaitItem())

                callback.captured.onLost(network)
                assertFalse(awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given network available callback after loss, when isConnected collected, then emits true again`() =
        runTest(testDispatcher) {
            every { connectivityManager.activeNetwork } returns network
            val callback = stubCallbackRegistration()

            repository.isConnected().test {
                assertTrue(awaitItem())

                callback.captured.onLost(network)
                assertFalse(awaitItem())

                callback.captured.onAvailable(network)
                assertTrue(awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given collection is cancelled, then network callback is unregistered`() =
        runTest(testDispatcher) {
            every { connectivityManager.activeNetwork } returns network
            val callback = stubCallbackRegistration()

            repository.isConnected().test {
                awaitItem()

                cancelAndIgnoreRemainingEvents()
            }

            verify { connectivityManager.unregisterNetworkCallback(callback.captured) }
        }

    private fun stubCallbackRegistration(): CapturingSlot<ConnectivityManager.NetworkCallback> {
        val callback = slot<ConnectivityManager.NetworkCallback>()
        every { connectivityManager.registerDefaultNetworkCallback(capture(callback)) } just Runs
        every { connectivityManager.unregisterNetworkCallback(any<ConnectivityManager.NetworkCallback>()) } just Runs
        return callback
    }
}
