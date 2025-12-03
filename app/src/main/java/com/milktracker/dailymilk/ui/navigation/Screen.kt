package com.milktracker.dailymilk.ui.navigation

/**
 * Navigation routes for the app.
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object MonthlySummary : Screen("monthly_summary")
    data object Settings : Screen("settings")
}
