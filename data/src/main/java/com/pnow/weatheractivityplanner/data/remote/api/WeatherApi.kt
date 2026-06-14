package com.pnow.weatheractivityplanner.data.remote.api

import com.pnow.weatheractivityplanner.data.remote.dto.forecast.ForecastResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

internal interface WeatherApi {

    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = CURRENT_FIELDS,
        @Query("daily") daily: String = DAILY_FIELDS,
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("timezone") timezone: String = TIMEZONE_AUTO,
        @Query("wind_speed_unit") windSpeedUnit: String = WIND_SPEED_UNIT_KMH,
    ): ForecastResponseDto

    companion object {

        private const val TIMEZONE_AUTO = "auto"
        private const val WIND_SPEED_UNIT_KMH = "kmh"

        private const val FIELD_SEPARATOR = ","
        private const val FIELD_WEATHER_CODE = "weather_code"
        private const val FIELD_TEMPERATURE_2M = "temperature_2m"
        private const val FIELD_TEMPERATURE_2M_MAX = "temperature_2m_max"
        private const val FIELD_TEMPERATURE_2M_MIN = "temperature_2m_min"
        private const val FIELD_APPARENT_TEMPERATURE = "apparent_temperature"
        private const val FIELD_RELATIVE_HUMIDITY_2M = "relative_humidity_2m"
        private const val FIELD_PRECIPITATION = "precipitation"
        private const val FIELD_PRECIPITATION_SUM = "precipitation_sum"
        private const val FIELD_PRECIPITATION_PROBABILITY_MAX = "precipitation_probability_max"
        private const val FIELD_WIND_SPEED_10M = "wind_speed_10m"
        private const val FIELD_WIND_SPEED_10M_MAX = "wind_speed_10m_max"
        private const val FIELD_WIND_GUSTS_10M_MAX = "wind_gusts_10m_max"
        private const val FIELD_SNOWFALL_SUM = "snowfall_sum"
        private const val FIELD_UV_INDEX_MAX = "uv_index_max"
        private const val FIELD_DAYLIGHT_DURATION = "daylight_duration"
        private const val FIELD_IS_DAY = "is_day"

        private val CURRENT_FIELDS = listOf(
            FIELD_WEATHER_CODE,
            FIELD_TEMPERATURE_2M,
            FIELD_RELATIVE_HUMIDITY_2M,
            FIELD_APPARENT_TEMPERATURE,
            FIELD_PRECIPITATION,
            FIELD_WIND_SPEED_10M,
            FIELD_IS_DAY,
        ).joinToString(FIELD_SEPARATOR)

        private val DAILY_FIELDS = listOf(
            FIELD_WEATHER_CODE,
            FIELD_TEMPERATURE_2M_MAX,
            FIELD_TEMPERATURE_2M_MIN,
            FIELD_PRECIPITATION_SUM,
            FIELD_PRECIPITATION_PROBABILITY_MAX,
            FIELD_SNOWFALL_SUM,
            FIELD_WIND_SPEED_10M_MAX,
            FIELD_WIND_GUSTS_10M_MAX,
            FIELD_UV_INDEX_MAX,
            FIELD_DAYLIGHT_DURATION,
        ).joinToString(FIELD_SEPARATOR)
    }
}
