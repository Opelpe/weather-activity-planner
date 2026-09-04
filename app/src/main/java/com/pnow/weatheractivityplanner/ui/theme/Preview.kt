package com.pnow.weatheractivityplanner.ui.theme

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class PreviewLightDark

@Preview(name = "Light", showBackground = true)
annotation class PreviewLight

@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class PreviewDark
