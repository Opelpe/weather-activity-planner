package com.pnow.weatheractivityplanner.feature.forecast

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.usecase.GetForecastUseCase
import com.pnow.weatheractivityplanner.domain.usecase.ObserveConnectivityLossUseCase
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.common.toUiError
import com.pnow.weatheractivityplanner.navigation.toLocationOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WeatherForecastViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getForecastUseCase: GetForecastUseCase,
    private val observeConnectivityLossUseCase: ObserveConnectivityLossUseCase,
) : ViewModel() {

    private val location: Location? = savedStateHandle.toLocationOrNull()

    private val _forecastState = MutableStateFlow(
        WeatherForecastUiState(
            locationName = location?.name.orEmpty(),
            locationCountry = location?.country.orEmpty(),
            error = if (location == null) UiError.InvalidNavigationArguments else null,
        ),
    )
    val forecastState: StateFlow<WeatherForecastUiState> = _forecastState.asStateFlow()

    private val _cachedDataNotices = Channel<Unit>(Channel.BUFFERED)
    val cachedDataNotices: Flow<Unit> = _cachedDataNotices.receiveAsFlow()

    init {
        location?.let(::loadForecast)
        observeConnectivity()
    }

    fun onRetry() {
        location?.let(::loadForecast)
    }

    fun onRefresh() {
        location?.let(::refreshForecast)
    }

    private fun loadForecast(location: Location) {
        viewModelScope.launch {
            _forecastState.update { it.copy(isLoading = true, error = null) }
            fetchForecast(location)
        }
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            observeConnectivityLossUseCase().collect {
                if (_forecastState.value.dailyForecast.isNotEmpty()) {
                    _cachedDataNotices.trySend(Unit)
                }
            }
        }
    }

    private fun refreshForecast(location: Location) {
        viewModelScope.launch {
            _forecastState.update { it.copy(isRefreshing = true, error = null) }
            fetchForecast(location, forceRefresh = true)
        }
    }

    private suspend fun fetchForecast(
        location: Location,
        forceRefresh: Boolean = false,
    ) {
        getForecastUseCase(location = location, forceRefresh = forceRefresh)
            .onSuccess { forecast ->
                _forecastState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        dailyForecast = forecast.daily.drop(SKIPPED_CURRENT_DAY_COUNT)
                            .take(DISPLAYED_DAY_COUNT)
                            .mapIndexed { index, daily -> daily.toUiModel(isTomorrow = index == 0) },
                    )
                }
                if (forecast.isCached) {
                    _cachedDataNotices.trySend(Unit)
                }
            }
            .onFailure { throwable ->
                _forecastState.update {
                    it.copy(isLoading = false, isRefreshing = false, error = throwable.toUiError())
                }
            }
    }

    private companion object {

        const val SKIPPED_CURRENT_DAY_COUNT = 1
        const val DISPLAYED_DAY_COUNT = 7
    }
}
