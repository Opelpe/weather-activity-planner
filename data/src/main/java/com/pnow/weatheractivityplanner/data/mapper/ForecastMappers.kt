package com.pnow.weatheractivityplanner.data.mapper

import com.pnow.weatheractivityplanner.data.remote.dto.forecast.CurrentWeatherDto
import com.pnow.weatheractivityplanner.data.remote.dto.forecast.DailyDataDto
import com.pnow.weatheractivityplanner.data.remote.dto.forecast.ForecastResponseDto
import com.pnow.weatheractivityplanner.domain.model.CurrentWeather
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.Forecast

private const val MISSING_CURRENT_WEATHER_MESSAGE = "Forecast response is missing current weather data"
private const val SECONDS_PER_HOUR = 3600.0

internal fun ForecastResponseDto.toDomain(): Forecast = Forecast(
    latitude = latitude,
    longitude = longitude,
    timezone = timezone,
    current = requireNotNull(current) { MISSING_CURRENT_WEATHER_MESSAGE }.toDomain(),
    daily = daily.toDomainList(),
)

internal fun CurrentWeatherDto.toDomain(): CurrentWeather = CurrentWeather(
    temperatureCelsius = temperatureCelsius,
    apparentTemperatureCelsius = apparentTemperatureCelsius,
    relativeHumidityPercent = relativeHumidityPercent,
    precipitationMm = precipitation,
    windSpeedKph = windSpeedKph,
    condition = weatherCode.toWeatherCondition(),
    isDay = isDay == 1,
)

internal fun DailyDataDto.toDomainList(): List<DailyForecast> =
    time.indices.map { i ->
        DailyForecast(
            date = time[i],
            maxTemperatureCelsius = maxTemperatureCelsius[i],
            minTemperatureCelsius = minTemperatureCelsius[i],
            precipitationSumMm = precipitationSumMm[i],
            precipitationProbabilityMaxPercent = precipitationProbabilityMaxPercent[i],
            snowfallSumCm = snowfallSumCm[i],
            windSpeedMaxKph = windSpeedMaxKph[i],
            windGustsMaxKph = windGustsMaxKph[i],
            uvIndexMax = uvIndexMax[i],
            daylightDurationHours = daylightDurationSeconds[i] / SECONDS_PER_HOUR,
            condition = weatherCode[i].toWeatherCondition(),
        )
    }
