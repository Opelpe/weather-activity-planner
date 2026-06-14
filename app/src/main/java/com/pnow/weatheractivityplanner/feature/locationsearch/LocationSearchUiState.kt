package com.pnow.weatheractivityplanner.feature.locationsearch

import com.pnow.weatheractivityplanner.feature.common.UiError

data class LocationSearchUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val locations: List<LocationUiModel> = emptyList(),
    val error: UiError? = null,
)
