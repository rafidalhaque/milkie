package com.milktracker.dailymilk.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.milktracker.dailymilk.data.MilkDatabase
import com.milktracker.dailymilk.data.MilkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Receiver for handling notification quick actions.
 */
class NotificationActionReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val amount = when (intent.action) {
            NotificationHelper.ACTION_LOG_ZERO -> 0.0
            NotificationHelper.ACTION_LOG_HALF -> 0.5
            NotificationHelper.ACTION_LOG_ONE -> 1.0
            else -> return
        }
        
        val database = MilkDatabase.getDatabase(context)
        val repository = MilkRepository(database.milkEntryDao())
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        CoroutineScope(Dispatchers.IO).launch {
            repository.saveEntry(today, amount)
        }
        
        // Dismiss the notification
        NotificationHelper(context).dismissNotification()
    }
}
