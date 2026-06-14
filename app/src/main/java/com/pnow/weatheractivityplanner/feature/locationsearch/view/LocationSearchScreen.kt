package com.pnow.weatheractivityplanner.feature.locationsearch.view

import android.content.res.Configuration
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.locationsearch.LocationSearchPreviewData
import com.pnow.weatheractivityplanner.feature.locationsearch.LocationSearchUiState
import com.pnow.weatheractivityplanner.feature.locationsearch.LocationSearchViewModel
import com.pnow.weatheractivityplanner.feature.locationsearch.LocationUiModel
import com.pnow.weatheractivityplanner.ui.theme.WeatherActivityPlannerTheme
import com.pnow.weatheractivityplanner.util.Dimens

@Composable
fun LocationSearchScreen(
    modifier: Modifier = Modifier,
    onNavigateToRankings: (LocationUiModel) -> Unit,
    viewModel: LocationSearchViewModel = hiltViewModel(),
) {
    val state by viewModel.searchState.collectAsStateWithLifecycle()

    LocationSearchContent(
        modifier = modifier,
        state = state,
        onQueryChange = viewModel::onQueryChanged,
        onRetry = viewModel::onRetry,
        onLocationSelected = onNavigateToRankings,
    )
}

@Composable
private fun LocationSearchContent(
    modifier: Modifier = Modifier,
    state: LocationSearchUiState,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    onLocationSelected: (LocationUiModel) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .padding(horizontal = Dimens.Spacing16, vertical = Dimens.Spacing32),
        verticalArrangement = Arrangement.Bottom,
    ) {

        LocationSearchBar(
            query = state.searchQuery,
            isLoading = state.isLoading,
            onQueryChange = onQueryChange,
        )

        LocationSearchResultsContent(
            modifier = Modifier.weight(1f),
            state = state,
            onRetry = onRetry,
            onLocationSelected = { location ->
                onQueryChange(location.name)
                onLocationSelected(location)
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationSearchScreenPromptPreview() {
    WeatherActivityPlannerTheme {
        LocationSearchContent(
            state = LocationSearchUiState(),
            onQueryChange = {},
            onRetry = {},
            onLocationSelected = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationSearchScreenSuccessPreview() {
    WeatherActivityPlannerTheme {
        LocationSearchContent(
            state = LocationSearchUiState(
                searchQuery = LocationSearchPreviewData.SEARCH_QUERY,
                locations = LocationSearchPreviewData.Locations,
            ),
            onQueryChange = {},
            onRetry = {},
            onLocationSelected = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationSearchScreenNoResultsPreview() {
    WeatherActivityPlannerTheme {
        LocationSearchContent(
            state = LocationSearchUiState(
                searchQuery = LocationSearchPreviewData.SEARCH_QUERY,
                locations = emptyList(),
            ),
            onQueryChange = {},
            onRetry = {},
            onLocationSelected = {},
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LocationSearchScreenErrorPreview() {
    WeatherActivityPlannerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LocationSearchContent(
                state = LocationSearchUiState(
                    searchQuery = LocationSearchPreviewData.SEARCH_QUERY,
                    error = UiError.NetworkUnavailable,
                ),
                onQueryChange = {},
                onRetry = {},
                onLocationSelected = {},
            )
        }
    }
}
