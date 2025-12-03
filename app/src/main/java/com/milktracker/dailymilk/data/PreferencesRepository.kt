package com.milktracker.dailymilk.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Repository for managing user preferences.
 */
class PreferencesRepository(private val context: Context) {
    
    companion object {
        private val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        private val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
        private val PRICE_PER_LITER = doublePreferencesKey("price_per_liter")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }
    
    val notificationHour: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATION_HOUR] ?: 8 // Default 8 AM
    }
    
    val notificationMinute: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATION_MINUTE] ?: 0
    }
    
    val pricePerLiter: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[PRICE_PER_LITER] ?: 50.0 // Default price
    }
    
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED] ?: true
    }
    
    suspend fun setNotificationTime(hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_HOUR] = hour
            preferences[NOTIFICATION_MINUTE] = minute
        }
    }
    
    suspend fun setPricePerLiter(price: Double) {
        context.dataStore.edit { preferences ->
            preferences[PRICE_PER_LITER] = price
        }
    }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
}
