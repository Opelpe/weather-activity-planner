package com.pnow.weatheractivityplanner.data.di

import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.pnow.weatheractivityplanner.data.location.LocationAvailabilityCheckerImpl
import com.pnow.weatheractivityplanner.data.location.LocationAvailabilityChecker
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    internal abstract fun bindLocationAvailabilityChecker(
        impl: LocationAvailabilityCheckerImpl,
    ): LocationAvailabilityChecker

    companion object {

        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        @Provides
        @Singleton
        fun provideLocationManager(@ApplicationContext context: Context): LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
}
