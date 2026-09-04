package com.pnow.weatheractivityplanner.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

internal class ApiKeyInterceptor(
    private val queryParamName: String,
    private val apiKey: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val urlWithApiKey = originalRequest.url.newBuilder()
            .addQueryParameter(queryParamName, apiKey)
            .build()
        val requestWithApiKey = originalRequest.newBuilder()
            .url(urlWithApiKey)
            .build()
        return chain.proceed(requestWithApiKey)
    }
}
