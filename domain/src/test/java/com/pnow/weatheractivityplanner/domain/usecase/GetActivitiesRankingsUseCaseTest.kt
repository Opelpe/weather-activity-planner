package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.error.DomainError
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingsResult
import com.pnow.weatheractivityplanner.domain.model.CurrentWeather
import com.pnow.weatheractivityplanner.domain.model.DailyForecast
import com.pnow.weatheractivityplanner.domain.model.Forecast
import com.pnow.weatheractivityplanner.domain.model.Location
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import com.pnow.weatheractivityplanner.domain.ranking.ActivitiesRankingCalculator
import com.pnow.weatheractivityplanner.domain.ranking.CyclingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.FishingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.IndoorSightseeingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.OutdoorSightseeingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.SkiingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.StargazingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.SunbathingDayScorer
import com.pnow.weatheractivityplanner.domain.ranking.SurfingDayScorer
import com.pnow.weatheractivityplanner.domain.repository.WeatherRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
        const val NIGHT_CLOUD_COVER_PERCENT = 50.0
        const val DAWN_DUSK_WIND_SPEED_KPH = 15.0
        const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 30.0
        const val DAYTIME_WIND_SPEED_MAX_KPH = 15.0
        const val DAYTIME_WIND_GUSTS_MAX_KPH = 25.0
    }

    object Day2 {

        const val DATE = "2026-06-16"
        const val MAX_TEMPERATURE_CELSIUS = -5.0
        const val MIN_TEMPERATURE_CELSIUS = -12.0
        const val PRECIPITATION_SUM_MM = 15.0
        const val PRECIPITATION_PROBABILITY_PERCENT = 90
        const val SNOWFALL_SUM_CM = 20.0
        const val WIND_SPEED_MAX_KPH = 40.0
        const val WIND_GUSTS_MAX_KPH = 60.0
        const val UV_INDEX_MAX = 1.0
        const val DAYLIGHT_DURATION_HOURS = 8.0
        const val NIGHT_CLOUD_COVER_PERCENT = 95.0
        const val DAWN_DUSK_WIND_SPEED_KPH = 35.0
        const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 80.0
        const val DAYTIME_WIND_SPEED_MAX_KPH = 45.0
        const val DAYTIME_WIND_GUSTS_MAX_KPH = 65.0
    }

    object Day3 {

        const val DATE = "2026-06-17"
        const val MAX_TEMPERATURE_CELSIUS = 18.0
        const val MIN_TEMPERATURE_CELSIUS = 9.0
        const val PRECIPITATION_SUM_MM = 2.0
        const val PRECIPITATION_PROBABILITY_PERCENT = 20
        const val SNOWFALL_SUM_CM = 0.0
        const val WIND_SPEED_MAX_KPH = 18.0
        const val WIND_GUSTS_MAX_KPH = 28.0
        const val UV_INDEX_MAX = 4.0
        const val DAYLIGHT_DURATION_HOURS = 14.0
        const val NIGHT_CLOUD_COVER_PERCENT = 60.0
        const val DAWN_DUSK_WIND_SPEED_KPH = 20.0
        const val DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT = 25.0
        const val DAYTIME_WIND_SPEED_MAX_KPH = 22.0
        const val DAYTIME_WIND_GUSTS_MAX_KPH = 30.0
    }
}

class GetActivitiesRankingsUseCaseTest {

    private val calculator = ActivitiesRankingCalculator(
        skiingDayScorer = SkiingDayScorer(),
        surfingDayScorer = SurfingDayScorer(),
        outdoorSightseeingDayScorer = OutdoorSightseeingDayScorer(),
        indoorSightseeingDayScorer = IndoorSightseeingDayScorer(),
        cyclingDayScorer = CyclingDayScorer(),
        sunbathingDayScorer = SunbathingDayScorer(),
        stargazingDayScorer = StargazingDayScorer(),
        fishingDayScorer = FishingDayScorer(),
    )

    @Test
    fun `given successful forecast, when invoked, then returns calculated rankings`() = runTest {
        val forecast = buildForecast()
        val useCase = GetActivityRankingsUseCase(
            getForecastUseCase = GetForecastUseCase(FakeWeatherRepository(Result.success(forecast))),
            activitiesRankingCalculator = calculator,
        )

        val result = useCase(location = buildLocation(), days = 1)

        val expected = ActivitiesRankingsResult(
            currentWeather = forecast.current,
            rankings = calculator.calculate(forecast.daily),
        )
        assertEquals(Result.success(expected), result)
    }

    @Test
    fun `given network error, when invoked, then returns failure`() = runTest {
        val useCase = GetActivityRankingsUseCase(
            getForecastUseCase = GetForecastUseCase(
                FakeWeatherRepository(Result.failure(DomainError.NetworkUnavailable())),
            ),
            activitiesRankingCalculator = calculator,
        )

        val result = useCase(buildLocation())

        assertTrue(result.exceptionOrNull() is DomainError.NetworkUnavailable)
    }

