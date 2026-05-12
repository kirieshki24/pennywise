package com.example.pennywise.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennywise.data.repository.FinanceRepository
import com.example.pennywise.domain.model.HistoryEntry
import com.example.pennywise.domain.model.Profile
import com.example.pennywise.domain.model.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val profiles: List<Profile> = emptyList(),
    val history: List<HistoryEntry> = emptyList(),
    val profileNameById: Map<Long, String> = emptyMap(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0
)

class HomeViewModel(
    private val repository: FinanceRepository
) : ViewModel() {
    private val profilesFlow = repository.observeProfiles().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    private val historyFlow = repository.observeHistory().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val uiState: StateFlow<HomeUiState> = combine(
        profilesFlow,
        historyFlow
    ) { profiles, history ->
        val income = history
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        val expense = history
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        HomeUiState(
            profiles = profiles,
            history = history,
            profileNameById = profiles.associate { it.id to it.name },
            totalIncome = income,
            totalExpense = expense,
            balance = income - expense
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HomeUiState()
    )

    fun deleteHistory(id: Long) {
        viewModelScope.launch {
            repository.deleteHistory(id)
        }
    }
}
