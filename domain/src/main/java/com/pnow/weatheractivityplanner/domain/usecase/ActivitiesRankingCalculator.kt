package com.pnow.weatheractivityplanner.domain.usecase

import com.pnow.weatheractivityplanner.domain.model.Activities
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRanking
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRankingReason
import com.pnow.weatheractivityplanner.domain.model.CurrentWeather
import com.pnow.weatheractivityplanner.domain.model.WeatherCondition
import javax.inject.Inject

class ActivitiesRankingCalculator @Inject constructor() {

    fun calculate(current: CurrentWeather): List<ActivitiesRanking> =
        Activities.entries
            .map { activity -> activity.toRanking(current) }
            .sortedWith(
                compareByDescending<ActivitiesRanking> { it.score }.thenBy { it.activities.name },
            )

    private fun Activities.toRanking(current: CurrentWeather): ActivitiesRanking = when (this) {
        Activities.SKIING -> rankSkiing(current)
        Activities.SURFING -> rankSurfing(current)
        Activities.OUTDOOR_SIGHTSEEING -> rankOutdoorSightseeing(current)
        Activities.INDOOR_SIGHTSEEING -> rankIndoorSightseeing(current)
    }

    private fun rankSkiing(current: CurrentWeather): ActivitiesRanking {
        val isSnowy = current.condition.isSnowy()
        val isFreezing = current.temperatureCelsius <= SKIING_FREEZING_THRESHOLD_CELSIUS
        val isRainy = current.condition.isRainy()
        val isTooWarm =
            !isFreezing && current.temperatureCelsius > SKIING_TOO_WARM_THRESHOLD_CELSIUS

        val score = SKIING_BASE_SCORE
            .bonus(isSnowy, SKIING_SNOW_BONUS)
            .bonus(isFreezing, SKIING_FREEZING_BONUS)
            .penalty(isRainy, SKIING_RAIN_PENALTY)
            .penalty(isTooWarm, SKIING_TOO_WARM_PENALTY)

        val reason = when {
            isSnowy && isFreezing -> ActivitiesRankingReason.SKIING_SNOW_AND_FREEZING
            isFreezing -> ActivitiesRankingReason.SKIING_FREEZING_ONLY
            isSnowy -> ActivitiesRankingReason.SKIING_SNOW_ONLY
            isRainy -> ActivitiesRankingReason.SKIING_RAIN
            else -> ActivitiesRankingReason.SKIING_NONE
        }

        return buildRanking(Activities.SKIING, score, reason)
    }

    private fun rankSurfing(current: CurrentWeather): ActivitiesRanking {
        val isWarm = current.temperatureCelsius >= SURFING_WARM_THRESHOLD_CELSIUS
        val isWindy = current.windSpeedKph >= SURFING_WINDY_THRESHOLD_KPH
        val isThunderstorm = current.condition.isThunderstorm()
        val isCold = current.temperatureCelsius < SURFING_COLD_THRESHOLD_CELSIUS

        val score = SURFING_BASE_SCORE
            .bonus(isWarm, SURFING_WARM_BONUS)
            .bonus(isWindy, SURFING_WIND_BONUS)
            .penalty(isThunderstorm, SURFING_THUNDERSTORM_PENALTY)
            .penalty(isCold, SURFING_COLD_PENALTY)

        val reason = when {
            isThunderstorm -> ActivitiesRankingReason.SURFING_THUNDERSTORM
            isWarm && isWindy -> ActivitiesRankingReason.SURFING_WARM_AND_WINDY
            isWindy -> ActivitiesRankingReason.SURFING_WINDY_ONLY
            isWarm -> ActivitiesRankingReason.SURFING_WARM_ONLY
            else -> ActivitiesRankingReason.SURFING_NONE
        }

        return buildRanking(Activities.SURFING, score, reason)
    }

    private fun rankOutdoorSightseeing(current: CurrentWeather): ActivitiesRanking {
        val isClear = current.condition.isClear()
        val isComfortable =
            current.temperatureCelsius in COMFORTABLE_MIN_CELSIUS..COMFORTABLE_MAX_CELSIUS
        val hasSignificantPrecipitation =
            current.precipitationMm > SIGNIFICANT_PRECIPITATION_THRESHOLD_MM
        val isFoggy = current.condition.isFoggy()

        val score = OUTDOOR_BASE_SCORE
            .bonus(isClear, OUTDOOR_CLEAR_BONUS)
            .bonus(isComfortable, OUTDOOR_COMFORTABLE_BONUS)
            .penalty(hasSignificantPrecipitation, OUTDOOR_RAIN_PENALTY)
            .penalty(isFoggy, OUTDOOR_FOG_PENALTY)

        val reason = when {
            isClear && isComfortable -> ActivitiesRankingReason.OUTDOOR_CLEAR_AND_COMFORTABLE
            hasSignificantPrecipitation -> ActivitiesRankingReason.OUTDOOR_RAIN
            isFoggy -> ActivitiesRankingReason.OUTDOOR_FOG
            isClear -> ActivitiesRankingReason.OUTDOOR_CLEAR_ONLY
            else -> ActivitiesRankingReason.OUTDOOR_NONE
        }

        return buildRanking(Activities.OUTDOOR_SIGHTSEEING, score, reason)
    }

