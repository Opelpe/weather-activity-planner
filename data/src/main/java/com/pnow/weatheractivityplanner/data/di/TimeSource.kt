package com.pnow.weatheractivityplanner.data.di

import javax.inject.Inject

internal fun interface TimeSource {

    fun nowMs(): Long
}

internal class TimeSourceImpl @Inject constructor() : TimeSource {

    override fun nowMs(): Long = System.currentTimeMillis()
}
