package com.pnow.weatheractivityplanner.data.di

import android.content.Context
import android.content.pm.PackageManager
import dagger.Module

import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class ApplicationPackageName

@Module
@InstallIn(SingletonComponent::class)
object PackageInfoModule {

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager = context.packageManager

    @Provides
    @ApplicationPackageName
    fun provideApplicationPackageName(@ApplicationContext context: Context): String = context.packageName
}
