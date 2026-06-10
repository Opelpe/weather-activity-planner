package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.pnow.weatheractivityplanner.domain.model.CurrentWeather
import com.pnow.weatheractivityplanner.domain.model.Forecast
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import com.pnow.weatheractivityplanner.domain.repository.WeatherRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private object GetForecastFixture {
    const val LATITUDE = 51.5
    const val LONGITUDE = -0.1
    const val TIMEZONE = "Europe/London"
    const val HTTP_ERROR_CODE = 429
    const val HTTP_ERROR_MESSAGE = "Too Many Requests"

    object Current {
        const val TEMPERATURE_CELSIUS = 18.0
        const val APPARENT_TEMPERATURE_CELSIUS = 17.0
        const val HUMIDITY_PERCENT = 65
        const val PRECIPITATION_MM = 0.0
        const val WIND_SPEED_KPH = 15.0
    }
}

class GetForecastUseCaseTest {

    @Test
    fun `given successful response, when invoked, then returns forecast`() = runTest {
        val forecast = buildForecast()
        val useCase = GetForecastUseCase(FakeWeatherRepository(Result.success(forecast)))

        val result = useCase(latitude = GetForecastFixture.LATITUDE, longitude = GetForecastFixture.LONGITUDE)

        assertEquals(forecast, result.getOrNull())
    }

    @Test
    fun `given http error, when invoked, then returns failure`() = runTest {
        val error = DomainError.HttpError(
            code = GetForecastFixture.HTTP_ERROR_CODE,
            message = GetForecastFixture.HTTP_ERROR_MESSAGE,
        )
        val useCase = GetForecastUseCase(FakeWeatherRepository(Result.failure(error)))

        val result = useCase(latitude = GetForecastFixture.LATITUDE, longitude = GetForecastFixture.LONGITUDE)

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `given network unavailable, when invoked, then returns NetworkUnavailable error`() = runTest {
        val error = DomainError.NetworkUnavailable()
        val useCase = GetForecastUseCase(FakeWeatherRepository(Result.failure(error)))

        val result = useCase(latitude = GetForecastFixture.LATITUDE, longitude = GetForecastFixture.LONGITUDE)

        assertTrue(result.exceptionOrNull() is DomainError.NetworkUnavailable)
    }

    private fun buildForecast() = Forecast(
        latitude = GetForecastFixture.LATITUDE,
        longitude = GetForecastFixture.LONGITUDE,
        timezone = GetForecastFixture.TIMEZONE,
        current = CurrentWeather(
            temperatureCelsius = GetForecastFixture.Current.TEMPERATURE_CELSIUS,
            apparentTemperatureCelsius = GetForecastFixture.Current.APPARENT_TEMPERATURE_CELSIUS,
            relativeHumidityPercent = GetForecastFixture.Current.HUMIDITY_PERCENT,
            precipitationMm = GetForecastFixture.Current.PRECIPITATION_MM,
            windSpeedKph = GetForecastFixture.Current.WIND_SPEED_KPH,
            condition = WeatherCondition.PartlyCloudy,
            isDay = true,
        ),
        daily = emptyList(),
    )

    private class FakeWeatherRepository(
        private val result: Result<Forecast>,
    ) : WeatherRepository {
        override suspend fun getForecast(latitude: Double, longitude: Double): Result<Forecast> =
            result
    }
}