package com.pnow.weatheractivityplanner.data.repository

import android.net.ConnectivityManager
import android.net.Network
import com.pnow.weatheractivityplanner.data.di.IoDispatcher
import com.pnow.weatheractivityplanner.domain.repository.ConnectivityRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

internal class ConnectivityRepositoryImpl @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ConnectivityRepository {

    override fun isConnected(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }
        }
        trySend(connectivityManager.activeNetwork != null)
        connectivityManager.registerDefaultNetworkCallback(callback)
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged().flowOn(ioDispatcher)
}
