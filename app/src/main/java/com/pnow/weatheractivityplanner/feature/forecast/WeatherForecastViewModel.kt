package com.pnow.weatheractivityplanner.feature.forecast

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnow.weatheractivityplanner.domain.usecase.GetForecastUseCase
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.common.toUiError
import com.pnow.weatheractivityplanner.navigation.RouteArgKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class ForecastArgs(
    val locationName: String,
    val locationCountry: String,
    val latitude: Double,
    val longitude: Double,
)

private fun SavedStateHandle.toForecastArgsOrNull(): ForecastArgs? {
    val locationName: String = get(RouteArgKeys.LOCATION_NAME) ?: return null
    val locationCountry: String = get(RouteArgKeys.LOCATION_COUNTRY) ?: return null
    val latitude: Double = get(RouteArgKeys.LATITUDE) ?: return null
    val longitude: Double = get(RouteArgKeys.LONGITUDE) ?: return null
    return ForecastArgs(
        locationName = locationName,
        locationCountry = locationCountry,
        latitude = latitude,
        longitude = longitude,
    )
}

@HiltViewModel
class WeatherForecastViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getForecastUseCase: GetForecastUseCase,
) : ViewModel() {

    private val args = savedStateHandle.toForecastArgsOrNull()

    private val _forecastState = MutableStateFlow(
        WeatherForecastUiState(
            locationName = args?.locationName.orEmpty(),
            locationCountry = args?.locationCountry.orEmpty(),
            error = if (args == null) UiError.InvalidNavigationArguments else null,
        ),
    )
    val forecastState: StateFlow<WeatherForecastUiState> = _forecastState.asStateFlow()

    init {
        args?.let(::loadForecast)
    }

    fun onRetry() {
        args?.let(::loadForecast)
    }

    fun onRefresh() {
        args?.let(::refreshForecast)
    }

    private fun loadForecast(args: ForecastArgs) {
        viewModelScope.launch {
            _forecastState.update { it.copy(isLoading = true, error = null) }
            fetchForecast(args)
        }
    }

    private fun refreshForecast(args: ForecastArgs) {
        viewModelScope.launch {
            _forecastState.update { it.copy(isRefreshing = true, error = null) }
            fetchForecast(args)
        }
    }

    private suspend fun fetchForecast(args: ForecastArgs) {
        getForecastUseCase(latitude = args.latitude, longitude = args.longitude)
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
