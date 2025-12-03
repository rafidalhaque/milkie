package com.milktracker.dailymilk.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.milktracker.dailymilk.data.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Receiver for rescheduling notifications after device boot.
 */
class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val preferencesRepository = PreferencesRepository(context)
            
            CoroutineScope(Dispatchers.IO).launch {
                val enabled = preferencesRepository.notificationsEnabled.first()
                if (enabled) {
                    val hour = preferencesRepository.notificationHour.first()
                    val minute = preferencesRepository.notificationMinute.first()
                    
                    NotificationHelper(context).scheduleNotification(hour, minute)
                }
            }
        }
    }
}
