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
    @param:StringRes val reasonRes: Int,
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
        activities = activities,
        score = score,
        reasonRes = reason.toStringRes(),
        isTopRanked = isTopRanked,
    )

@StringRes
internal fun Activities.toDisplayNameRes(): Int = when (this) {
    Activities.SKIING -> R.string.weather_activity_skiing
    Activities.SURFING -> R.string.weather_activity_surfing
    Activities.OUTDOOR_SIGHTSEEING -> R.string.weather_activity_outdoor_sightseeing
    Activities.INDOOR_SIGHTSEEING -> R.string.weather_activity_indoor_sightseeing
}

@DrawableRes
internal fun Activities.toIconRes(): Int = when (this) {
    Activities.SKIING -> R.drawable.ic_skiing
    Activities.SURFING -> R.drawable.ic_surfing
    Activities.OUTDOOR_SIGHTSEEING -> R.drawable.ic_outdoor_sightseeing
    Activities.INDOOR_SIGHTSEEING -> R.drawable.ic_indoor_sightseeing
}
