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
import com.pnow.weatheractivityplanner.navigation.toLocationOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WeatherRecommendationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getActivityRankingsUseCase: GetActivityRankingsUseCase,
) : ViewModel() {

    private val location: Location? = savedStateHandle.toLocationOrNull()

    private val _state = MutableStateFlow(
        WeatherRecommendationUiState(
            locationName = location?.name.orEmpty(),
            locationCountry = location?.country.orEmpty(),
            error = if (location == null) UiError.InvalidNavigationArguments else null,
        ),
    )
    val state: StateFlow<WeatherRecommendationUiState> = _state.asStateFlow()

    init {
        location?.let(::loadRankings)
    }

    fun onRetry() {
        location?.let(::loadRankings)
    }

    fun onRefresh() {
        location?.let(::refreshRankings)
    }

    private fun loadRankings(location: Location) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            fetchRankings(location)
        }
    }

    private fun refreshRankings(location: Location) {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }
            fetchRankings(location, forceRefresh = true)
        }
    }

    private suspend fun fetchRankings(
        location: Location,
        forceRefresh: Boolean = false,
    ) {
        getActivityRankingsUseCase(
            location = location,
            forceRefresh = forceRefresh,
        )
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
