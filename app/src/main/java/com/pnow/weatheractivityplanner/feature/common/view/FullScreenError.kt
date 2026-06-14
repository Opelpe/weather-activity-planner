package com.pnow.weatheractivityplanner.feature.common.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.ui.theme.WeatherActivityPlannerTheme
import com.pnow.weatheractivityplanner.util.Dimens

@Composable
fun FullScreenError(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.Spacing16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ErrorDescription(
            message = message,
        )
        Spacer(modifier = Modifier.height(Dimens.Spacing12))
        ErrorRetryButton(
            onClick = onRetry,
        )

    }
}

@Composable
private fun ErrorDescription(
    modifier: Modifier = Modifier,
    message: String,
) {
    Text(
        modifier = modifier,
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun ErrorRetryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text(text = stringResource(R.string.weather_activity_retry))
    }
}


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FullScreenErrorPreview() {
    WeatherActivityPlannerTheme {
        Surface {
            FullScreenError(
                message = CommonContentPreviewData.ERROR_MESSAGE,
                onRetry = {},
            )
        }
    }
}

private object CommonContentPreviewData {

    const val ERROR_MESSAGE = "No internet connection"
}
