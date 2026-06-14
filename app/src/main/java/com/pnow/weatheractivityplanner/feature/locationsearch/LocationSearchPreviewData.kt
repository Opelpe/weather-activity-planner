package com.pnow.weatheractivityplanner.feature.locationsearch

internal object LocationSearchPreviewData {

    const val SEARCH_QUERY = "London"

    val Locations = listOf(
        LocationUiModel(
            id = 1L,
            name = "London",
            country = "United Kingdom",
            latitude = 51.5072,
            longitude = -0.1276,
        ),
        LocationUiModel(
            id = 2L,
            name = "London",
            country = "Canada",
            latitude = 42.9849,
            longitude = -81.2453,
        ),
    )
}
