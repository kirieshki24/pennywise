package com.example.pennywise.data.repository

import com.example.pennywise.domain.model.CurrencyRates

interface CurrencyRepository {
    suspend fun fetchRates(): CurrencyRates
}
