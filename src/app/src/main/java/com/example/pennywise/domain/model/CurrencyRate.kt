package com.example.pennywise.domain.model

data class CurrencyRate(
    val charCode: String,
    val name: String,
    val nominal: Int,
    val value: Double,
    val unitRate: Double
)

data class CurrencyRates(
    val date: String,
    val rates: List<CurrencyRate>
)
