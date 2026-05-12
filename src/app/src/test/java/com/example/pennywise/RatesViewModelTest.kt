package com.example.pennywise

import com.example.pennywise.data.repository.CurrencyRepository
import com.example.pennywise.domain.model.CurrencyRate
import com.example.pennywise.domain.model.CurrencyRates
import com.example.pennywise.ui.rates.RatesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RatesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun convertsBetweenCurrencies() = runTest {
        val repository = FakeCurrencyRepository(
            CurrencyRates(
                date = "13.05.2026",
                rates = listOf(
                    CurrencyRate("RUB", "Russian Ruble", 1, 1.0, 1.0),
                    CurrencyRate("USD", "US Dollar", 1, 100.0, 100.0),
                    CurrencyRate("EUR", "Euro", 1, 120.0, 120.0)
                )
            )
        )
        val viewModel = RatesViewModel(repository)

        advanceUntilIdle()
        viewModel.onFromCurrencyChange("RUB")
        viewModel.onToCurrencyChange("USD")
        viewModel.onFromAmountChange("100")
        advanceUntilIdle()

        assertEquals("1.0000", viewModel.uiState.value.toAmount)
    }

    private class FakeCurrencyRepository(
        private val data: CurrencyRates
    ) : CurrencyRepository {
        override suspend fun fetchRates(): CurrencyRates = data
    }
}
