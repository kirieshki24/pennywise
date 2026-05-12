package com.example.pennywise.data.repository

import com.example.pennywise.data.local.HistoryDao
import com.example.pennywise.data.local.ProfileDao
import com.example.pennywise.data.local.toDomain
import com.example.pennywise.data.local.toEntity
import com.example.pennywise.domain.model.HistoryEntry
import com.example.pennywise.domain.model.Profile
import com.example.pennywise.domain.model.TransactionType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FinanceRepositoryImpl(
    private val profileDao: ProfileDao,
    private val historyDao: HistoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FinanceRepository {
    override fun observeProfiles(): Flow<List<Profile>> {
        return profileDao.observeProfiles().map { profiles ->
            profiles.map { it.toDomain() }
        }
    }

    override fun observeHistory(): Flow<List<HistoryEntry>> {
        return historyDao.observeHistory().map { entries ->
            entries.map { it.toDomain() }
        }
    }

    override suspend fun getProfileById(id: Long): Profile? {
        return withContext(ioDispatcher) {
            profileDao.getById(id)?.toDomain()
        }
    }

    override suspend fun getHistoryById(id: Long): HistoryEntry? {
        return withContext(ioDispatcher) {
            historyDao.getById(id)?.toDomain()
        }
    }

    override suspend fun addProfile(
        name: String,
        monthlyLimit: Double,
        isUnlimited: Boolean
    ): Long {
        return withContext(ioDispatcher) {
            profileDao.insert(
                Profile(
                    id = 0,
                    name = name,
                    monthlyLimit = monthlyLimit,
                    isUnlimited = isUnlimited,
                    createdAt = System.currentTimeMillis()
                ).toEntity()
            )
        }
    }

    override suspend fun deleteProfile(id: Long) {
        withContext(ioDispatcher) {
            val existing = profileDao.getById(id) ?: return@withContext
            profileDao.delete(existing)
        }
    }

    override suspend fun addHistory(entry: HistoryEntry): Long {
        return withContext(ioDispatcher) {
            historyDao.insert(entry.copy(id = 0).toEntity())
        }
    }

    override suspend fun updateHistory(entry: HistoryEntry) {
        withContext(ioDispatcher) {
            historyDao.update(entry.toEntity())
        }
    }

    override suspend fun deleteHistory(id: Long) {
        withContext(ioDispatcher) {
            val existing = historyDao.getById(id) ?: return@withContext
            historyDao.delete(existing)
        }
    }

    override suspend fun getTotalExpenseForProfile(profileId: Long): Double {
        return withContext(ioDispatcher) {
            historyDao.getTotalAmountForProfile(profileId, TransactionType.EXPENSE.storageValue)
        }
    }
}
