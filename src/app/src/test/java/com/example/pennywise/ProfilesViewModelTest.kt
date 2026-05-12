package com.example.pennywise

import com.example.pennywise.data.repository.FinanceRepository
import com.example.pennywise.domain.model.HistoryEntry
import com.example.pennywise.domain.model.Profile
import com.example.pennywise.domain.model.TransactionType
import com.example.pennywise.ui.profiles.ProfilesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfilesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun addProfileUsesUnlimitedFlag() = runTest {
        val repository = FakeFinanceRepository()
        val viewModel = ProfilesViewModel(repository)

        viewModel.onNameChange("Main")
        viewModel.onUnlimitedChange(true)
        viewModel.addProfile()

        val saved = repository.lastAdded
        requireNotNull(saved)
        assertTrue(saved.isUnlimited)
        assertEquals(0.0, saved.monthlyLimit, 0.0001)
    }

    private class FakeFinanceRepository : FinanceRepository {
        private val profilesFlow = MutableStateFlow<List<Profile>>(emptyList())
        private val historyFlow = MutableStateFlow<List<HistoryEntry>>(emptyList())
        var lastAdded: Profile? = null

        override fun observeProfiles(): StateFlow<List<Profile>> = profilesFlow

        override fun observeHistory(): StateFlow<List<HistoryEntry>> = historyFlow

        override suspend fun getProfileById(id: Long): Profile? = lastAdded

        override suspend fun getHistoryById(id: Long): HistoryEntry? = null

        override suspend fun addProfile(
            name: String,
            monthlyLimit: Double,
            isUnlimited: Boolean
        ): Long {
            val profile = Profile(
                id = 1,
                name = name,
                monthlyLimit = monthlyLimit,
                isUnlimited = isUnlimited,
                createdAt = 0
            )
            lastAdded = profile
            profilesFlow.value = listOf(profile)
            return 1
        }

        override suspend fun deleteProfile(id: Long) = Unit

        override suspend fun addHistory(entry: HistoryEntry): Long = 1

        override suspend fun updateHistory(entry: HistoryEntry) = Unit

        override suspend fun deleteHistory(id: Long) = Unit

        override suspend fun getTotalExpenseForProfile(profileId: Long): Double = 0.0
    }
}
