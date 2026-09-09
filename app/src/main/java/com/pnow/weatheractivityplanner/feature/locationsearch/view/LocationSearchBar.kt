package com.pnow.weatheractivityplanner.feature.locationsearch.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.feature.locationsearch.LocationSearchPreviewData
import com.pnow.weatheractivityplanner.ui.theme.PreviewLightDark
import com.pnow.weatheractivityplanner.ui.theme.WeatherActivityPlannerTheme
import com.pnow.weatheractivityplanner.util.Dimens
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

private const val LOADING_INDICATOR_DELAY_MS = 500L

@Composable
fun LocationSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    isLoading: Boolean,
    isResolvingCurrentLocation: Boolean,
    onQueryChange: (String) -> Unit,
    onUseCurrentLocationClick: () -> Unit,
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = query, selection = TextRange(query.length)))
    }
    val showLoadingIndicator = rememberDelayedLoadingIndicator(isLoading)

    LaunchedEffect(query) {
        if (query != textFieldValue.text) {
            textFieldValue = TextFieldValue(text = query, selection = TextRange(query.length))
        }
    }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
            onQueryChange(newValue.text)
        },
        label = { Text(stringResource(R.string.weather_activity_search_label)) },
        singleLine = true,
        leadingIcon = {
            UseCurrentLocationIcon(
                isActive = isResolvingCurrentLocation,
                onClick = onUseCurrentLocationClick,
            )
        },
        trailingIcon = {
            SearchTrailingIcon(
                isLoading = showLoadingIndicator,
                showClear = textFieldValue.text.isNotEmpty(),
                onClear = {
                    textFieldValue = TextFieldValue()
                    onQueryChange("")
                },
            )
        },
    )
}

@Composable
private fun UseCurrentLocationIcon(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val tint = if (isActive || isPressed) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    val contentDescription = if (isActive) {
        stringResource(R.string.weather_activity_cancel_current_location_content_description)
    } else {
        stringResource(R.string.weather_activity_use_current_location_content_description)
    }

    Image(
        modifier = modifier
            .size(Dimens.IconSize)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false),
                onClick = onClick,
            ),
        colorFilter = ColorFilter.tint(tint),
        painter = painterResource(R.drawable.ic_my_location),
        contentDescription = contentDescription,
    )
}

@PreviewLightDark
@Composable
private fun UseCurrentLocationIconIdlePreview() {
    WeatherActivityPlannerTheme {
        UseCurrentLocationIcon(isActive = false, onClick = {})
    }
}

@PreviewLightDark
@Composable
private fun UseCurrentLocationIconActivePreview() {
    WeatherActivityPlannerTheme {
        UseCurrentLocationIcon(isActive = true, onClick = {})
    }
}

@Composable
private fun rememberDelayedLoadingIndicator(isLoading: Boolean): Boolean {
    var showLoadingIndicator by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(LOADING_INDICATOR_DELAY_MS.milliseconds)
            showLoadingIndicator = true
        } else {
            showLoadingIndicator = false
        }
    }

    return showLoadingIndicator
}

@Composable
private fun SearchTrailingIcon(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    showClear: Boolean,
    onClear: () -> Unit,
) {
    Row(
        modifier = modifier.padding(horizontal = Dimens.Spacing8),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimens.LoadingIndicatorSize),
                strokeWidth = Dimens.LoadingIndicatorStrokeWidth,
            )
        }
        if (showClear) {
            ClearSearchIcon(onClick = onClear)
        }
    }
}

@Composable
private fun ClearSearchIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Image(
        modifier = modifier
            .size(Dimens.IconSize)
            .clickable(onClick = onClick),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
        painter = painterResource(R.drawable.ic_clear),
        contentDescription = stringResource(R.string.weather_activity_search_clear_content_description),
    )
}

@PreviewLightDark
@Composable
private fun LocationSearchBarDefaultPreview() {
    WeatherActivityPlannerTheme {
        LocationSearchBar(
            query = "",
            isLoading = false,
            isResolvingCurrentLocation = false,
            onQueryChange = {},
            onUseCurrentLocationClick = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun LocationSearchBarWithQueryPreview() {
    WeatherActivityPlannerTheme {
        LocationSearchBar(
            query = LocationSearchPreviewData.SEARCH_QUERY,
            isLoading = false,
            isResolvingCurrentLocation = false,
            onQueryChange = {},
            onUseCurrentLocationClick = {},
        )
    }
}
