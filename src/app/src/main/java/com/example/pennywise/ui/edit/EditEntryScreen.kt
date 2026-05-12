package com.example.pennywise.ui.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pennywise.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEntryScreen(
    viewModel: EditEntryViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EditEntryEvent.Saved -> onSaved()
            }
        }
    }

    var profilesExpanded by remember { mutableStateOf(false) }
    val selectedProfile = uiState.profiles.firstOrNull { it.id == uiState.selectedProfileId }
    val amountValue = uiState.amountInput.replace(",", ".").toDoubleOrNull()
    val canSave = amountValue != null && amountValue > 0 && uiState.selectedProfileId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Transaction") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.amountInput,
                onValueChange = viewModel::onAmountChange,
                label = { Text(text = "Amount") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("entry_amount_input")
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.noteInput,
                onValueChange = viewModel::onNoteChange,
                label = { Text(text = "Note") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("entry_note_input")
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box {
                OutlinedTextField(
                    value = selectedProfile?.name ?: "Select profile",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = "Profile") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { profilesExpanded = true }
                        .testTag("entry_profile_dropdown")
                )
                DropdownMenu(
                    expanded = profilesExpanded,
                    onDismissRequest = { profilesExpanded = false }
                ) {
                    uiState.profiles.forEach { profile ->
                        DropdownMenuItem(
                            text = { Text(text = profile.name) },
                            onClick = {
                                profilesExpanded = false
                                viewModel.onProfileSelected(profile.id)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Type", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.type == TransactionType.EXPENSE,
                    onClick = { viewModel.onTypeSelected(TransactionType.EXPENSE) },
                    label = { Text(text = "Expense") }
                )
                FilterChip(
                    selected = uiState.type == TransactionType.INCOME,
                    onClick = { viewModel.onTypeSelected(TransactionType.INCOME) },
                    label = { Text(text = "Income") }
                )
            }

            val errorMessage = uiState.errorMessage
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = viewModel::saveEntry,
                enabled = canSave && !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("entry_save_button")
            ) {
                Text(text = if (uiState.isSaving) "Saving..." else "Save")
            }
        }
    }

    val confirmLimit = uiState.confirmLimitExceededBy
    if (confirmLimit != null) {
        AlertDialog(
            onDismissRequest = { viewModel.confirmLimitExceeded(false) },
            title = { Text(text = "Limit exceeded") },
            text = {
                Text(text = "Limit exceeded by ${formatAmount(confirmLimit)}. Save anyway?")
            },
            confirmButton = {
                Button(onClick = { viewModel.confirmLimitExceeded(true) }) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.confirmLimitExceeded(false) }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

private fun formatAmount(value: Double): String {
    return String.format(java.util.Locale.US, "%.2f", value)
}
