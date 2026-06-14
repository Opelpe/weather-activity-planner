package com.pnow.weatheractivityplanner.feature.locationsearch.view

import android.content.res.Configuration
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.common.toMessage
import com.pnow.weatheractivityplanner.feature.common.view.FullScreenError
import com.pnow.weatheractivityplanner.feature.locationsearch.LocationSearchPreviewData
import com.pnow.weatheractivityplanner.feature.locationsearch.LocationSearchUiState
import com.pnow.weatheractivityplanner.feature.locationsearch.LocationUiModel
import com.pnow.weatheractivityplanner.ui.theme.WeatherActivityPlannerTheme
import com.pnow.weatheractivityplanner.util.Dimens

@Composable
fun LocationSearchResultsContent(
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
fun LocationResultList(
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
fun DefaultSearchContentLabel(
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
private fun LocationResultsContentSearchPromptPreview() {
    WeatherActivityPlannerTheme {
        LocationSearchResultsContent(
            state = LocationSearchUiState(),
            onRetry = {},
            onLocationSelected = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationResultsContentSuccessPreview() {
    WeatherActivityPlannerTheme {
        LocationSearchResultsContent(
            state = LocationSearchUiState(
                searchQuery = LocationSearchPreviewData.SEARCH_QUERY,
                locations = LocationSearchPreviewData.Locations,
            ),
            onRetry = {},
            onLocationSelected = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationResultsContentNoDataPreview() {
    WeatherActivityPlannerTheme {
        LocationSearchResultsContent(
            state = LocationSearchUiState(
                searchQuery = LocationSearchPreviewData.SEARCH_QUERY,
                locations = emptyList(),
            ),
            onRetry = {},
            onLocationSelected = {},
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LocationResultsContentErrorPreview() {
    WeatherActivityPlannerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LocationSearchResultsContent(
                state = LocationSearchUiState(
                    searchQuery = LocationSearchPreviewData.SEARCH_QUERY,
                    error = UiError.NetworkUnavailable,
                ),
                onRetry = {},
                onLocationSelected = {},
            )
        }
    }
}
