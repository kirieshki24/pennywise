package com.example.pennywise.ui

import android.content.Context
import androidx.room.Room
import com.example.pennywise.data.local.AppDatabase
import com.example.pennywise.data.mock.MockCurrencyService
import com.example.pennywise.data.repository.CurrencyRepository
import com.example.pennywise.data.repository.CurrencyRepositoryImpl
import com.example.pennywise.data.repository.FinanceRepository
import com.example.pennywise.data.repository.FinanceRepositoryImpl

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "pennywise.db"
    ).build()

    val financeRepository: FinanceRepository = FinanceRepositoryImpl(
        profileDao = database.profileDao(),
        historyDao = database.historyDao()
    )

    val currencyRepository: CurrencyRepository = CurrencyRepositoryImpl(
        service = MockCurrencyService()
    )
}
