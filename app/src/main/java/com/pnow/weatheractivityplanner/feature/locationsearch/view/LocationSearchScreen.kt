package com.pnow.weatheractivityplanner.feature.locationsearch.view

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.common.toMessage
import com.pnow.weatheractivityplanner.feature.common.view.FullScreenError
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

@Composable
private fun LocationSearchResultsContent(
    modifier: Modifier = Modifier,
    state: LocationSearchUiState,
    onRetry: () -> Unit,
    onLocationSelected: (LocationUiModel) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.error != null ->
                FullScreenError(
                    message = state.error.toMessage(),
                    onRetry = onRetry,
                )

            state.locations.isNotEmpty() ->
                LocationResultList(
                    locations = state.locations,
                    onLocationSelected = onLocationSelected,
                )

            state.searchQuery.isNotBlank() && !state.isLoading ->
                DefaultSearchContentLabel(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.weather_activity_no_cities_found),
                )

            else ->
                DefaultSearchContentLabel(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.weather_activity_search_prompt),
                )
        }
    }
}

@Composable
private fun LocationResultList(
    modifier: Modifier = Modifier,
    locations: List<LocationUiModel>,
    onLocationSelected: (LocationUiModel) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = Dimens.Spacing6),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
    ) {
        items(items = locations, key = { it.id }) { location ->
            LocationResultItem(
                location = location,
                onClick = { onLocationSelected(location) },
            )
        }
    }
}

@Composable
private fun LocationResultItem(
    modifier: Modifier = Modifier,
    location: LocationUiModel,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(Dimens.Spacing8)) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = location.country,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun DefaultSearchContentLabel(
    modifier: Modifier,
    text: String,
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.Spacing16),
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
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