    private fun rankIndoorSightseeing(current: CurrentWeather): ActivitiesRanking {
        val isClear = current.condition.isClear()
        val isComfortable =
            current.temperatureCelsius in COMFORTABLE_MIN_CELSIUS..COMFORTABLE_MAX_CELSIUS
        val isGreatOutdoor = isClear && isComfortable
        val isPoorOutdoor = current.precipitationMm > SIGNIFICANT_PRECIPITATION_THRESHOLD_MM ||
            current.condition.isRainy() ||
            current.condition.isThunderstorm() ||
            current.condition.isFoggy() ||
            current.condition.isSnowy()
        val isExtremeTemp =
            current.temperatureCelsius !in INDOOR_EXTREME_COLD_CELSIUS..INDOOR_EXTREME_HOT_CELSIUS

        val score = INDOOR_BASE_SCORE
            .bonus(isPoorOutdoor, INDOOR_POOR_OUTDOOR_BONUS)
            .bonus(isExtremeTemp, INDOOR_EXTREME_TEMP_BONUS)
            .penalty(isGreatOutdoor, INDOOR_GREAT_OUTDOOR_PENALTY)

        val reason = when {
            isPoorOutdoor -> ActivitiesRankingReason.INDOOR_POOR_OUTDOOR
            isExtremeTemp -> ActivitiesRankingReason.INDOOR_EXTREME_TEMP
            isGreatOutdoor -> ActivitiesRankingReason.INDOOR_GREAT_OUTDOOR
            else -> ActivitiesRankingReason.INDOOR_NONE
        }

        return buildRanking(Activities.INDOOR_SIGHTSEEING, score, reason)
    }

    private fun buildRanking(
        activities: Activities,
        score: Float,
        reason: ActivitiesRankingReason,
    ) = ActivitiesRanking(
        activities = activities,
        score = score.coerceIn(MIN_SCORE, MAX_SCORE),
        reason = reason,
    )

    private fun Float.bonus(
        condition: Boolean,
        amount: Float,
    ): Float =
        if (condition) this + amount else this

    private fun Float.penalty(
        condition: Boolean,
        amount: Float,
    ): Float =
        if (condition) this - amount else this

    private fun WeatherCondition.isSnowy(): Boolean = this in SNOWY_CONDITIONS
    private fun WeatherCondition.isRainy(): Boolean = this in RAINY_CONDITIONS
    private fun WeatherCondition.isThunderstorm(): Boolean = this in THUNDERSTORM_CONDITIONS
    private fun WeatherCondition.isClear(): Boolean = this in CLEAR_CONDITIONS
    private fun WeatherCondition.isFoggy(): Boolean = this in FOGGY_CONDITIONS

    private companion object {

        const val MIN_SCORE = 0f
        const val MAX_SCORE = 100f

        // Shared thresholds (used by more than one activity)
        const val COMFORTABLE_MIN_CELSIUS = 10.0
        const val COMFORTABLE_MAX_CELSIUS = 28.0
        const val SIGNIFICANT_PRECIPITATION_THRESHOLD_MM = 0.5

        // Skiing
        const val SKIING_BASE_SCORE = 10f
        const val SKIING_SNOW_BONUS = 70f
        const val SKIING_FREEZING_BONUS = 35f
        const val SKIING_RAIN_PENALTY = 40f
        const val SKIING_TOO_WARM_PENALTY = 25f
        const val SKIING_FREEZING_THRESHOLD_CELSIUS = -5.0
        const val SKIING_TOO_WARM_THRESHOLD_CELSIUS = 10.0

        // Surfing
        const val SURFING_BASE_SCORE = 20f
        const val SURFING_WARM_BONUS = 35f
        const val SURFING_WIND_BONUS = 35f
        const val SURFING_THUNDERSTORM_PENALTY = 70f
        const val SURFING_COLD_PENALTY = 20f
        const val SURFING_WARM_THRESHOLD_CELSIUS = 15.0
        const val SURFING_COLD_THRESHOLD_CELSIUS = 5.0
        const val SURFING_WINDY_THRESHOLD_KPH = 15.0

        // Outdoor sightseeing
        const val OUTDOOR_BASE_SCORE = 30f
        const val OUTDOOR_CLEAR_BONUS = 35f
        const val OUTDOOR_COMFORTABLE_BONUS = 25f
        const val OUTDOOR_RAIN_PENALTY = 45f
        const val OUTDOOR_FOG_PENALTY = 20f

        // Indoor sightseeing
        const val INDOOR_BASE_SCORE = 40f
        const val INDOOR_POOR_OUTDOOR_BONUS = 35f
        const val INDOOR_EXTREME_TEMP_BONUS = 15f
        const val INDOOR_GREAT_OUTDOOR_PENALTY = 15f
        const val INDOOR_EXTREME_COLD_CELSIUS = 5.0
        const val INDOOR_EXTREME_HOT_CELSIUS = 28.0

        val SNOWY_CONDITIONS = setOf(
            WeatherCondition.LightSnow,
            WeatherCondition.ModerateSnow,
            WeatherCondition.HeavySnow,
            WeatherCondition.SnowGrains,
            WeatherCondition.SlightSnowShowers,
            WeatherCondition.HeavySnowShowers,
        )

        val RAINY_CONDITIONS = setOf(
            WeatherCondition.LightDrizzle,
            WeatherCondition.ModerateDrizzle,
            WeatherCondition.DenseDrizzle,
            WeatherCondition.LightRain,
            WeatherCondition.ModerateRain,
            WeatherCondition.HeavyRain,
            WeatherCondition.SlightRainShowers,
            WeatherCondition.ModerateRainShowers,
            WeatherCondition.ViolentRainShowers,
        )

        val THUNDERSTORM_CONDITIONS = setOf(
            WeatherCondition.Thunderstorm,
            WeatherCondition.ThunderstormWithSlightHail,
            WeatherCondition.ThunderstormWithHeavyHail,
        )

        val CLEAR_CONDITIONS = setOf(
            WeatherCondition.Clear,
            WeatherCondition.MainlyClear,
            WeatherCondition.PartlyCloudy,
        )

        val FOGGY_CONDITIONS = setOf(
            WeatherCondition.Fog,
            WeatherCondition.DepositingRimeFog,
        )
    }
}
