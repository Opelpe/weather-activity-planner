package com.pnow.weatheractivityplanner.data.mapper

import com.pnow.weatheractivityplanner.domain.error.DomainError
import java.io.IOException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

private object ResultFixture {

    const val SUCCESS_VALUE = "data"
    const val NETWORK_ERROR_MESSAGE = "no network"
    const val HTTP_ERROR_CODE = 404
    const val HTTP_ERROR_BODY = "Not Found"
    const val UNKNOWN_ERROR_MESSAGE = "boom"
}

class ResultMappersTest {

    @Test
    fun `given success result, when toDomainResult, then value is preserved`() {
        val result = Result.success(ResultFixture.SUCCESS_VALUE)

        val mapped = result.toDomainResult()

        assertEquals(ResultFixture.SUCCESS_VALUE, mapped.getOrNull())
    }

    @Test
    fun `given IOException, when toDomainResult, then failure is NetworkUnavailable`() {
        val result = Result.failure<String>(IOException(ResultFixture.NETWORK_ERROR_MESSAGE))

        val mapped = result.toDomainResult()

        assertTrue(mapped.exceptionOrNull() is DomainError.NetworkUnavailable)
    }

    @Test
    fun `given HttpException, when toDomainResult, then failure is HttpError with code`() {
        val httpException = HttpException(
            Response.error<String>(
                ResultFixture.HTTP_ERROR_CODE,
                ResultFixture.HTTP_ERROR_BODY.toResponseBody(),
            ),
        )
        val result = Result.failure<String>(httpException)

        val mapped = result.toDomainResult()

        val error = mapped.exceptionOrNull() as DomainError.HttpError
        assertEquals(ResultFixture.HTTP_ERROR_CODE, error.code)
    }

    @Test
    fun `given unknown exception, when toDomainResult, then failure is Unknown with cause`() {
        val cause = IllegalStateException(ResultFixture.UNKNOWN_ERROR_MESSAGE)
        val result = Result.failure<String>(cause)

        val mapped = result.toDomainResult()

        val error = mapped.exceptionOrNull() as DomainError.Unknown
        assertTrue(error.cause === cause)
    }
}
