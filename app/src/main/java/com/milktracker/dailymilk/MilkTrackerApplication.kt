package com.milktracker.dailymilk

import android.app.Application
import com.milktracker.dailymilk.data.MilkDatabase
import com.milktracker.dailymilk.data.MilkRepository
import com.milktracker.dailymilk.data.PreferencesRepository
import com.milktracker.dailymilk.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Application class for the Daily Milk Tracker app.
 */
class MilkTrackerApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    val database by lazy { MilkDatabase.getDatabase(this) }
    val milkRepository by lazy { MilkRepository(database.milkEntryDao()) }
    val preferencesRepository by lazy { PreferencesRepository(this) }
    val notificationHelper by lazy { NotificationHelper(this) }
    
    override fun onCreate() {
        super.onCreate()
        
        // Schedule notification on app start if enabled
        applicationScope.launch {
            val enabled = preferencesRepository.notificationsEnabled.first()
            if (enabled) {
                val hour = preferencesRepository.notificationHour.first()
                val minute = preferencesRepository.notificationMinute.first()
                notificationHelper.scheduleNotification(hour, minute)
            }
        }
    }
}
