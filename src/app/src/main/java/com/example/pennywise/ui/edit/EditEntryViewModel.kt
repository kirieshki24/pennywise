package com.example.pennywise.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennywise.data.repository.FinanceRepository
import com.example.pennywise.domain.model.HistoryEntry
import com.example.pennywise.domain.model.Profile
import com.example.pennywise.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface EditEntryEvent {
    object Saved : EditEntryEvent
}

data class EditEntryUiState(
    val profiles: List<Profile> = emptyList(),
    val amountInput: String = "",
    val noteInput: String = "",
    val selectedProfileId: Long? = null,
    val type: TransactionType = TransactionType.EXPENSE,
    val timestamp: Long = System.currentTimeMillis(),
    val confirmLimitExceededBy: Double? = null,
    val errorMessage: String? = null,
    val isSaving: Boolean = false
)

class EditEntryViewModel(
    private val financeRepository: FinanceRepository,
    private val entryId: Long?
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditEntryUiState())
    val uiState: StateFlow<EditEntryUiState> = _uiState.asStateFlow()

    val events = MutableSharedFlow<EditEntryEvent>()

    private var originalEntry: HistoryEntry? = null
    private var pendingEntry: HistoryEntry? = null

    init {
        viewModelScope.launch {
            financeRepository.observeProfiles().collect { profiles ->
                _uiState.update { state ->
                    val selectedId = state.selectedProfileId ?: profiles.firstOrNull()?.id
                    state.copy(profiles = profiles, selectedProfileId = selectedId)
                }
            }
        }

        if (entryId != null && entryId >= 0) {
            viewModelScope.launch {
                val entry = financeRepository.getHistoryById(entryId) ?: return@launch
                originalEntry = entry
                _uiState.update { state ->
                    state.copy(
                        amountInput = entry.amount.toString(),
                        noteInput = entry.note,
                        selectedProfileId = entry.profileId,
                        type = entry.type,
                        timestamp = entry.timestamp
                    )
                }
            }
        }
    }

    fun onAmountChange(value: String) {
        _uiState.update { it.copy(amountInput = value, errorMessage = null) }
    }

    fun onNoteChange(value: String) {
        _uiState.update { it.copy(noteInput = value, errorMessage = null) }
    }

    fun onProfileSelected(profileId: Long) {
        _uiState.update { it.copy(selectedProfileId = profileId, errorMessage = null) }
    }

    fun onTypeSelected(type: TransactionType) {
        _uiState.update { it.copy(type = type) }
    }

    fun saveEntry() {
        val amount = parseAmount(uiState.value.amountInput)
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(errorMessage = "Enter a valid amount.") }
            return
        }
        val profileId = uiState.value.selectedProfileId
        if (profileId == null) {
            _uiState.update { it.copy(errorMessage = "Select a profile first.") }
            return
        }

        viewModelScope.launch {
            val entry = HistoryEntry(
                id = entryId ?: 0,
                profileId = profileId,
                amount = amount,
                note = uiState.value.noteInput.trim(),
                timestamp = uiState.value.timestamp,
                type = uiState.value.type
            )

            if (entry.type == TransactionType.EXPENSE) {
                val profile = financeRepository.getProfileById(profileId)
                if (profile != null && !profile.isUnlimited) {
                    val totalExpense = financeRepository.getTotalExpenseForProfile(profileId)
                    val adjustedTotal = totalExpense - expenseDeltaForEdit(profileId)
                    val projectedTotal = adjustedTotal + amount
                    if (projectedTotal > profile.monthlyLimit) {
                        pendingEntry = entry
                        _uiState.update {
                            it.copy(
                                confirmLimitExceededBy = projectedTotal - profile.monthlyLimit
                            )
                        }
                        return@launch
                    }
                }
            }

            saveEntryInternal(entry)
        }
    }

    fun confirmLimitExceeded(shouldProceed: Boolean) {
        val entry = pendingEntry
        pendingEntry = null
        _uiState.update { it.copy(confirmLimitExceededBy = null) }
        if (!shouldProceed || entry == null) return
        viewModelScope.launch {
            saveEntryInternal(entry)
        }
    }

    private suspend fun saveEntryInternal(entry: HistoryEntry) {
        _uiState.update { it.copy(isSaving = true) }
        if (entryId == null || entryId < 0) {
            financeRepository.addHistory(entry)
        } else {
            financeRepository.updateHistory(entry)
        }
        _uiState.update { it.copy(isSaving = false) }
        events.emit(EditEntryEvent.Saved)
    }

    private fun expenseDeltaForEdit(profileId: Long): Double {
        val original = originalEntry ?: return 0.0
        return if (original.type == TransactionType.EXPENSE && original.profileId == profileId) {
            original.amount
        } else {
            0.0
        }
    }

    private fun parseAmount(raw: String): Double? {
        return raw.replace(",", ".").toDoubleOrNull()
    }
}
