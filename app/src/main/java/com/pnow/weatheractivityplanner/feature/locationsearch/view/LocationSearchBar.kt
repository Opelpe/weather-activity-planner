package com.pnow.weatheractivityplanner.feature.locationsearch.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
    onQueryChange: (String) -> Unit,
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
            onQueryChange = {},
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
            onQueryChange = {},
        )
    }
}
