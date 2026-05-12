package com.example.pennywise

import com.example.pennywise.data.repository.FinanceRepository
import com.example.pennywise.domain.model.HistoryEntry
import com.example.pennywise.domain.model.Profile
import com.example.pennywise.domain.model.TransactionType
import com.example.pennywise.ui.edit.EditEntryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditEntryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun showsConfirmationWhenLimitExceeded() = runTest {
        val repository = FakeFinanceRepository()
        val viewModel = EditEntryViewModel(repository, entryId = null)

        repository.emitProfiles(
            listOf(
                Profile(
                    id = 1,
                    name = "Main",
                    monthlyLimit = 35.0,
                    isUnlimited = false,
                    createdAt = 0
                )
            )
        )
        advanceUntilIdle()

        viewModel.onAmountChange("10")
        viewModel.onNoteChange("Coffee")
        viewModel.onTypeSelected(TransactionType.EXPENSE)

        viewModel.saveEntry()
        advanceUntilIdle()

        val exceededBy = viewModel.uiState.value.confirmLimitExceededBy
        assertNotNull(exceededBy)
        assertEquals(5.0, exceededBy ?: 0.0, 0.0001)
    }

    private class FakeFinanceRepository : FinanceRepository {
        private val profilesFlow = MutableStateFlow<List<Profile>>(emptyList())
        private val historyFlow = MutableStateFlow<List<HistoryEntry>>(emptyList())

        fun emitProfiles(profiles: List<Profile>) {
            profilesFlow.value = profiles
        }

        override fun observeProfiles(): StateFlow<List<Profile>> = profilesFlow

        override fun observeHistory(): StateFlow<List<HistoryEntry>> = historyFlow

        override suspend fun getProfileById(id: Long): Profile? =
            profilesFlow.value.firstOrNull { it.id == id }

        override suspend fun getHistoryById(id: Long): HistoryEntry? = null

        override suspend fun addProfile(
            name: String,
            monthlyLimit: Double,
            isUnlimited: Boolean
        ): Long = 1

        override suspend fun deleteProfile(id: Long) = Unit

        override suspend fun addHistory(entry: HistoryEntry): Long = 1

        override suspend fun updateHistory(entry: HistoryEntry) = Unit

        override suspend fun deleteHistory(id: Long) = Unit

        override suspend fun getTotalExpenseForProfile(profileId: Long): Double = 30.0
    }
}
