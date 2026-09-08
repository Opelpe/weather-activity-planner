package com.pnow.weatheractivityplanner.feature.weatheractivity.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import com.pnow.weatheractivityplanner.R
import com.pnow.weatheractivityplanner.domain.usecase.ActivityRankingDayRange
import com.pnow.weatheractivityplanner.ui.theme.PreviewLightDark
import com.pnow.weatheractivityplanner.ui.theme.WeatherActivityPlannerTheme
import com.pnow.weatheractivityplanner.util.Dimens

@Composable
fun ActivitiesRankingDaySelector(
    modifier: Modifier = Modifier,
    selectedDays: Int,
    onDaysChanged: (Int) -> Unit,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
    ) {
        for (days in ActivityRankingDayRange.MIN_DAY_COUNT..ActivityRankingDayRange.MAX_DAY_COUNT) {
            FilterChip(
                selected = days == selectedDays,
                onClick = { onDaysChanged(days) },
                label = {
                    Text(
                        pluralStringResource(
                            R.plurals.weather_activity_day_selector_value,
                            days,
                            days,
                        ),
                    )
                },
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ActivitiesRankingDaySelectorPreview() {
    WeatherActivityPlannerTheme {
        Surface {
            ActivitiesRankingDaySelector(
                modifier = Modifier.padding(Dimens.Spacing16),
                selectedDays = 3,
                onDaysChanged = {},
            )
        }
    }
}
