package com.pnow.weatheractivityplanner.feature.forecast

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.usecase.GetForecastUseCase
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.common.toUiError
import com.pnow.weatheractivityplanner.navigation.toLocationOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WeatherForecastViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getForecastUseCase: GetForecastUseCase,
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

    init {
        location?.let(::loadForecast)
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
                        dailyForecast = forecast.daily.map { daily -> daily.toUiModel() },
                    )
                }
            }
            .onFailure { throwable ->
                _forecastState.update {
                    it.copy(isLoading = false, isRefreshing = false, error = throwable.toUiError())
                }
            }
    }
}
