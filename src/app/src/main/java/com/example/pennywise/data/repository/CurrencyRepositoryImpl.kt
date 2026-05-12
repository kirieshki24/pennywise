package com.example.pennywise.data.repository

import com.example.pennywise.data.network.CbrCurrencyService
import com.example.pennywise.domain.model.CurrencyRates
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrencyRepositoryImpl(
    private val service: CbrCurrencyService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CurrencyRepository {
    override suspend fun fetchRates(): CurrencyRates {
        return withContext(ioDispatcher) {
            service.fetchRates()
        }
    }
}
