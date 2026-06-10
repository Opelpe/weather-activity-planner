package com.pnow.weatheractivityplanner.domain.error

sealed class DomainError : Exception() {
    class NetworkUnavailable : DomainError()

    data class HttpError(
        val code: Int,
        override val message: String,
    ) : DomainError()

    data class DeserializationError(override val cause: Throwable) : DomainError()
    data class Unknown(override val cause: Throwable) : DomainError()
}
