package com.example.pennywise

import com.example.pennywise.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionTypeTest {
    @Test
    fun fromStorageReturnsMatchingType() {
        assertEquals(TransactionType.INCOME, TransactionType.fromStorage("INCOME"))
        assertEquals(TransactionType.EXPENSE, TransactionType.fromStorage("EXPENSE"))
    }

    @Test
    fun fromStorageDefaultsToExpense() {
        assertEquals(TransactionType.EXPENSE, TransactionType.fromStorage("UNKNOWN"))
    }
}
