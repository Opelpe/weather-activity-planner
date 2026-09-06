package com.pnow.weatheractivityplanner.feature.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.domain.error.DomainError

sealed interface UiError {
    data object NetworkUnavailable : UiError
    data class HttpError(val code: Int) : UiError
    data object DeserializationError : UiError
    data object Unknown : UiError
    data object InvalidNavigationArguments : UiError
    data object LocationPermissionDenied : UiError
    data object LocationDisabled : UiError
    data object LocationUnavailable : UiError
}

fun Throwable.toUiError(): UiError = when (this) {
    is DomainError.NetworkUnavailable -> UiError.NetworkUnavailable
    is DomainError.HttpError -> UiError.HttpError(code = code)
    is DomainError.DeserializationError -> UiError.DeserializationError
    is DomainError.LocationPermissionDenied -> UiError.LocationPermissionDenied
    is DomainError.LocationDisabled -> UiError.LocationDisabled
    is DomainError.LocationUnavailable -> UiError.LocationUnavailable
    is DomainError.Unknown -> UiError.Unknown
    else -> UiError.Unknown
}

fun UiError.toMessage(context: Context): String = when (this) {
    UiError.NetworkUnavailable -> context.getString(R.string.common_error_network_unavailable)
    is UiError.HttpError -> context.getString(R.string.common_error_http_format, code)
    UiError.DeserializationError -> context.getString(R.string.common_error_deserialization)
    UiError.Unknown -> context.getString(R.string.common_error_unknown)
    UiError.InvalidNavigationArguments -> context.getString(R.string.common_error_invalid_navigation)
    UiError.LocationPermissionDenied -> context.getString(R.string.common_error_location_permission_denied)
    UiError.LocationDisabled -> context.getString(R.string.common_error_location_disabled)
    UiError.LocationUnavailable -> context.getString(R.string.common_error_location_unavailable)
}

@Composable
fun UiError.toMessage(): String = toMessage(LocalContext.current)
