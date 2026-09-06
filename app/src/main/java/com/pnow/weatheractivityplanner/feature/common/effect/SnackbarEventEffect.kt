package com.pnow.weatheractivityplanner.feature.common.effect

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> ObserveSnackbarActions(
    events: Flow<T>,
    snackbarHostState: SnackbarHostState,
    message: (T) -> String,
    actionLabel: String? = null,
    duration: SnackbarDuration = SnackbarDuration.Short,
    onActionPerformed: () -> Unit = {},
) {
    LaunchedEffect(events, snackbarHostState) {
        events.collect { event ->
            val result = snackbarHostState.showSnackbar(
                message = message(event),
                actionLabel = actionLabel,
                duration = duration,
            )
            if (result == SnackbarResult.ActionPerformed) {
                onActionPerformed()
            }
        }
    }
}
