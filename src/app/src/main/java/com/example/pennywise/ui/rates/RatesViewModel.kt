package com.example.pennywise.ui.rates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennywise.data.repository.CurrencyRepository
import com.example.pennywise.domain.model.CurrencyRate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RatesUiState(
    val date: String = "",
    val rates: List<CurrencyRate> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RatesViewModel(
    private val repository: CurrencyRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RatesUiState())
    val uiState: StateFlow<RatesUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                delay(300)
                val result = repository.fetchRates()
                _uiState.update {
                    it.copy(
                        date = result.date,
                        rates = result.rates,
                        isLoading = false
                    )
                }
            } catch (ex: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load rates."
                    )
                }
            }
        }
    }
}
