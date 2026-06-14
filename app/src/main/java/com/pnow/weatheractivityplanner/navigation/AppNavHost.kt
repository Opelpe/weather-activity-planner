package com.pnow.weatheractivityplanner.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pnow.weatheractivityplanner.feature.forecast.WeatherForecastScreen
import com.pnow.weatheractivityplanner.feature.locationsearch.view.LocationSearchScreen
import com.pnow.weatheractivityplanner.feature.weatheractivity.view.WeatherRecommendationScreen


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = LocationSearchRoute,
    ) {

        composable<LocationSearchRoute> {
            LocationSearchScreen(
                onNavigateToRankings = { location ->
                    navController.navigate(
                        WeatherRecommendationRoute(
                            locationId = location.id,
                            locationName = location.name,
                            locationCountry = location.country,
                            latitude = location.latitude,
                            longitude = location.longitude,
                        ),
                    )
                },
            )
        }
        composable<WeatherRecommendationRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<WeatherRecommendationRoute>()
            WeatherRecommendationScreen(
                onNavigateToForecast = {
                    navController.navigate(
                        WeatherForecastRoute(
                            locationName = route.locationName,
                            locationCountry = route.locationCountry,
                            latitude = route.latitude,
                            longitude = route.longitude,
                        ),
                    )
                },
            )
        }
        composable<WeatherForecastRoute> {
            WeatherForecastScreen(onNavigateBack = navController::popBackStack)
        }
    }
}
