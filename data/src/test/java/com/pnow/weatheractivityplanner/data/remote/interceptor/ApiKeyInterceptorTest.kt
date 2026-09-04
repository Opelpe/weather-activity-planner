package com.pnow.weatheractivityplanner.data.remote.interceptor

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Test

private object ApiKeyInterceptorFixture {

    const val API_KEY = "test-api-key"
    const val ORIGINAL_URL = "https://us1.locationiq.com/v1/search?q=London"
    const val QUERY_PARAM_NAME = "key"
}

class ApiKeyInterceptorTest {

    private val interceptor = ApiKeyInterceptor(
        queryParamName = ApiKeyInterceptorFixture.QUERY_PARAM_NAME,
        apiKey = ApiKeyInterceptorFixture.API_KEY,
    )

    @Test
    fun `given request without api key, when intercept, then api key query param is appended`() {
        val originalRequest = Request.Builder()
            .url(ApiKeyInterceptorFixture.ORIGINAL_URL)
            .build()
        val requestSlot = slot<Request>()
        val chain = mockk<Interceptor.Chain>()
        val fakeResponse = Response.Builder()
            .request(originalRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()
        every { chain.request() } returns originalRequest
        every { chain.proceed(capture(requestSlot)) } returns fakeResponse

        interceptor.intercept(chain)

        assertEquals(
            ApiKeyInterceptorFixture.API_KEY,
            requestSlot.captured.url.queryParameter(ApiKeyInterceptorFixture.QUERY_PARAM_NAME),
        )
    }

    @Test
    fun `given request, when intercept, then original query params are preserved`() {
        val originalRequest = Request.Builder()
            .url(ApiKeyInterceptorFixture.ORIGINAL_URL)
            .build()
        val requestSlot = slot<Request>()
        val chain = mockk<Interceptor.Chain>()
        val fakeResponse = Response.Builder()
            .request(originalRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()
        every { chain.request() } returns originalRequest
        every { chain.proceed(capture(requestSlot)) } returns fakeResponse

        interceptor.intercept(chain)

        assertEquals("London", requestSlot.captured.url.queryParameter("q"))
    }
}
