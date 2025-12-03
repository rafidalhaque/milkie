package com.milktracker.dailymilk.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a daily milk entry.
 */
@Entity(tableName = "milk_entries")
data class MilkEntry(
    @PrimaryKey
    val date: String, // Format: YYYY-MM-DD
    val amountLiters: Double,
    val timestamp: Long = System.currentTimeMillis()
)
