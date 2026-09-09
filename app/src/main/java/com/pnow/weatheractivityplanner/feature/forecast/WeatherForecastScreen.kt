package com.pnow.weatheractivityplanner.feature.forecast

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.common.effect.ObserveCachedDataNotice
import com.pnow.weatheractivityplanner.feature.common.toMessage
import com.pnow.weatheractivityplanner.feature.common.view.FullScreenError
import com.pnow.weatheractivityplanner.feature.common.view.FullScreenLoading
import com.pnow.weatheractivityplanner.ui.theme.PreviewLight
import com.pnow.weatheractivityplanner.ui.theme.PreviewLightDark
import com.pnow.weatheractivityplanner.ui.theme.WeatherActivityPlannerTheme
import com.pnow.weatheractivityplanner.util.Dimens
import kotlin.math.roundToInt

@Composable
fun WeatherForecastScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    viewModel: WeatherForecastViewModel = hiltViewModel(),
) {
    val state by viewModel.forecastState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveCachedDataNotice(
        notices = viewModel.cachedDataNotices,
        snackbarHostState = snackbarHostState,
        onRefresh = viewModel::onRefresh,
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            WeatherForecastTopBar(
                title = stringResource(
                    R.string.weather_activity_city_country_format,
                    state.locationName,
                    state.locationCountry,
                ),
                onNavigateBack = onNavigateBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        WeatherForecastContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onRetry = viewModel::onRetry,
            onRefresh = viewModel::onRefresh,
        )
    }
}

@Composable
private fun WeatherForecastTopBar(
    modifier: Modifier = Modifier,
    title: String,
    onNavigateBack: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .height(Dimens.TopBarHeight)
            .padding(horizontal = Dimens.Spacing16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing12),
    ) {
        TopBarIcon(onClick = onNavigateBack)
        TopBarTitle(title = title)
    }
}

@Composable
private fun TopBarIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Image(
        modifier = modifier
            .size(Dimens.IconSize)
            .clickable(onClick = onClick),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
        painter = painterResource(R.drawable.ic_arrow_back),
        contentDescription = null,
    )
}

@Composable
private fun TopBarTitle(
    modifier: Modifier = Modifier,
    title: String,
) {
    SelectionContainer(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun WeatherForecastContent(
    modifier: Modifier = Modifier,
    state: WeatherForecastUiState,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
) {
    when {
        state.isLoading -> FullScreenLoading(modifier = modifier)

        state.error != null ->
            FullScreenError(
                modifier = modifier,
                message = state.error.toMessage(),
                onRetry = onRetry,
            )

        else -> WeatherForecastResultsContent(
            modifier = modifier,
            state = state,
            onRefresh = onRefresh,
        )
    }
}

@Composable
private fun WeatherForecastResultsContent(
    modifier: Modifier = Modifier,
    state: WeatherForecastUiState,
    onRefresh: () -> Unit,
) {
    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.Spacing16),
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
        ) {
            items(items = state.dailyForecast, key = { it.date }) { day ->
                DailyForecastItem(day = day)
            }
        }
    }
}

@Composable
private fun DailyForecastItem(
    modifier: Modifier = Modifier,
    day: DailyForecastUiModel,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Spacing16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing12),
        ) {

            ForecastItemIcon(iconRes = day.conditionIconRes)

            ForecastItemDateAndCondition(
                modifier = Modifier.weight(1f),
                displayDate = day.displayDate,
                isTomorrow = day.isTomorrow,
                conditionRes = day.conditionDisplayNameRes,
                precipitationProbabilityPercent = day.precipitationProbabilityPercent,
            )

            ForecastItemTemperature(
                minTemperature = day.minTemperatureCelsius.roundToInt(),
                maxTemperature = day.maxTemperatureCelsius.roundToInt(),
            )
        }
    }
}

@Composable
private fun ForecastItemIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
) {
    Image(
        modifier = modifier.size(Dimens.IconSizeLarge),
        painter = painterResource(iconRes),
        contentDescription = null,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
    )
}

@Composable
private fun ForecastItemDateAndCondition(
    modifier: Modifier = Modifier,
    displayDate: String,
    isTomorrow: Boolean,
    @StringRes conditionRes: Int,
    precipitationProbabilityPercent: Int,
) {
    Column(modifier = modifier) {
        if (isTomorrow) {
            ForecastItemTomorrowTag()
        }
        ForecastItemDate(displayDate = displayDate)
        ForecastItemCondition(conditionRes = conditionRes)
        if (precipitationProbabilityPercent > 0) {
            ForecastItemPrecipitation(precipitationProbabilityPercent = precipitationProbabilityPercent)
        }
    }
}

