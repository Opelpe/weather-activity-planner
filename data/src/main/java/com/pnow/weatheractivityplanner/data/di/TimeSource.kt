package com.pnow.weatheractivityplanner.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeModule {

    @Binds
    @Singleton
    internal abstract fun bindTimeSource(impl: TimeSourceImpl): TimeSource
}

internal fun interface TimeSource {

    fun nowMs(): Long
}

internal class TimeSourceImpl @Inject constructor() : TimeSource {

    override fun nowMs(): Long = System.currentTimeMillis()
}
