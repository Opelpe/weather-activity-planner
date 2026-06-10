package com.pnow.weatheractivityplanner.data.di

import com.pnow.weatheractivityplanner.data.repository.GeocodingRepositoryImpl
import com.pnow.weatheractivityplanner.data.repository.WeatherRepositoryImpl
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
}
