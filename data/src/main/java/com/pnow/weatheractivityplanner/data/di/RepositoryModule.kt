package com.pnow.weatheractivityplanner.data.di

import com.pnow.weatheractivityplanner.data.repository.ConnectivityRepositoryImpl
import com.pnow.weatheractivityplanner.data.repository.CurrentLocationRepositoryImpl
import com.pnow.weatheractivityplanner.data.repository.GeocodingRepositoryImpl
import com.pnow.weatheractivityplanner.data.repository.WeatherRepositoryImpl
import com.pnow.weatheractivityplanner.domain.repository.ConnectivityRepository
import com.pnow.weatheractivityplanner.domain.repository.CurrentLocationRepository
import com.pnow.weatheractivityplanner.domain.repository.GeocodingRepository
import com.pnow.weatheractivityplanner.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    internal abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    internal abstract fun bindGeocodingRepository(impl: GeocodingRepositoryImpl): GeocodingRepository

    @Binds
    @Singleton
    internal abstract fun bindConnectivityRepository(impl: ConnectivityRepositoryImpl): ConnectivityRepository

    @Binds
    @Singleton
    internal abstract fun bindCurrentLocationRepository(impl: CurrentLocationRepositoryImpl): CurrentLocationRepository
}
