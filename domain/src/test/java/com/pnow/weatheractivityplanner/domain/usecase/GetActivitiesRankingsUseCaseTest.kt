package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingsResult
import com.pnow.weatheractivityplanner.domain.model.CurrentWeather
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.Forecast
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import com.pnow.weatheractivityplanner.domain.ranking.ActivitiesRankingCalculator
import com.pnow.weatheractivityplanner.domain.ranking.IndoorSightseeingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.OutdoorSightseeingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.SkiingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.SurfingDayScorer
import com.pnow.weatheractivityplanner.domain.repository.WeatherRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private object GetActivitiesRankingFixture {

    object Paris {

        const val ID = 1L
        const val NAME = "Paris"
        const val LATITUDE = 48.85
        const val LONGITUDE = 2.35
        const val COUNTRY = "France"
        const val TIMEZONE = "Europe/Paris"
        const val TEMPERATURE_CELSIUS = 22.0
        const val APPARENT_TEMPERATURE_CELSIUS = 21.0
        const val HUMIDITY_PERCENT = 50
        const val PRECIPITATION_MM = 0.0
        const val WIND_SPEED_KPH = 10.0
    }

    object Day1 {

        const val DATE = "2026-06-15"
        const val MAX_TEMPERATURE_CELSIUS = 24.0
        const val MIN_TEMPERATURE_CELSIUS = 14.0
        const val PRECIPITATION_SUM_MM = 0.0
        const val PRECIPITATION_PROBABILITY_PERCENT = 10
        const val SNOWFALL_SUM_CM = 0.0
        const val WIND_SPEED_MAX_KPH = 12.0
        const val WIND_GUSTS_MAX_KPH = 20.0
        const val UV_INDEX_MAX = 5.0
        const val DAYLIGHT_DURATION_HOURS = 15.5
    }
}

class GetActivitiesRankingsUseCaseTest {

    private val calculator = ActivitiesRankingCalculator(
        skiingDayScorer = SkiingDayScorer(),
        surfingDayScorer = SurfingDayScorer(),
        outdoorSightseeingDayScorer = OutdoorSightseeingDayScorer(),
        indoorSightseeingDayScorer = IndoorSightseeingDayScorer(),
    )

    @Test
    fun `given successful forecast, when invoked, then returns calculated rankings`() = runTest {
        val forecast = buildForecast()
        val useCase = GetActivityRankingsUseCase(
            getForecastUseCase = fakeGetForecastUseCase(Result.success(forecast)),
            activitiesRankingCalculator = calculator,
        )

        val result = useCase(buildLocation())

        val expected = ActivitiesRankingsResult(
            currentWeather = forecast.current,
            rankings = calculator.calculate(forecast.daily),
        )
        assertEquals(Result.success(expected), result)
    }

    @Test
    fun `given network error, when invoked, then returns failure`() = runTest {
        val useCase = GetActivityRankingsUseCase(
            getForecastUseCase = fakeGetForecastUseCase(
                Result.failure(DomainError.NetworkUnavailable()),
            ),
            activitiesRankingCalculator = calculator,
        )

        val result = useCase(buildLocation())

        assertTrue(result.exceptionOrNull() is DomainError.NetworkUnavailable)
    }

    private fun fakeGetForecastUseCase(result: Result<Forecast>) =
        GetForecastUseCase(FakeWeatherRepository(result))

    private fun buildLocation() = Location(
        id = GetActivitiesRankingFixture.Paris.ID,
        name = GetActivitiesRankingFixture.Paris.NAME,
        latitude = GetActivitiesRankingFixture.Paris.LATITUDE,
        longitude = GetActivitiesRankingFixture.Paris.LONGITUDE,
        country = GetActivitiesRankingFixture.Paris.COUNTRY,
        countryCode = null,
        region = null,
    )

    private fun buildForecast() = Forecast(
        latitude = GetActivitiesRankingFixture.Paris.LATITUDE,
        longitude = GetActivitiesRankingFixture.Paris.LONGITUDE,
        timezone = GetActivitiesRankingFixture.Paris.TIMEZONE,
        current = CurrentWeather(
            temperatureCelsius = GetActivitiesRankingFixture.Paris.TEMPERATURE_CELSIUS,
            apparentTemperatureCelsius = GetActivitiesRankingFixture.Paris.APPARENT_TEMPERATURE_CELSIUS,
            relativeHumidityPercent = GetActivitiesRankingFixture.Paris.HUMIDITY_PERCENT,
            precipitationMm = GetActivitiesRankingFixture.Paris.PRECIPITATION_MM,
            windSpeedKph = GetActivitiesRankingFixture.Paris.WIND_SPEED_KPH,
            condition = WeatherCondition.Clear,
            isDay = true,
        ),
        daily = listOf(
            DailyForecast(
                date = GetActivitiesRankingFixture.Day1.DATE,
                maxTemperatureCelsius = GetActivitiesRankingFixture.Day1.MAX_TEMPERATURE_CELSIUS,
                minTemperatureCelsius = GetActivitiesRankingFixture.Day1.MIN_TEMPERATURE_CELSIUS,
                precipitationSumMm = GetActivitiesRankingFixture.Day1.PRECIPITATION_SUM_MM,
                precipitationProbabilityMaxPercent = GetActivitiesRankingFixture.Day1.PRECIPITATION_PROBABILITY_PERCENT,
                snowfallSumCm = GetActivitiesRankingFixture.Day1.SNOWFALL_SUM_CM,
                windSpeedMaxKph = GetActivitiesRankingFixture.Day1.WIND_SPEED_MAX_KPH,
                windGustsMaxKph = GetActivitiesRankingFixture.Day1.WIND_GUSTS_MAX_KPH,
                uvIndexMax = GetActivitiesRankingFixture.Day1.UV_INDEX_MAX,
                daylightDurationHours = GetActivitiesRankingFixture.Day1.DAYLIGHT_DURATION_HOURS,
                condition = WeatherCondition.Clear,
            ),
        ),
    )

    private class FakeWeatherRepository(
        private val result: Result<Forecast>,
    ) : WeatherRepository {

        override suspend fun getForecast(
            latitude: Double,
            longitude: Double,
        ): Result<Forecast> = result
    }
}
