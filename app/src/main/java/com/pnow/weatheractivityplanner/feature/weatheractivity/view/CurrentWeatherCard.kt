package com.pnow.weatheractivityplanner.feature.weatheractivity.view

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.feature.weatheractivity.WeatherRecommendationPreviewData
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.CurrentWeatherUiModel
import com.pnow.weatheractivityplanner.ui.theme.WeatherActivityPlannerTheme
import com.pnow.weatheractivityplanner.util.Dimens
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherCard(
    modifier: Modifier = Modifier,
    locationName: String,
    locationCountry: String,
    currentWeather: CurrentWeatherUiModel,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = stringResource(
                    R.string.weather_activity_view_forecast_content_description,
                    locationName,
                ),
                onClick = onClick,
            ),
    ) {
        Column(modifier = Modifier.padding(Dimens.Spacing16)) {

            CurrentWeatherSummary(
                cityName = locationName,
                cityCountry = locationCountry,
                currentWeather = currentWeather,
            )

            CurrentWeatherDetails(
                modifier = Modifier.padding(top = 2.dp),
                currentWeather = currentWeather,
            )

            ViewForecastHint(
                modifier = Modifier.padding(top = Dimens.Spacing8),
            )
        }
    }
}

@Composable
private fun CurrentWeatherSummary(
    modifier: Modifier = Modifier,
    cityName: String,
    cityCountry: String,
    currentWeather: CurrentWeatherUiModel,
) {
    Column(modifier = modifier) {

        WeatherSummaryLocationTitle(
            cityName = cityName,
            cityCountry = cityCountry,
        )
        WeatherSummaryTemperature(currentWeather.temperatureCelsius)
        WeatherSummaryCondition(currentWeather.conditionDisplayNameRes)
    }
}

@Composable
private fun WeatherSummaryCondition(@StringRes conditionRes: Int) {
    Text(
        text = stringResource(conditionRes),
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
private fun WeatherSummaryTemperature(currentTemperature: Double) {
    Text(
        text = stringResource(
            R.string.weather_activity_temperature_format,
            currentTemperature,
        ),
        style = MaterialTheme.typography.displaySmall,
    )
}

@Composable
private fun WeatherSummaryLocationTitle(
    modifier: Modifier = Modifier,
    cityName: String,
    cityCountry: String,
) {
    Text(
        modifier = modifier,
        text = stringResource(
            R.string.weather_activity_city_country_format,
            cityName,
            cityCountry,
        ),
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
private fun CurrentWeatherDetails(
    modifier: Modifier = Modifier,
    currentWeather: CurrentWeatherUiModel,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
    ) {
        WeatherDetailsScore(
            modifier = Modifier.weight(1f),
            titleId = R.string.weather_activity_feels_like_format,
            value = currentWeather.apparentTemperatureCelsius.roundToInt(),
        )

        WeatherDetailsScore(
            modifier = Modifier.weight(1f),
            titleId = R.string.weather_activity_humidity_format,
            value = currentWeather.humidityPercent,
        )

        WeatherDetailsScore(
            modifier = Modifier.weight(1f),
            titleId = R.string.weather_activity_wind_format,
            value = currentWeather.windSpeedKph.roundToInt(),
        )
    }
}

@Composable
private fun WeatherDetailsScore(
    modifier: Modifier = Modifier,
    @StringRes titleId: Int,
    value: Int,
) {
    Text(
        modifier = modifier,
        text = stringResource(
            titleId,
            value,
        ),
        style = MaterialTheme.typography.bodyMedium,
    )
}


@Composable
private fun ViewForecastHint(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.weather_activity_view_forecast_hint),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
    )
}


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CurrentWeatherCardPreview() {
    WeatherActivityPlannerTheme {
        Surface(modifier = Modifier.padding(Dimens.Spacing16)) {
            CurrentWeatherCard(
                locationName = WeatherRecommendationPreviewData.LOCATION_NAME,
                locationCountry = WeatherRecommendationPreviewData.LOCATION_COUNTRY,
                currentWeather = WeatherRecommendationPreviewData.CurrentWeather,
                onClick = {},
            )
        }
    }
}
