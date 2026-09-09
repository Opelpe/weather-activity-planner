package com.pnow.weatheractivityplanner.feature.weatheractivity.view

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.feature.weatheractivity.WeatherRecommendationPreviewData
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.CurrentWeatherUiModel
import com.pnow.weatheractivityplanner.ui.theme.PreviewLightDark
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

            CurrentWeatherLocationTitle(
                cityName = locationName,
                cityCountry = locationCountry,
            )

            CurrentWeatherSummary(currentWeather = currentWeather)

            CurrentWeatherDetails(
                modifier = Modifier.padding(top = Dimens.Spacing2),
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
    currentWeather: CurrentWeatherUiModel,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing32),
    ) {
        Column {
            WeatherSummaryTemperature(currentWeather.temperatureCelsius)
            WeatherSummaryCondition(currentWeather.conditionDisplayNameRes)
        }
        WeatherSummaryIcon(iconRes = currentWeather.conditionIconRes)
    }
}

@Composable
private fun WeatherSummaryIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
) {
    Image(
        modifier = modifier.size(Dimens.IconSizeXLarge),
        painter = painterResource(iconRes),
        contentDescription = null,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
    )
}

@Composable
private fun WeatherSummaryCondition(@StringRes conditionRes: Int) {
    Text(
        text = stringResource(conditionRes),
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
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
private fun CurrentWeatherLocationTitle(
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
            labelRes = R.string.weather_activity_feels_like_label,
            valueFormatRes = R.string.weather_activity_feels_like_value_format,
            value = currentWeather.apparentTemperatureCelsius.roundToInt(),
        )

        WeatherDetailsScore(
            modifier = Modifier.weight(1f),
            labelRes = R.string.weather_activity_humidity_label,
            valueFormatRes = R.string.weather_activity_humidity_value_format,
            value = currentWeather.humidityPercent,
        )

        WeatherDetailsScore(
            modifier = Modifier.weight(1f),
            labelRes = R.string.weather_activity_wind_label,
            valueFormatRes = R.string.weather_activity_wind_value_format,
            value = currentWeather.windSpeedKph.roundToInt(),
        )
    }
}

@Composable
private fun WeatherDetailsScore(
    modifier: Modifier = Modifier,
    @StringRes labelRes: Int,
    @StringRes valueFormatRes: Int,
    value: Int,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing4),
    ) {
        Text(
            modifier = Modifier.weight(1f, fill = false),
            text = stringResource(labelRes),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(valueFormatRes, value),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
        )
    }
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


@PreviewLightDark
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
