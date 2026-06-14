package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingsResult
import com.pnow.weatheractivityplanner.domain.model.CurrentWeather
import com.pnow.weatheractivityplanner.domain.model.Forecast
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import com.pnow.weatheractivityplanner.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.toList
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
}

class GetActivitiesRankingsUseCaseTest {

    private val calculator = ActivitiesRankingCalculator()

    @Test
    fun `given successful forecast, when invoked, then emits calculated rankings`() = runTest {
        val forecast = buildForecast()
        val useCase = GetActivityRankingsUseCase(
            getForecastUseCase = fakeGetForecastUseCase(Result.success(forecast)),
            activitiesRankingCalculator = calculator,
        )

        val result = useCase(buildLocation()).toList()

        val expected = ActivitiesRankingsResult(
            currentWeather = forecast.current,
            rankings = calculator.calculate(forecast.current),
        )
        assertEquals(listOf(expected), result)
    }

    @Test
    fun `given network error, when invoked, then propagates failure`() = runTest {
        val useCase = GetActivityRankingsUseCase(
            getForecastUseCase = fakeGetForecastUseCase(
                Result.failure(DomainError.NetworkUnavailable()),
            ),
            activitiesRankingCalculator = calculator,
        )

        val exception = runCatching { useCase(buildLocation()).toList() }.exceptionOrNull()

        assertTrue(exception is DomainError.NetworkUnavailable)
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
        daily = emptyList(),
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
