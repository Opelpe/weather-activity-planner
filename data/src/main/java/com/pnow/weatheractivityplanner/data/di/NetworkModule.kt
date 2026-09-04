package com.pnow.weatheractivityplanner.data.di

import com.pnow.data.BuildConfig
import com.pnow.weatheractivityplanner.data.remote.api.GeocodingApi
import com.pnow.weatheractivityplanner.data.remote.api.WeatherApi
import com.pnow.weatheractivityplanner.data.remote.interceptor.ApiKeyInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class Weather

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class Geocoding

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val WEATHER_BASE_URL = "https://api.open-meteo.com/"
    private const val GEOCODING_BASE_URL = "https://us1.locationiq.com/"
    private const val GEOCODING_API_KEY_QUERY_PARAM = "key"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    @Weather
    fun provideWeatherOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Provides
    @Singleton
    @Geocoding
    fun provideGeocodingOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                ApiKeyInterceptor(
                    queryParamName = GEOCODING_API_KEY_QUERY_PARAM,
                    apiKey = BuildConfig.LOCATIONIQ_API_KEY,
                ),
            )
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Provides
    @Singleton
    @Weather
    fun provideWeatherRetrofit(
        @Weather okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    @Geocoding
    fun provideGeocodingRetrofit(
        @Geocoding okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(GEOCODING_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    internal fun provideWeatherApi(@Weather retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)

    @Provides
    @Singleton
    internal fun provideGeocodingApi(@Geocoding retrofit: Retrofit): GeocodingApi =
        retrofit.create(GeocodingApi::class.java)
}
