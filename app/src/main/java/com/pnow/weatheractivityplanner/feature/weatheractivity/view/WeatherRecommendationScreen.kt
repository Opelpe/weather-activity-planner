package com.pnow.weatheractivityplanner.feature.weatheractivity.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.common.toMessage
import com.pnow.weatheractivityplanner.feature.common.view.FullScreenError
import com.pnow.weatheractivityplanner.feature.common.view.FullScreenLoading
import com.pnow.weatheractivityplanner.feature.weatheractivity.WeatherRecommendationPreviewData
import com.pnow.weatheractivityplanner.feature.weatheractivity.WeatherRecommendationUiState
import com.pnow.weatheractivityplanner.feature.weatheractivity.WeatherRecommendationViewModel
import com.pnow.weatheractivityplanner.ui.theme.WeatherActivityPlannerTheme
import com.pnow.weatheractivityplanner.util.Dimens

@Composable
fun WeatherRecommendationScreen(
    modifier: Modifier = Modifier,
    onNavigateToForecast: () -> Unit,
    viewModel: WeatherRecommendationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    WeatherRecommendationContent(
        modifier = modifier,
        state = state,
        onRetry = viewModel::onRetry,
        onRefresh = viewModel::onRefresh,
        onWeatherCardClick = onNavigateToForecast,
    )
}

@Composable
private fun WeatherRecommendationContent(
    modifier: Modifier = Modifier,
    state: WeatherRecommendationUiState,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onWeatherCardClick: () -> Unit,
) {
    when {
        state.isLoading ->
            FullScreenLoading(modifier = modifier)

        state.error != null ->
            FullScreenError(
                modifier = modifier,
                message = state.error.toMessage(),
                onRetry = onRetry,
            )

        else ->
            WeatherRecommendationResultsContent(
                modifier = modifier,
                state = state,
                onRefresh = onRefresh,
                onWeatherCardClick = onWeatherCardClick,
            )

    }
}

@Composable
private fun WeatherRecommendationResultsContent(
    modifier: Modifier = Modifier,
    state: WeatherRecommendationUiState,
    onRefresh: () -> Unit,
    onWeatherCardClick: () -> Unit,
) {
    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.Spacing12),
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing16),
        ) {
            item {
                state.currentWeather?.let { currentWeather ->
                    CurrentWeatherCard(
                        locationName = state.locationName,
                        locationCountry = state.locationCountry,
                        currentWeather = currentWeather,
                        onClick = onWeatherCardClick,
                    )
                }
            }

            item {
                ActivitiesRankingHeader()
            }

            items(
                items = state.ranking,
                key = { ranking -> ranking.activities.name },
            ) { ranking ->
                ActivitiesRankingItem(ranking = ranking)
            }

        }
    }
}

@Composable
private fun ActivitiesRankingHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.weather_activity_ranking_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(R.string.weather_activity_ranking_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WeatherRecommendationContentSuccessPreview() {
    WeatherActivityPlannerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            WeatherRecommendationContent(
                state = WeatherRecommendationPreviewData.SuccessState,
                onRetry = {},
                onRefresh = {},
                onWeatherCardClick = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherRecommendationContentErrorPreview() {
    WeatherActivityPlannerTheme {
        WeatherRecommendationContent(
            state = WeatherRecommendationUiState(
                locationName = WeatherRecommendationPreviewData.LOCATION_NAME,
                locationCountry = WeatherRecommendationPreviewData.LOCATION_COUNTRY,
                error = UiError.NetworkUnavailable,
            ),
            onRetry = {},
            onRefresh = {},
            onWeatherCardClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherRecommendationContentLoadingPreview() {
    WeatherActivityPlannerTheme {
        WeatherRecommendationContent(
            state = WeatherRecommendationUiState(
                isLoading = true,
            ),
            onRetry = {},
            onRefresh = {},
            onWeatherCardClick = {},
        )
    }
}
