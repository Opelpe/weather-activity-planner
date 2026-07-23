package com.pnow.weatheractivityplanner.data.mapper

import com.pnow.weatheractivityplanner.data.remote.dto.forecast.CurrentWeatherDto
import com.pnow.weatheractivityplanner.data.remote.dto.forecast.DailyDataDto
import com.pnow.weatheractivityplanner.data.remote.dto.forecast.ForecastResponseDto
import com.pnow.weatheractivityplanner.data.remote.dto.forecast.HourlyDataDto
import com.pnow.weatheractivityplanner.domain.model.CurrentWeather
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.Forecast

private const val MISSING_CURRENT_WEATHER_MESSAGE = "Forecast response is missing current weather data"
private const val SECONDS_PER_HOUR = 3600.0
private const val DATE_LENGTH = 10
private const val IS_NIGHT = 0
private const val IS_DAYTIME = 1
private const val NEUTRAL_NIGHT_CLOUD_COVER_PERCENT = 50.0

internal fun ForecastResponseDto.toDomain(): Forecast = Forecast(
    latitude = latitude,
    longitude = longitude,
    timezone = timezone,
    current = requireNotNull(current) { MISSING_CURRENT_WEATHER_MESSAGE }.toDomain(),
    daily = daily.toDomainList(hourly.toDailyAggregates()),
)

private data class HourlyDailyAggregates(
    val nightCloudCoverPercentByDate: Map<String, Double>,
    val dawnDuskWindSpeedKphByDate: Map<String, Double>,
    val dawnDuskPrecipitationProbabilityPercentByDate: Map<String, Double>,
    val daytimeWindSpeedMaxKphByDate: Map<String, Double>,
    val daytimeWindGustsMaxKphByDate: Map<String, Double>,
)

private fun HourlyDataDto.toDailyAggregates(): HourlyDailyAggregates = HourlyDailyAggregates(
    nightCloudCoverPercentByDate = averageByDate(cloudCoverPercent.map { it.toDouble() }) { isDay[it] == IS_NIGHT },
    dawnDuskWindSpeedKphByDate = averageByDate(windSpeedKph, ::isTwilightHour),
    dawnDuskPrecipitationProbabilityPercentByDate = averageByDate(
        precipitationProbabilityPercent.map { it.toDouble() },
        ::isTwilightHour,
    ),
    daytimeWindSpeedMaxKphByDate = maxByDate(windSpeedKph) { isDay[it] == IS_DAYTIME },
    daytimeWindGustsMaxKphByDate = maxByDate(windGustsKph) { isDay[it] == IS_DAYTIME },
)

private fun HourlyDataDto.averageByDate(
    values: List<Double>,
    includeIndex: (Int) -> Boolean,
): Map<String, Double> =
    groupIndicesByDate(values, includeIndex) { it.average() }

private fun HourlyDataDto.maxByDate(
    values: List<Double>,
    includeIndex: (Int) -> Boolean,
): Map<String, Double> =
    groupIndicesByDate(values, includeIndex) { it.max() }

private fun HourlyDataDto.groupIndicesByDate(
    values: List<Double>,
    includeIndex: (Int) -> Boolean,
    aggregate: (List<Double>) -> Double,
): Map<String, Double> =
    time.indices
        .filter(includeIndex)
        .groupBy { i -> time[i].substring(0, DATE_LENGTH) }
        .mapValues { (_, indices) -> aggregate(indices.map { values[it] }) }

private fun HourlyDataDto.isTwilightHour(index: Int): Boolean {
    val previousDiffers = index > 0 && isDay[index] != isDay[index - 1]
    val nextDiffers = index < isDay.lastIndex && isDay[index] != isDay[index + 1]
    return previousDiffers || nextDiffers
}

internal fun CurrentWeatherDto.toDomain(): CurrentWeather = CurrentWeather(
    temperatureCelsius = temperatureCelsius,
    apparentTemperatureCelsius = apparentTemperatureCelsius,
    relativeHumidityPercent = relativeHumidityPercent,
    precipitationMm = precipitation,
    windSpeedKph = windSpeedKph,
    condition = weatherCode.toWeatherCondition(),
    isDay = isDay == 1,
)

private fun DailyDataDto.toDomainList(aggregates: HourlyDailyAggregates): List<DailyForecast> =
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
            nightCloudCoverPercent = aggregates.nightCloudCoverPercentByDate[time[i]]
                ?: NEUTRAL_NIGHT_CLOUD_COVER_PERCENT,
            dawnDuskWindSpeedKph = aggregates.dawnDuskWindSpeedKphByDate[time[i]]
                ?: windSpeedMaxKph[i],
            dawnDuskPrecipitationProbabilityPercent = aggregates.dawnDuskPrecipitationProbabilityPercentByDate[time[i]]
                ?: precipitationProbabilityMaxPercent[i].toDouble(),
            daytimeWindSpeedMaxKph = aggregates.daytimeWindSpeedMaxKphByDate[time[i]]
                ?: windSpeedMaxKph[i],
            daytimeWindGustsMaxKph = aggregates.daytimeWindGustsMaxKphByDate[time[i]]
                ?: windGustsMaxKph[i],
            condition = weatherCode[i].toWeatherCondition(),
        )
    }
