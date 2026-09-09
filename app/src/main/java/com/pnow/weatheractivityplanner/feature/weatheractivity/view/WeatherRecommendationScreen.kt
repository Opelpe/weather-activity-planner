package com.pnow.weatheractivityplanner.feature.weatheractivity.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.common.effect.ObserveCachedDataNotice
import com.pnow.weatheractivityplanner.feature.common.effect.ObserveSnackbarActions
import com.pnow.weatheractivityplanner.feature.common.toMessage
import com.pnow.weatheractivityplanner.feature.common.view.FullScreenError
import com.pnow.weatheractivityplanner.feature.common.view.FullScreenLoading
import com.pnow.weatheractivityplanner.feature.weatheractivity.WeatherRecommendationPreviewData
import com.pnow.weatheractivityplanner.feature.weatheractivity.WeatherRecommendationUiState
import com.pnow.weatheractivityplanner.feature.weatheractivity.WeatherRecommendationViewModel
import com.pnow.weatheractivityplanner.ui.theme.PreviewLight
import com.pnow.weatheractivityplanner.ui.theme.PreviewLightDark
import com.pnow.weatheractivityplanner.ui.theme.WeatherActivityPlannerTheme
import com.pnow.weatheractivityplanner.util.Dimens

@Composable
fun WeatherRecommendationScreen(
    modifier: Modifier = Modifier,
    onNavigateToForecast: () -> Unit,
    viewModel: WeatherRecommendationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val incompleteDataNoticeMessage = stringResource(R.string.common_incomplete_data_notice_message)

    ObserveSnackbarActions(
        events = viewModel.incompleteDataNotices,
        snackbarHostState = snackbarHostState,
        message = { incompleteDataNoticeMessage },
    )

    ObserveCachedDataNotice(
        notices = viewModel.cachedDataNotices,
        snackbarHostState = snackbarHostState,
        onRefresh = viewModel::onRefresh,
    )

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        WeatherRecommendationContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onRetry = viewModel::onRetry,
            onRefresh = viewModel::onRefresh,
            onWeatherCardClick = onNavigateToForecast,
            onDayCountChanged = viewModel::onDayCountChanged,
        )
    }
}

@Composable
private fun WeatherRecommendationContent(
    modifier: Modifier = Modifier,
    state: WeatherRecommendationUiState,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onWeatherCardClick: () -> Unit,
    onDayCountChanged: (Int) -> Unit,
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
                onDayCountChanged = onDayCountChanged,
            )

    }
}

@Composable
private fun WeatherRecommendationResultsContent(
    modifier: Modifier = Modifier,
    state: WeatherRecommendationUiState,
    onRefresh: () -> Unit,
    onWeatherCardClick: () -> Unit,
    onDayCountChanged: (Int) -> Unit,
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
                ActivitiesRankingHeader(
                    selectedDayCount = state.selectedDayCount,
                    onDayCountChanged = onDayCountChanged,
                )
            }

            items(
                items = state.ranking,
                key = { ranking -> ranking.activities.name },
            ) { ranking ->
                ActivitiesRankingItem(ranking = ranking, selectedDayCount = state.selectedDayCount)
            }

        }
    }
}

@Composable
private fun ActivitiesRankingHeader(
    modifier: Modifier = Modifier,
    selectedDayCount: Int,
    onDayCountChanged: (Int) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
    ) {
        RankingHeaderTitle()
        RankingHeaderDaySelector(
            selectedDayCount = selectedDayCount,
            onDayCountChanged = onDayCountChanged,
        )
        RankingHeaderSubtitle(selectedDayCount = selectedDayCount)
    }
}

@Composable
private fun RankingHeaderTitle(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.weather_activity_ranking_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun RankingHeaderDaySelector(
    modifier: Modifier = Modifier,
    selectedDayCount: Int,
    onDayCountChanged: (Int) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.weather_activity_day_selector_field_label),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ActivitiesRankingDaySelector(
            selectedDays = selectedDayCount,
            onDaysChanged = onDayCountChanged,
        )
    }
}

@Composable
private fun RankingHeaderSubtitle(modifier: Modifier = Modifier, selectedDayCount: Int) {
    Text(
        modifier = modifier,
        text = pluralStringResource(
            R.plurals.weather_activity_ranking_subtitle,
            selectedDayCount,
            selectedDayCount,
        ),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@PreviewLightDark
@Composable
private fun WeatherRecommendationContentSuccessPreview() {
    WeatherActivityPlannerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            WeatherRecommendationContent(
                state = WeatherRecommendationPreviewData.SuccessState,
                onRetry = {},
                onRefresh = {},
                onWeatherCardClick = {},
                onDayCountChanged = {},
            )
        }
    }
}

@PreviewLight
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
            onDayCountChanged = {},
        )
    }
}

@PreviewLight
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
            onDayCountChanged = {},
        )
    }
}
