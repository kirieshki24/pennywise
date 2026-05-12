package com.example.pennywise.ui.rates

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pennywise.domain.model.CurrencyRate
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatesScreen(
    viewModel: RatesViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Currency rates") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = viewModel::refresh) { Text(text = "Refresh") }
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
            if (uiState.date.isNotBlank()) {
                Text(
                    text = "Rates date: ${uiState.date}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (uiState.isLoading) {
                Text(text = "Loading...", style = MaterialTheme.typography.bodyMedium)
                return@Column
            }
            val errorMessage = uiState.errorMessage
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                return@Column
            }

            ConverterPanel(
                rates = uiState.rates,
                fromCode = uiState.fromCode,
                toCode = uiState.toCode,
                fromAmount = uiState.fromAmount,
                toAmount = uiState.toAmount,
                onFromAmountChange = viewModel::onFromAmountChange,
                onToAmountChange = viewModel::onToAmountChange,
                onFromCurrencyChange = viewModel::onFromCurrencyChange,
                onToCurrencyChange = viewModel::onToCurrencyChange
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "All rates", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.rates, key = { it.charCode }) { rate ->
                    RateCard(rate)
                }
            }
        }
    }
}

@Composable
private fun ConverterPanel(
    rates: List<CurrencyRate>,
    fromCode: String,
    toCode: String,
    fromAmount: String,
    toAmount: String,
    onFromAmountChange: (String) -> Unit,
    onToAmountChange: (String) -> Unit,
    onFromCurrencyChange: (String) -> Unit,
    onToCurrencyChange: (String) -> Unit
) {
    if (rates.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CurrencyInput(
            label = "From",
            amount = fromAmount,
            onAmountChange = onFromAmountChange,
            selectedCode = fromCode,
            rates = rates,
            onCodeSelected = onFromCurrencyChange
        )
        CurrencyInput(
            label = "To",
            amount = toAmount,
            onAmountChange = onToAmountChange,
            selectedCode = toCode,
            rates = rates,
            onCodeSelected = onToCurrencyChange
        )
    }
}

@Composable
private fun CurrencyInput(
    label: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    selectedCode: String,
    rates: List<CurrencyRate>,
    onCodeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            label = { Text(text = "Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = selectedCode,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = "Currency") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp)
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            rates.forEach { rate ->
                DropdownMenuItem(
                    text = { Text(text = "${rate.charCode} · ${rate.name}") },
                    onClick = {
                        expanded = false
                        onCodeSelected(rate.charCode)
                    }
                )
            }
        }
    }
}

@Composable
private fun RateCard(rate: CurrencyRate) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${rate.charCode} · ${rate.name}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Nominal: ${rate.nominal}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatAmount(rate.value),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Unit: ${formatAmount(rate.unitRate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatAmount(value: Double): String {
    return String.format(Locale.US, "%.4f", value)
}
