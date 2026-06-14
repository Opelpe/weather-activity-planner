package com.pnow.weatheractivityplanner.feature.locationsearch.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.feature.locationsearch.LocationSearchPreviewData
import com.pnow.weatheractivityplanner.ui.theme.WeatherActivityPlannerTheme
import com.pnow.weatheractivityplanner.util.Dimens

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

    LaunchedEffect(query) {
        if (query != textFieldValue.text) {
            textFieldValue = TextFieldValue(text = query, selection = TextRange(query.length))
        }
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
            onQueryChange(newValue.text)
        },
        modifier = modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.weather_activity_search_label)) },
        singleLine = true,
        trailingIcon = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens.LoadingIndicatorSize),
                    strokeWidth = Dimens.LoadingIndicatorStrokeWidth,
                )
            }
        },
    )
}

@Preview(showBackground = true)
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

@Preview(showBackground = true)
@Composable
private fun LocationSearchBarLoadingPreview() {
    WeatherActivityPlannerTheme {
        LocationSearchBar(
            query = LocationSearchPreviewData.SEARCH_QUERY,
            isLoading = true,
            onQueryChange = {},
        )
    }
}
