package com.pnow.weatheractivityplanner.feature.common.view

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.pnow.weatheractivityplanner.R
import kotlinx.coroutines.flow.Flow

@Composable
fun ObserveCachedDataNotice(
    notices: Flow<Unit>,
    snackbarHostState: SnackbarHostState,
    onRetry: () -> Unit,
) {
    val message = stringResource(R.string.common_cached_data_notice_message)
    val actionLabel = stringResource(R.string.weather_activity_retry)

    LaunchedEffect(notices, snackbarHostState) {
        notices.collect {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = SnackbarDuration.Long,
            )
            if (result == SnackbarResult.ActionPerformed) {
                onRetry()
            }
        }
    }
}
