package com.pnow.weatheractivityplanner.feature.forecast

import android.content.res.Configuration
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.feature.common.UiError
import com.pnow.weatheractivityplanner.feature.common.toMessage
import com.pnow.weatheractivityplanner.feature.common.view.FullScreenError
import com.pnow.weatheractivityplanner.feature.common.view.FullScreenLoading
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
    ) { innerPadding ->
        WeatherForecastContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onRetry = viewModel::onRetry,
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
        )
    }
}

@Composable
private fun WeatherForecastContent(
    modifier: Modifier = Modifier,
    state: WeatherForecastUiState,
    onRetry: () -> Unit,
) {
    when {
        state.isLoading -> FullScreenLoading(modifier = modifier)

        state.error != null ->
            FullScreenError(
                modifier = modifier,
                message = state.error.toMessage(),
                onRetry = onRetry,
            )

        else -> LazyColumn(
            modifier = modifier
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
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

            ForecastItemDateAndCondition(
                modifier = Modifier.weight(1f),
                date = day.date,
                conditionRes = day.conditionDisplayNameRes,
            )

            ForecastItemTemperature(
                minTemperature = day.minTemperatureCelsius.roundToInt(),
                maxTemperature = day.maxTemperatureCelsius.roundToInt(),
            )
        }
    }
}

@Composable
private fun ForecastItemDateAndCondition(
    modifier: Modifier,
    date: String,
    @StringRes conditionRes: Int,
) {
    Column(modifier = modifier) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(conditionRes),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ForecastItemTemperature(
    minTemperature: Int,
    maxTemperature: Int,
) {
    Text(
        text = stringResource(
            R.string.weather_forecast_temperature_range_format,
            maxTemperature,
            minTemperature,
        ),
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
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

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
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

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WeatherForecastContentSuccessPreview() {
    WeatherActivityPlannerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            WeatherForecastContent(
                state = WeatherForecastPreviewData.SuccessState,
                onRetry = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherForecastContentErrorPreview() {
    WeatherActivityPlannerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            WeatherForecastContent(
                state = WeatherForecastUiState(
                    locationName = WeatherForecastPreviewData.CITY_NAME,
                    locationCountry = WeatherForecastPreviewData.CITY_COUNTRY,
                    error = UiError.NetworkUnavailable,
                ),
                onRetry = {},
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun WeatherForecastContentLoadingPreview() {
    WeatherActivityPlannerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            WeatherForecastContent(
                state = WeatherForecastUiState(isLoading = true),
                onRetry = {},
            )
        }
    }
}


private object WeatherForecastPreviewData {

    const val CITY_NAME = "London"
    const val CITY_COUNTRY = "United Kingdom"

    val DailyForecasts = listOf(
        DailyForecastUiModel(
            date = "2026-06-12",
            conditionDisplayNameRes = R.string.weather_condition_clear,
            maxTemperatureCelsius = 24.0,
            minTemperatureCelsius = 14.0,
        ),
        DailyForecastUiModel(
            date = "2026-06-13",
            conditionDisplayNameRes = R.string.weather_condition_light_rain,
            maxTemperatureCelsius = 21.0,
            minTemperatureCelsius = 12.0,
        ),
        DailyForecastUiModel(
            date = "2026-06-14",
            conditionDisplayNameRes = R.string.weather_condition_partly_cloudy,
            maxTemperatureCelsius = 19.0,
            minTemperatureCelsius = 10.0,
        ),
    )

    val SuccessState = WeatherForecastUiState(
        locationName = CITY_NAME,
        locationCountry = CITY_COUNTRY,
        dailyForecast = DailyForecasts,
    )
}
