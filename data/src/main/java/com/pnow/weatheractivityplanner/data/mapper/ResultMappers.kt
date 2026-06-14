package com.pnow.weatheractivityplanner.data.mapper

import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import java.io.IOException
import retrofit2.HttpException

private const val DEFAULT_HTTP_ERROR_MESSAGE = "HTTP error"

internal fun <T> Result<T>.toDomainResult(): Result<T> =
    fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(it.toDomainError()) },
    )

internal fun Throwable.toDomainError(): DomainError = when (this) {
    is JsonDataException, is JsonEncodingException -> DomainError.DeserializationError(this)
    is IOException -> DomainError.NetworkUnavailable()
    is HttpException -> DomainError.HttpError(code(), message() ?: DEFAULT_HTTP_ERROR_MESSAGE)
    else -> DomainError.Unknown(this)
}
