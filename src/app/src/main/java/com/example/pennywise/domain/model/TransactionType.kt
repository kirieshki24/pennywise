package com.example.pennywise.domain.model

enum class TransactionType(val storageValue: String) {
    INCOME("INCOME"),
    EXPENSE("EXPENSE");

    companion object {
        fun fromStorage(value: String): TransactionType {
            return entries.firstOrNull { it.storageValue == value } ?: EXPENSE
        }
    }
}
