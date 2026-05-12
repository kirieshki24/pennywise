package com.example.pennywise.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_entries ORDER BY timestamp DESC")
    fun observeHistory(): Flow<List<HistoryEntryEntity>>

    @Query("SELECT * FROM history_entries WHERE id = :id")
    suspend fun getById(id: Long): HistoryEntryEntity?

    @Query(
        "SELECT COALESCE(SUM(amount), 0) FROM history_entries " +
            "WHERE profileId = :profileId AND type = :type"
    )
    suspend fun getTotalAmountForProfile(profileId: Long, type: String): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HistoryEntryEntity): Long

    @Update
    suspend fun update(entry: HistoryEntryEntity)

    @Delete
    suspend fun delete(entry: HistoryEntryEntity)
}
