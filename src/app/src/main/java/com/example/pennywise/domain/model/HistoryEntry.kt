package com.example.pennywise.domain.model

data class HistoryEntry(
    val id: Long,
    val profileId: Long,
    val amount: Double,
    val note: String,
    val timestamp: Long,
    val type: TransactionType
)