    @Test
    fun `given forceRefresh true, when invoked, then passes forceRefresh to forecast use case`() =
        runTest {
            val repository = FakeWeatherRepository(Result.success(buildForecast()))
            val useCase = GetActivityRankingsUseCase(
                getForecastUseCase = GetForecastUseCase(repository),
                activitiesRankingCalculator = calculator,
            )

            useCase(location = buildLocation(), forceRefresh = true)

            assertTrue(repository.capturedForceRefresh)
        }

    @Test
    fun `given days below the forecast length, when invoked, then only scores the requested number of days`() =
        runTest {
            val forecast = buildForecast(dayCount = 2)
            val useCase = GetActivityRankingsUseCase(
                getForecastUseCase = GetForecastUseCase(
                    FakeWeatherRepository(
                        Result.success(
                            forecast,
                        ),
                    ),
                ),
                activitiesRankingCalculator = calculator,
            )

            val result = useCase(location = buildLocation(), days = 1)

            val expected = ActivitiesRankingsResult(
                currentWeather = forecast.current,
                rankings = calculator.calculate(forecast.daily.take(1)),
            )
            assertEquals(Result.success(expected), result)
        }

    @Test
    fun `given days above the maximum, when invoked, then coerces to the maximum day count`() =
        runTest {
            val forecast = buildForecast(dayCount = 3)
            val useCase = GetActivityRankingsUseCase(
                getForecastUseCase = GetForecastUseCase(
                    FakeWeatherRepository(
                        Result.success(
                            forecast,
                        ),
                    ),
                ),
                activitiesRankingCalculator = calculator,
            )

            val result = useCase(location = buildLocation(), days = 10)

            // The fixture forecast only has 3 days, fewer than the coerced 7-day window.
            val expected = ActivitiesRankingsResult(
                currentWeather = forecast.current,
                rankings = calculator.calculate(
                    forecast.daily.drop(1).take(ActivityRankingDayRange.MAX_DAY_COUNT),
                ),
                isIncomplete = true,
            )
            assertEquals(Result.success(expected), result)
        }

    @Test
    fun `given days above one, when invoked, then excludes today and scores the following days`() =
        runTest {
            val forecast = buildForecast(dayCount = 3)
            val useCase = GetActivityRankingsUseCase(
                getForecastUseCase = GetForecastUseCase(
                    FakeWeatherRepository(
                        Result.success(
                            forecast,
                        ),
                    ),
                ),
                activitiesRankingCalculator = calculator,
            )

            val result = useCase(location = buildLocation(), days = 2)

            val expected = ActivitiesRankingsResult(
                currentWeather = forecast.current,
                rankings = calculator.calculate(forecast.daily.drop(1).take(2)),
            )
            assertEquals(Result.success(expected), result)
        }

    @Test
    fun `given days below the minimum, when invoked, then coerces to the minimum day count`() =
        runTest {
            val forecast = buildForecast(dayCount = 2)
            val useCase = GetActivityRankingsUseCase(
                getForecastUseCase = GetForecastUseCase(
                    FakeWeatherRepository(
                        Result.success(
                            forecast,
                        ),
                    ),
                ),
                activitiesRankingCalculator = calculator,
            )

            val result = useCase(location = buildLocation(), days = 0)

            val expected = ActivitiesRankingsResult(
                currentWeather = forecast.current,
                rankings = calculator.calculate(forecast.daily.take(ActivityRankingDayRange.MIN_DAY_COUNT)),
            )
            assertEquals(Result.success(expected), result)
        }

    @Test
    fun `given forecast shorter than the requested window, when invoked, then result is marked incomplete`() =
        runTest {
            val forecast = buildForecast(dayCount = 2)
            val useCase = GetActivityRankingsUseCase(
                getForecastUseCase = GetForecastUseCase(
                    FakeWeatherRepository(
                        Result.success(
                            forecast,
                        ),
                    ),
                ),
                activitiesRankingCalculator = calculator,
            )

            val result = useCase(location = buildLocation(), days = 3)

            assertTrue(result.getOrNull()!!.isIncomplete)
        }

    @Test
    fun `given forecast covers the entire requested window, when invoked, then result is not marked incomplete`() =
        runTest {
            val forecast = buildForecast(dayCount = 3)
            val useCase = GetActivityRankingsUseCase(
                getForecastUseCase = GetForecastUseCase(
                    FakeWeatherRepository(
                        Result.success(
                            forecast,
                        ),
                    ),
                ),
                activitiesRankingCalculator = calculator,
            )

            val result = useCase(location = buildLocation(), days = 2)

            assertFalse(result.getOrNull()!!.isIncomplete)
        }

    private fun buildLocation() = Location(
        id = GetActivitiesRankingFixture.Paris.ID,
        name = GetActivitiesRankingFixture.Paris.NAME,
        latitude = GetActivitiesRankingFixture.Paris.LATITUDE,
        longitude = GetActivitiesRankingFixture.Paris.LONGITUDE,
        country = GetActivitiesRankingFixture.Paris.COUNTRY,
        region = null,
    )

