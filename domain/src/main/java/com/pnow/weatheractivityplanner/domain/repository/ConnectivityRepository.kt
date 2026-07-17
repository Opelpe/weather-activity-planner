package com.pnow.weatheractivityplanner.domain.repository

import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {

    fun isConnected(): Flow<Boolean>
}
