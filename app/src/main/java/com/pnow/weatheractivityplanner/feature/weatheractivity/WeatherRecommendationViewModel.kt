package com.pnow.weatheractivityplanner.feature.weatheractivity

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnow.weatheractivityplanner.data.di.DefaultDispatcher
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.usecase.GetActivityRankingsUseCase
import com.pnow.weatheractivityplanner.domain.usecase.ObserveConnectivityLossUseCase
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.common.toUiError
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.toUiModel
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.toUiModels
import com.pnow.weatheractivityplanner.navigation.toLocationOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class WeatherRecommendationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getActivityRankingsUseCase: GetActivityRankingsUseCase,
    private val observeConnectivityLossUseCase: ObserveConnectivityLossUseCase,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
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

    private val _cachedDataNotices = Channel<Unit>(Channel.BUFFERED)
    val cachedDataNotices: Flow<Unit> = _cachedDataNotices.receiveAsFlow()

    init {
        location?.let(::loadRankings)
        observeConnectivity()
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

    private fun observeConnectivity() {
        viewModelScope.launch {
            observeConnectivityLossUseCase().collect {
                if (_state.value.currentWeather != null) {
                    _cachedDataNotices.trySend(Unit)
                }
            }
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
        val rankingsResult = withContext(defaultDispatcher) {
            getActivityRankingsUseCase(
                location = location,
                forceRefresh = forceRefresh,
            )
        }

        rankingsResult
            .onSuccess { result ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        currentWeather = result.currentWeather.toUiModel(),
                        ranking = result.rankings.toUiModels(),
                    )
                }
                if (result.isCached) {
                    _cachedDataNotices.trySend(Unit)
                }
            }
            .onFailure { throwable ->
                _state.update {
                    it.copy(isLoading = false, isRefreshing = false, error = throwable.toUiError())
                }
            }
    }
}
