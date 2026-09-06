package com.pnow.weatheractivityplanner.feature.common.effect

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pnow.weatheractivityplanner.R
import kotlinx.coroutines.flow.Flow

@Composable
fun ObserveCachedDataNotice(
    notices: Flow<Unit>,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit,
) {
    val message = stringResource(R.string.common_cached_data_notice_message)
    val actionLabel = stringResource(R.string.weather_activity_retry)

    ObserveSnackbarActions(
        events = notices,
        snackbarHostState = snackbarHostState,
        message = { message },
        actionLabel = actionLabel,
        duration = SnackbarDuration.Long,
        onActionPerformed = onRefresh,
    )
}
