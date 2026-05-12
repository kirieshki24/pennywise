package com.example.pennywise.data.repository

import com.example.pennywise.domain.model.HistoryEntry
import com.example.pennywise.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun observeProfiles(): Flow<List<Profile>>
    fun observeHistory(): Flow<List<HistoryEntry>>
    suspend fun getProfileById(id: Long): Profile?
    suspend fun getHistoryById(id: Long): HistoryEntry?
    suspend fun addProfile(name: String, monthlyLimit: Double): Long
    suspend fun deleteProfile(id: Long)
    suspend fun addHistory(entry: HistoryEntry): Long
    suspend fun updateHistory(entry: HistoryEntry)
    suspend fun deleteHistory(id: Long)
}
