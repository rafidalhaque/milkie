package com.milktracker.dailymilk.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing milk entries.
 */
class MilkRepository(private val milkEntryDao: MilkEntryDao) {
    
    fun getEntryByDateFlow(date: String): Flow<MilkEntry?> {
        return milkEntryDao.getEntryByDateFlow(date)
    }
    
    suspend fun getEntryByDate(date: String): MilkEntry? {
        return milkEntryDao.getEntryByDate(date)
    }
    
    fun getEntriesForMonth(yearMonth: String): Flow<List<MilkEntry>> {
        return milkEntryDao.getEntriesForMonth(yearMonth)
    }
    
    fun getAllEntries(): Flow<List<MilkEntry>> {
        return milkEntryDao.getAllEntries()
    }
    
    fun getMonthlyTotal(yearMonth: String): Flow<Double?> {
        return milkEntryDao.getMonthlyTotal(yearMonth)
    }
    
    suspend fun saveEntry(date: String, amountLiters: Double) {
        val entry = MilkEntry(date = date, amountLiters = amountLiters)
        milkEntryDao.insertEntry(entry)
    }
    
    suspend fun deleteEntry(date: String) {
        milkEntryDao.deleteEntryByDate(date)
    }
}
