package com.milktracker.dailymilk.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for milk entries.
 */
@Dao
interface MilkEntryDao {
    
    @Query("SELECT * FROM milk_entries WHERE date = :date")
    suspend fun getEntryByDate(date: String): MilkEntry?
    
    @Query("SELECT * FROM milk_entries WHERE date = :date")
    fun getEntryByDateFlow(date: String): Flow<MilkEntry?>
    
    @Query("SELECT * FROM milk_entries WHERE date LIKE :yearMonth || '%' ORDER BY date ASC")
    fun getEntriesForMonth(yearMonth: String): Flow<List<MilkEntry>>
    
    @Query("SELECT * FROM milk_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<MilkEntry>>
    
    @Query("SELECT SUM(amountLiters) FROM milk_entries WHERE date LIKE :yearMonth || '%'")
    fun getMonthlyTotal(yearMonth: String): Flow<Double?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: MilkEntry)
    
    @Delete
    suspend fun deleteEntry(entry: MilkEntry)
    
    @Query("DELETE FROM milk_entries WHERE date = :date")
    suspend fun deleteEntryByDate(date: String)
}
