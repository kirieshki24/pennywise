package com.example.pennywise.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pennywise.ui.edit.EditEntryViewModel
import com.example.pennywise.ui.home.HomeViewModel
import com.example.pennywise.ui.profiles.ProfilesViewModel
import com.example.pennywise.ui.rates.RatesViewModel

class AppViewModelFactory(
    private val container: AppContainer,
    private val entryId: Long? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(container.financeRepository) as T
            }
            modelClass.isAssignableFrom(ProfilesViewModel::class.java) -> {
                ProfilesViewModel(container.financeRepository) as T
            }
            modelClass.isAssignableFrom(EditEntryViewModel::class.java) -> {
                EditEntryViewModel(
                    financeRepository = container.financeRepository,
                    entryId = entryId
                ) as T
            }
            modelClass.isAssignableFrom(RatesViewModel::class.java) -> {
                RatesViewModel(container.currencyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