@Composable
private fun ForecastItemTomorrowTag(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.weather_forecast_tomorrow_label),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun ForecastItemDate(
    modifier: Modifier = Modifier,
    displayDate: String,
) {
    Text(
        modifier = modifier,
        text = displayDate,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
private fun ForecastItemCondition(
    modifier: Modifier = Modifier,
    @StringRes conditionRes: Int,
) {
    Text(
        modifier = modifier,
        text = stringResource(conditionRes),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun ForecastItemPrecipitation(
    modifier: Modifier = Modifier,
    precipitationProbabilityPercent: Int,
) {
    Text(
        modifier = modifier,
        text = pluralStringResource(
            R.plurals.weather_forecast_precipitation_probability,
            precipitationProbabilityPercent,
            precipitationProbabilityPercent,
        ),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun ForecastItemTemperature(
    modifier: Modifier = Modifier,
    minTemperature: Int,
    maxTemperature: Int,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing4),
    ) {
        ForecastItemTemperatureValue(
            iconRes = R.drawable.ic_indicator_day,
            iconContentDescriptionRes = R.string.weather_forecast_day_temperature_content_description,
            temperature = maxTemperature,
        )
        ForecastItemTemperatureValue(
            iconRes = R.drawable.ic_indicator_night,
            iconContentDescriptionRes = R.string.weather_forecast_night_temperature_content_description,
            temperature = minTemperature,
        )
    }
}

@Composable
private fun ForecastItemTemperatureValue(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes iconContentDescriptionRes: Int,
    temperature: Int,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing4),
    ) {
        Image(
            modifier = Modifier.size(Dimens.IconSizeSmall),
            painter = painterResource(iconRes),
            contentDescription = stringResource(iconContentDescriptionRes),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
        )
        Text(
            text = stringResource(R.string.weather_forecast_temperature_value_format, temperature),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@PreviewLightDark
@Composable
private fun WeatherForecastTopBarPreview() {
    WeatherActivityPlannerTheme {
        WeatherForecastTopBar(
            title = stringResource(
                R.string.weather_activity_city_country_format,
                WeatherForecastPreviewData.CITY_NAME,
                WeatherForecastPreviewData.CITY_COUNTRY,
            ),
            onNavigateBack = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun DailyForecastItemPreview() {
    WeatherActivityPlannerTheme {
        Surface {
            Column(
                modifier = Modifier.padding(Dimens.Spacing16),
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
            ) {
                WeatherForecastPreviewData.DailyForecasts.forEach { day ->
                    DailyForecastItem(day = day)
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun WeatherForecastContentSuccessPreview() {
    WeatherActivityPlannerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            WeatherForecastContent(
                state = WeatherForecastPreviewData.SuccessState,
                onRetry = {},
                onRefresh = {},
            )
        }
    }
}

@PreviewLight
@Composable
private fun WeatherForecastContentErrorPreview() {
    WeatherActivityPlannerTheme {
        WeatherForecastContent(
            state = WeatherForecastUiState(
                locationName = WeatherForecastPreviewData.CITY_NAME,
                locationCountry = WeatherForecastPreviewData.CITY_COUNTRY,
                error = UiError.NetworkUnavailable,
            ),
            onRetry = {},
            onRefresh = {},
        )
    }
}


@PreviewLight
@Composable
private fun WeatherForecastContentLoadingPreview() {
    WeatherActivityPlannerTheme {
        WeatherForecastContent(
            state = WeatherForecastUiState(isLoading = true),
            onRetry = {},
            onRefresh = {},
        )
    }
}


private object WeatherForecastPreviewData {

    const val CITY_NAME = "London"
    const val CITY_COUNTRY = "United Kingdom"

    val DailyForecasts = listOf(
        DailyForecastUiModel(
            date = "2026-06-12",
            displayDate = "Fri, Jun 12",
            isTomorrow = true,
            conditionDisplayNameRes = R.string.weather_condition_clear,
            conditionIconRes = R.drawable.ic_weather_clear,
            maxTemperatureCelsius = 24.0,
            minTemperatureCelsius = 14.0,
            precipitationProbabilityPercent = 0,
        ),
        DailyForecastUiModel(
            date = "2026-06-13",
            displayDate = "Sat, Jun 13",
            isTomorrow = false,
            conditionDisplayNameRes = R.string.weather_condition_light_rain,
            conditionIconRes = R.drawable.ic_weather_rain,
            maxTemperatureCelsius = 21.0,
            minTemperatureCelsius = 12.0,
            precipitationProbabilityPercent = 60,
        ),
        DailyForecastUiModel(
            date = "2026-06-14",
            displayDate = "Sun, Jun 14",
            isTomorrow = false,
            conditionDisplayNameRes = R.string.weather_condition_partly_cloudy,
            conditionIconRes = R.drawable.ic_weather_partly_cloudy,
            maxTemperatureCelsius = 19.0,
            minTemperatureCelsius = 10.0,
            precipitationProbabilityPercent = 20,
        ),
    )

    val SuccessState = WeatherForecastUiState(
        locationName = CITY_NAME,
        locationCountry = CITY_COUNTRY,
        dailyForecast = DailyForecasts,
    )
}
