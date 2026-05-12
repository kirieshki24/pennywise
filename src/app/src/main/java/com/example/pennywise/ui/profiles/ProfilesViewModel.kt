package com.example.pennywise.ui.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennywise.data.repository.FinanceRepository
import com.example.pennywise.domain.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfilesUiState(
    val profiles: List<Profile> = emptyList(),
    val nameInput: String = "",
    val limitInput: String = "",
    val errorMessage: String? = null
)

class ProfilesViewModel(
    private val repository: FinanceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfilesUiState())
    val uiState: StateFlow<ProfilesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeProfiles().collect { profiles ->
                _uiState.update { it.copy(profiles = profiles) }
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(nameInput = value, errorMessage = null) }
    }

    fun onLimitChange(value: String) {
        _uiState.update { it.copy(limitInput = value, errorMessage = null) }
    }

    fun addProfile() {
        val name = uiState.value.nameInput.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Profile name is required.") }
            return
        }
        val limit = parseAmount(uiState.value.limitInput)
        if (limit == null || limit < 0) {
            _uiState.update { it.copy(errorMessage = "Monthly limit must be a number.") }
            return
        }

        viewModelScope.launch {
            repository.addProfile(name = name, monthlyLimit = limit)
            _uiState.update {
                it.copy(
                    nameInput = "",
                    limitInput = "",
                    errorMessage = null
                )
            }
        }
    }

    fun deleteProfile(id: Long) {
        viewModelScope.launch {
            repository.deleteProfile(id)
        }
    }

    private fun parseAmount(raw: String): Double? {
        if (raw.isBlank()) return 0.0
        return raw.replace(",", ".").toDoubleOrNull()
    }
}
