package com.pnow.weatheractivityplanner.feature.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.domain.error.DomainError

sealed interface UiError {
    data object NetworkUnavailable : UiError
    data class HttpError(val code: Int) : UiError
    data object DeserializationError : UiError
    data object Unknown : UiError
    data object InvalidNavigationArguments : UiError
}

fun Throwable.toUiError(): UiError = when (this) {
    is DomainError.NetworkUnavailable -> UiError.NetworkUnavailable
    is DomainError.HttpError -> UiError.HttpError(code = code)
    is DomainError.DeserializationError -> UiError.DeserializationError
    is DomainError.Unknown -> UiError.Unknown
    else -> UiError.Unknown
}

@Composable
fun UiError.toMessage(): String = when (this) {
    UiError.NetworkUnavailable -> stringResource(R.string.common_error_network_unavailable)
    is UiError.HttpError -> stringResource(R.string.common_error_http_format, code)
    UiError.DeserializationError -> stringResource(R.string.common_error_deserialization)
    UiError.Unknown -> stringResource(R.string.common_error_unknown)
    UiError.InvalidNavigationArguments -> stringResource(R.string.common_error_invalid_navigation)
}
