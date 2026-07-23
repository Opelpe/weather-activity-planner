package com.pnow.weatheractivityplanner.feature.weatheractivity.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.domain.model.Activities
import com.pnow.weatheractivityplanner.domain.model.ActivitiesRanking

data class ActivitiesRankingUiModel(
    val activities: Activities,
    val score: Float,
    val isTopRanked: Boolean,
    @param:StringRes val weeklyReasonRes: Int,
    @param:StringRes val dailyReasonRes: Int,
)

internal fun List<ActivitiesRanking>.toUiModels(): List<ActivitiesRankingUiModel> {
    val topScore = maxOfOrNull { it.score }
    return map { ranking ->
        val isTopRanked = ranking.score == topScore
        ranking.toUiModel(isTopRanked = isTopRanked)
    }
}

internal fun ActivitiesRanking.toUiModel(isTopRanked: Boolean): ActivitiesRankingUiModel =
    ActivitiesRankingUiModel(
        activities = activity,
        score = score,
        weeklyReasonRes = weeklyReason.toStringRes(),
        dailyReasonRes = dailyReason.toStringRes(),
        isTopRanked = isTopRanked,
    )

@StringRes
internal fun Activities.toDisplayNameRes(): Int = when (this) {
    Activities.SKIING -> R.string.weather_activity_skiing
    Activities.SURFING -> R.string.weather_activity_surfing
    Activities.OUTDOOR_SIGHTSEEING -> R.string.weather_activity_outdoor_sightseeing
    Activities.INDOOR_SIGHTSEEING -> R.string.weather_activity_indoor_sightseeing
    Activities.CYCLING -> R.string.weather_activity_cycling
    Activities.SUNBATHING -> R.string.weather_activity_sunbathing
    Activities.STARGAZING -> R.string.weather_activity_stargazing
    Activities.FISHING -> R.string.weather_activity_fishing
}

@DrawableRes
internal fun Activities.toIconRes(): Int = when (this) {
    Activities.SKIING -> R.drawable.ic_skiing
    Activities.SURFING -> R.drawable.ic_surfing
    Activities.OUTDOOR_SIGHTSEEING -> R.drawable.ic_outdoor_sightseeing
    Activities.INDOOR_SIGHTSEEING -> R.drawable.ic_indoor_sightseeing
    Activities.CYCLING -> R.drawable.ic_cycling
    Activities.SUNBATHING -> R.drawable.ic_sunbathing
    Activities.STARGAZING -> R.drawable.ic_stargazing
    Activities.FISHING -> R.drawable.ic_fishing
}
