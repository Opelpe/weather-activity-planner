package com.pnow.weatheractivityplanner.feature.locationsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnow.weatheractivityplanner.data.di.DefaultDispatcher
import com.pnow.weatheractivityplanner.domain.usecase.SearchLocationsUseCase
import com.pnow.weatheractivityplanner.feature.common.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
class LocationSearchViewModel @Inject constructor(
    private val searchLocationsUseCase: SearchLocationsUseCase,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _searchState = MutableStateFlow(LocationSearchUiState())
    val searchState: StateFlow<LocationSearchUiState> = _searchState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    @Volatile
    private var lastSuccessfulQuery: String? = null

    init {
        queryFlow
            .debounce(SEARCH_DEBOUNCE_MS.milliseconds)
            .onEach { query ->
                if (query.isNotBlank() && query != lastSuccessfulQuery) {
                    searchLocations(query)
                }
            }
            .flowOn(defaultDispatcher)
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(query: String) {
        _searchState.update { it.copy(searchQuery = query, error = null) }
        if (query.isBlank()) {
            _searchState.update { it.copy(isLoading = false, locations = emptyList()) }
        }
        queryFlow.value = query
    }

    fun onRetry() {
        if (_searchState.value.error != null) {
            searchLocations(_searchState.value.searchQuery)
        }
    }

    private fun searchLocations(query: String) {
        viewModelScope.launch {
            _searchState.update { it.copy(isLoading = true, error = null) }
            searchLocationsUseCase(query = query)
                .onSuccess { locations ->
                    lastSuccessfulQuery = query
                    _searchState.update {
                        it.copy(
                            isLoading = false,
                            locations = locations.map { location -> location.toUiModel() },
                        )
                    }
                }
                .onFailure { throwable ->
                    _searchState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.toUiError(),
                        )
                    }
                }
        }
    }

    private companion object {

        const val SEARCH_DEBOUNCE_MS = 500L
    }
}
