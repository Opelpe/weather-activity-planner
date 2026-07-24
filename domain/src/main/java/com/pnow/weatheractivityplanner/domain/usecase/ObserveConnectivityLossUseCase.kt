package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.repository.ConnectivityRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class ObserveConnectivityLossUseCase @Inject constructor(
    private val connectivityRepository: ConnectivityRepository,
) {

    operator fun invoke(): Flow<Unit> =
        connectivityRepository.isConnected()
            .filter { isConnected -> !isConnected }
            .map { }
}
