package com.example.pennywise.data.repository

import com.example.pennywise.data.mock.MockCurrencyService
import com.example.pennywise.domain.model.CurrencyRates
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrencyRepositoryImpl(
    private val service: MockCurrencyService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CurrencyRepository {
    override suspend fun fetchRates(): CurrencyRates {
        return withContext(ioDispatcher) {
            service.fetchRates()
        }
    }
}
