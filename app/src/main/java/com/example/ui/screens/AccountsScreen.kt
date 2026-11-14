package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ShopSettings
import com.example.ui.theme.AppleBlue

@Composable
fun AccountsScreen(
    settings: ShopSettings,
    onSave: (ShopSettings) -> Unit
) {
    var bankName by remember { mutableStateOf("") }
    var accountName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("Accounts", fontWeight = FontWeight.Bold) }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = AppleBlue)
                            Text("  Add Bank Account", fontWeight = FontWeight.Bold)
                        }
                        OutlinedTextField(value = bankName, onValueChange = { bankName = it }, label = { Text("Bank Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = accountName, onValueChange = { accountName = it }, label = { Text("Account Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = accountNumber, onValueChange = { accountNumber = it }, label = { Text("Account Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        Button(
                            onClick = {
                                val entry = listOf(bankName, accountName, accountNumber).joinToString(" | ") { it.trim().replace("|", "") }
                                if (bankName.isNotBlank() && accountName.isNotBlank() && accountNumber.isNotBlank() && !settings.bankAccounts.contains(entry)) {
                                    onSave(settings.copy(bankAccounts = settings.bankAccounts + entry))
                                    bankName = ""; accountName = ""; accountNumber = ""
                                }
                            },
                            enabled = bankName.isNotBlank() && accountName.isNotBlank() && accountNumber.isNotBlank(),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Save Account") }
                    }
                }
            }
            item { Text("Saved Bank Accounts", fontWeight = FontWeight.Bold) }
            if (settings.bankAccounts.isEmpty()) {
                item { Text("No bank accounts saved yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            items(settings.bankAccounts) { account ->
                Card(shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(account, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        IconButton(onClick = { onSave(settings.copy(bankAccounts = settings.bankAccounts - account)) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete account", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
