package com.pnow.weatheractivityplanner.feature.weatheractivity.view

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.domain.model.Activities
import com.pnow.weatheractivityplanner.feature.weatheractivity.WeatherRecommendationPreviewData
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.ActivitiesRankingUiModel
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.toDisplayNameRes
import com.pnow.weatheractivityplanner.feature.weatheractivity.model.toIconRes
import com.pnow.weatheractivityplanner.ui.theme.WeatherActivityPlannerTheme
import com.pnow.weatheractivityplanner.util.Dimens
import kotlin.math.roundToInt

@Composable
fun ActivitiesRankingItem(
    modifier: Modifier = Modifier,
    ranking: ActivitiesRankingUiModel,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = if (ranking.isTopRanked) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            )
        } else {
            CardDefaults.cardColors()
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Spacing16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing12),
        ) {

            ActivitiesRankingIcon(
                activities = ranking.activities,
            )

            ActivitiesRankingDetails(
                modifier = Modifier.weight(1f),
                ranking = ranking,
            )

            ActivitiesRankingScore(
                score = ranking.score,
            )
        }
    }
}

@Composable
private fun ActivitiesRankingIcon(
    modifier: Modifier = Modifier,
    activities: Activities,
) {
    Image(
        modifier = modifier.size(Dimens.IconSizeLarge),
        painter = painterResource(activities.toIconRes()),
        contentDescription = activities.name,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
    )
}

@Composable
private fun ActivitiesRankingDetails(
    modifier: Modifier = Modifier,
    ranking: ActivitiesRankingUiModel,
) {
    Column(modifier = modifier) {
        ActivitiesRankingTitle(
            activityTitleRes = ranking.activities.toDisplayNameRes(),
            isTopRanked = ranking.isTopRanked,
        )

        ActivitiesRankingReason(
            reasonRes = ranking.reasonRes,
        )

    }
}

@Composable
private fun ActivitiesRankingReason(
    modifier: Modifier = Modifier,
    @StringRes reasonRes: Int,
) {
    Text(
        modifier = modifier,
        text = stringResource(reasonRes),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun ActivitiesRankingTitle(
    modifier: Modifier = Modifier,
    isTopRanked: Boolean,
    @StringRes activityTitleRes: Int,
) {
    Text(
        modifier = modifier,
        text = stringResource(activityTitleRes),
        style = if (isTopRanked) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
    )
}

@Composable
private fun ActivitiesRankingScore(
    modifier: Modifier = Modifier,
    score: Float,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
    ) {
        Text(
            text = "${stringResource(R.string.weather_activity_score_label)}:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(
                R.string.weather_activity_score_value_format,
                score.roundToInt(),
            ),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ActivitiesRankingItemPreview() {
    WeatherActivityPlannerTheme {
        Surface {
            Column(
                modifier = Modifier.padding(Dimens.Spacing16),
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
            ) {
                ActivitiesRankingItem(
                    ranking = WeatherRecommendationPreviewData.Rankings.first(),
                )
                ActivitiesRankingItem(
                    ranking = WeatherRecommendationPreviewData.Rankings.last(),
                )
            }
        }
    }
}