    private fun buildForecast(dayCount: Int = 1) = Forecast(
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
                nightCloudCoverPercent = GetActivitiesRankingFixture.Day1.NIGHT_CLOUD_COVER_PERCENT,
                dawnDuskWindSpeedKph = GetActivitiesRankingFixture.Day1.DAWN_DUSK_WIND_SPEED_KPH,
                dawnDuskPrecipitationProbabilityPercent = GetActivitiesRankingFixture.Day1.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
                daytimeWindSpeedMaxKph = GetActivitiesRankingFixture.Day1.DAYTIME_WIND_SPEED_MAX_KPH,
                daytimeWindGustsMaxKph = GetActivitiesRankingFixture.Day1.DAYTIME_WIND_GUSTS_MAX_KPH,
                condition = WeatherCondition.Clear,
            ),
            DailyForecast(
                date = GetActivitiesRankingFixture.Day2.DATE,
                maxTemperatureCelsius = GetActivitiesRankingFixture.Day2.MAX_TEMPERATURE_CELSIUS,
                minTemperatureCelsius = GetActivitiesRankingFixture.Day2.MIN_TEMPERATURE_CELSIUS,
                precipitationSumMm = GetActivitiesRankingFixture.Day2.PRECIPITATION_SUM_MM,
                precipitationProbabilityMaxPercent = GetActivitiesRankingFixture.Day2.PRECIPITATION_PROBABILITY_PERCENT,
                snowfallSumCm = GetActivitiesRankingFixture.Day2.SNOWFALL_SUM_CM,
                windSpeedMaxKph = GetActivitiesRankingFixture.Day2.WIND_SPEED_MAX_KPH,
                windGustsMaxKph = GetActivitiesRankingFixture.Day2.WIND_GUSTS_MAX_KPH,
                uvIndexMax = GetActivitiesRankingFixture.Day2.UV_INDEX_MAX,
                daylightDurationHours = GetActivitiesRankingFixture.Day2.DAYLIGHT_DURATION_HOURS,
                nightCloudCoverPercent = GetActivitiesRankingFixture.Day2.NIGHT_CLOUD_COVER_PERCENT,
                dawnDuskWindSpeedKph = GetActivitiesRankingFixture.Day2.DAWN_DUSK_WIND_SPEED_KPH,
                dawnDuskPrecipitationProbabilityPercent = GetActivitiesRankingFixture.Day2.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
                daytimeWindSpeedMaxKph = GetActivitiesRankingFixture.Day2.DAYTIME_WIND_SPEED_MAX_KPH,
                daytimeWindGustsMaxKph = GetActivitiesRankingFixture.Day2.DAYTIME_WIND_GUSTS_MAX_KPH,
                condition = WeatherCondition.HeavySnow,
            ),
            DailyForecast(
                date = GetActivitiesRankingFixture.Day3.DATE,
                maxTemperatureCelsius = GetActivitiesRankingFixture.Day3.MAX_TEMPERATURE_CELSIUS,
                minTemperatureCelsius = GetActivitiesRankingFixture.Day3.MIN_TEMPERATURE_CELSIUS,
                precipitationSumMm = GetActivitiesRankingFixture.Day3.PRECIPITATION_SUM_MM,
                precipitationProbabilityMaxPercent = GetActivitiesRankingFixture.Day3.PRECIPITATION_PROBABILITY_PERCENT,
                snowfallSumCm = GetActivitiesRankingFixture.Day3.SNOWFALL_SUM_CM,
                windSpeedMaxKph = GetActivitiesRankingFixture.Day3.WIND_SPEED_MAX_KPH,
                windGustsMaxKph = GetActivitiesRankingFixture.Day3.WIND_GUSTS_MAX_KPH,
                uvIndexMax = GetActivitiesRankingFixture.Day3.UV_INDEX_MAX,
                daylightDurationHours = GetActivitiesRankingFixture.Day3.DAYLIGHT_DURATION_HOURS,
                nightCloudCoverPercent = GetActivitiesRankingFixture.Day3.NIGHT_CLOUD_COVER_PERCENT,
                dawnDuskWindSpeedKph = GetActivitiesRankingFixture.Day3.DAWN_DUSK_WIND_SPEED_KPH,
                dawnDuskPrecipitationProbabilityPercent = GetActivitiesRankingFixture.Day3.DAWN_DUSK_PRECIPITATION_PROBABILITY_PERCENT,
                daytimeWindSpeedMaxKph = GetActivitiesRankingFixture.Day3.DAYTIME_WIND_SPEED_MAX_KPH,
                daytimeWindGustsMaxKph = GetActivitiesRankingFixture.Day3.DAYTIME_WIND_GUSTS_MAX_KPH,
                condition = WeatherCondition.Clear,
            ),
        ).take(dayCount),
    )

    private class FakeWeatherRepository(
        private val result: Result<Forecast>,
    ) : WeatherRepository {

        var capturedForceRefresh = false

        override suspend fun getForecast(
            locationId: Long,
            latitude: Double,
            longitude: Double,
            forceRefresh: Boolean,
        ): Result<Forecast> {
            capturedForceRefresh = forceRefresh
            return result
        }
    }
}
