package com.example.pennywise.ui.rates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennywise.data.repository.CurrencyRepository
import com.example.pennywise.domain.model.CurrencyRate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ConverterField {
    FROM,
    TO
}

data class RatesUiState(
    val date: String = "",
    val rates: List<CurrencyRate> = emptyList(),
    val fromCode: String = "RUB",
    val toCode: String = "USD",
    val fromAmount: String = "",
    val toAmount: String = "",
    val lastEdited: ConverterField = ConverterField.FROM,
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
                val result = repository.fetchRates()
                _uiState.update { state ->
                    val safeFrom = pickCurrency(state.fromCode, result.rates)
                    val safeTo = pickCurrency(state.toCode, result.rates, fallback = "USD")
                    val updated = state.copy(
                        date = result.date,
                        rates = result.rates,
                        fromCode = safeFrom,
                        toCode = safeTo,
                        isLoading = false
                    )
                    recalculate(updated)
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

    fun onFromAmountChange(value: String) {
        _uiState.update { state ->
            recalculate(state.copy(fromAmount = value, lastEdited = ConverterField.FROM))
        }
    }

    fun onToAmountChange(value: String) {
        _uiState.update { state ->
            recalculate(state.copy(toAmount = value, lastEdited = ConverterField.TO))
        }
    }

    fun onFromCurrencyChange(code: String) {
        _uiState.update { state ->
            recalculate(state.copy(fromCode = code))
        }
    }

    fun onToCurrencyChange(code: String) {
        _uiState.update { state ->
            recalculate(state.copy(toCode = code))
        }
    }

    private fun recalculate(state: RatesUiState): RatesUiState {
        if (state.rates.isEmpty()) return state
        return when (state.lastEdited) {
            ConverterField.FROM -> {
                val amount = parseAmount(state.fromAmount)
                if (amount == null) {
                    state.copy(toAmount = "")
                } else {
                    val converted = convert(
                        amount = amount,
                        fromCode = state.fromCode,
                        toCode = state.toCode,
                        rates = state.rates
                    )
                    state.copy(toAmount = formatAmount(converted))
                }
            }
            ConverterField.TO -> {
                val amount = parseAmount(state.toAmount)
                if (amount == null) {
                    state.copy(fromAmount = "")
                } else {
                    val converted = convert(
                        amount = amount,
                        fromCode = state.toCode,
                        toCode = state.fromCode,
                        rates = state.rates
                    )
                    state.copy(fromAmount = formatAmount(converted))
                }
            }
        }
    }

    private fun pickCurrency(
        preferred: String,
        rates: List<CurrencyRate>,
        fallback: String = "RUB"
    ): String {
        return when {
            rates.any { it.charCode == preferred } -> preferred
            rates.any { it.charCode == fallback } -> fallback
            rates.isNotEmpty() -> rates.first().charCode
            else -> preferred
        }
    }

    private fun convert(
        amount: Double,
        fromCode: String,
        toCode: String,
        rates: List<CurrencyRate>
    ): Double {
        val fromRate = rateFor(fromCode, rates)
        val toRate = rateFor(toCode, rates)
        if (fromRate == 0.0 || toRate == 0.0) return 0.0
        return amount * (fromRate / toRate)
    }

    private fun rateFor(code: String, rates: List<CurrencyRate>): Double {
        return rates.firstOrNull { it.charCode == code }?.unitRate ?: 0.0
    }

    private fun parseAmount(raw: String): Double? {
        if (raw.isBlank()) return null
        return raw.replace(",", ".").toDoubleOrNull()
    }

    private fun formatAmount(value: Double): String {
        return String.format(java.util.Locale.US, "%.4f", value)
    }
}
