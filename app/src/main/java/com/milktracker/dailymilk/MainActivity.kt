package com.milktracker.dailymilk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.milktracker.dailymilk.ui.navigation.NavGraph
import com.milktracker.dailymilk.ui.theme.DailyMilkTrackerTheme

/**
 * Main activity for the Daily Milk Tracker app.
 */
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val application = application as MilkTrackerApplication
        
        setContent {
            DailyMilkTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavGraph(
                        navController = navController,
                        milkRepository = application.milkRepository,
                        preferencesRepository = application.preferencesRepository,
                        notificationHelper = application.notificationHelper
                    )
                }
            }
        }
    }
}
