package com.milktracker.dailymilk.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.milktracker.dailymilk.data.MilkRepository
import com.milktracker.dailymilk.data.PreferencesRepository
import com.milktracker.dailymilk.notification.NotificationHelper
import com.milktracker.dailymilk.ui.screens.HomeScreen
import com.milktracker.dailymilk.ui.screens.MonthlySummaryScreen
import com.milktracker.dailymilk.ui.screens.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    milkRepository: MilkRepository,
    preferencesRepository: PreferencesRepository,
    notificationHelper: NotificationHelper
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                milkRepository = milkRepository,
                onNavigateToSummary = {
                    navController.navigate(Screen.MonthlySummary.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(route = Screen.MonthlySummary.route) {
            MonthlySummaryScreen(
                milkRepository = milkRepository,
                preferencesRepository = preferencesRepository,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                preferencesRepository = preferencesRepository,
                notificationHelper = notificationHelper,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
