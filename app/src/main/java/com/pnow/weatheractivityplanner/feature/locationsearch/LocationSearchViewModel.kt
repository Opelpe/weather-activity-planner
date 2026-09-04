package com.pnow.weatheractivityplanner.feature.locationsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnow.weatheractivityplanner.data.di.DefaultDispatcher
import com.pnow.weatheractivityplanner.domain.usecase.SearchLocationsUseCase
import com.pnow.weatheractivityplanner.feature.common.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
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
    private var lastSuccessfulSearch: SearchCache? = null

    init {
        viewModelScope.launch {
            queryFlow
                .debounce { query ->
                    if (query.isBlank()) {
                        Duration.ZERO
                    } else {
                        SEARCH_DEBOUNCE_MS.milliseconds
                    }
                }
                .flowOn(defaultDispatcher)
                .collectLatest { query ->
                    if (query.isBlank()) return@collectLatest
                    val cached = lastSuccessfulSearch
                    if (cached != null && cached.query == query) {
                        _searchState.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                                locations = cached.locations,
                            )
                        }
                    } else {
                        searchLocations(query)
                    }
                }
        }
    }

    fun onQueryChanged(query: String) {
        _searchState.update { it.copy(searchQuery = query, error = null) }
        if (query.isBlank()) {
            _searchState.update { it.copy(isLoading = false, locations = emptyList()) }
        }
        queryFlow.value = query
    }

    fun onRetry() {
        val state = _searchState.value
        if (state.error != null && !state.isLoading) {
            viewModelScope.launch {
                searchLocations(
                    query = state.searchQuery,
                    clearStaleResults = true,
                )
            }
        }
    }

    private suspend fun searchLocations(
        query: String,
        clearStaleResults: Boolean = false,
    ) {
        _searchState.update {
            it.copy(
                isLoading = true,
                error = null,
                locations = if (clearStaleResults) emptyList() else it.locations,
            )
        }
        searchLocationsUseCase(query = query)
            .onSuccess { locations ->
                val uiLocations = locations.map { location -> location.toUiModel() }
                lastSuccessfulSearch = SearchCache(query = query, locations = uiLocations)
                _searchState.update {
                    it.copy(
                        isLoading = false,
                        locations = uiLocations,
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

    private companion object {

        const val SEARCH_DEBOUNCE_MS = 400L
    }
}

private data class SearchCache(
    val query: String,
    val locations: List<LocationUiModel>,
)
