package com.pnow.weatheractivityplanner.feature.weatheractivity

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.usecase.GetActivityRankingsUseCase
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.common.toUiError
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.toUiModel
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.toUiModels
import com.pnow.weatheractivityplanner.navigation.RouteArgKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class ActivitiesRankingArgs(
    val location: Location,
)

private fun SavedStateHandle.toActivityRankingsArgsOrNull(): ActivitiesRankingArgs? {
    val locationId: Long = get(RouteArgKeys.LOCATION_ID) ?: return null
    val locationName: String = get(RouteArgKeys.LOCATION_NAME) ?: return null
    val locationCountry: String = get(RouteArgKeys.LOCATION_COUNTRY) ?: return null
    val latitude: Double = get(RouteArgKeys.LATITUDE) ?: return null
    val longitude: Double = get(RouteArgKeys.LONGITUDE) ?: return null
    return ActivitiesRankingArgs(
        location = Location(
            id = locationId,
            name = locationName,
            latitude = latitude,
            longitude = longitude,
            country = locationCountry,
            countryCode = null,
            region = null,
        ),
    )
}

@HiltViewModel
class WeatherRecommendationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getActivityRankingsUseCase: GetActivityRankingsUseCase,
) : ViewModel() {

    private val args = savedStateHandle.toActivityRankingsArgsOrNull()

    private val _state = MutableStateFlow(
        WeatherRecommendationUiState(
            locationName = args?.location?.name.orEmpty(),
            locationCountry = args?.location?.country.orEmpty(),
            error = if (args == null) UiError.InvalidNavigationArguments else null,
        ),
    )
    val state: StateFlow<WeatherRecommendationUiState> = _state.asStateFlow()

    init {
        args?.let(::loadRankings)
    }

    fun onRetry() {
        args?.let(::loadRankings)
    }

    fun onRefresh() {
        args?.let(::refreshRankings)
    }

    private fun loadRankings(args: ActivitiesRankingArgs) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            fetchRankings(args)
        }
    }

    private fun refreshRankings(args: ActivitiesRankingArgs) {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }
            fetchRankings(args)
        }
    }

    private suspend fun fetchRankings(args: ActivitiesRankingArgs) {
        runCatching { getActivityRankingsUseCase(args.location).first() }
            .onSuccess { result ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        currentWeather = result.currentWeather.toUiModel(),
                        ranking = result.rankings.toUiModels(),
                    )
                }
            }
            .onFailure { throwable ->
                _state.update {
                    it.copy(isLoading = false, isRefreshing = false, error = throwable.toUiError())
                }
            }
    }
}
