package com.pnow.weatheractivityplanner.domain.usecase

import app.cash.turbine.test
import com.pnow.weatheractivityplanner.domain.repository.ConnectivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveConnectivityLossUseCaseTest {

    @Test
    fun `given connection is lost, when invoked, then emits`() = runTest {
        val connectivity = MutableStateFlow(true)
        val useCase = ObserveConnectivityLossUseCase(FakeConnectivityRepository(connectivity))

        useCase().test {
            connectivity.value = false

            assertEquals(Unit, awaitItem()) // connectivity lost

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given connection stays available, when invoked, then emits nothing`() = runTest {
        val connectivity = MutableStateFlow(true)
        val useCase = ObserveConnectivityLossUseCase(FakeConnectivityRepository(connectivity))

        useCase().test {
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given connection is lost then regained, when invoked, then emits only for the loss`() =
        runTest {
            val connectivity = MutableStateFlow(true)
            val useCase = ObserveConnectivityLossUseCase(FakeConnectivityRepository(connectivity))

            useCase().test {
                connectivity.value = false
                assertEquals(Unit, awaitItem()) // first loss

                connectivity.value = true // regained, no signal expected

                connectivity.value = false
                assertEquals(Unit, awaitItem()) // second loss

                cancelAndIgnoreRemainingEvents()
            }
        }

    private class FakeConnectivityRepository(
        private val connectivityFlow: Flow<Boolean>,
    ) : ConnectivityRepository {

        override fun isConnected(): Flow<Boolean> = connectivityFlow
    }
}
