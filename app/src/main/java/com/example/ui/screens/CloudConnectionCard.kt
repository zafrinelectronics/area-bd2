package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CloudConnectionCard(
    accountEmail: String?,
    isBusy: Boolean,
    message: String?,
    pairingCode: String?,
    onCreatePairingCode: () -> Unit,
    onJoinShop: (String) -> Unit,
    onSignIn: (String, String) -> Unit,
    onSignOut: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var joinCode by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Firebase Cloud Sync", style = MaterialTheme.typography.titleMedium)
            Text(
                if (accountEmail.isNullOrBlank()) "Automatic cloud connection is starting. No Firebase account is required."
                else if (accountEmail == "Automatic cloud sync") "Connected automatically. No email or password required."
                else "Connected account: $accountEmail",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (accountEmail.isNullOrBlank()) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Firebase account email") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Firebase account password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                Button(
                    onClick = { onSignIn(email.trim(), password) },
                    enabled = !isBusy && email.isNotBlank() && password.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isBusy) "Connecting..." else "Connect Firebase Account")
                }
            } else if (accountEmail != "Automatic cloud sync") {
                OutlinedButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth()) {
                    Text("Disconnect Firebase Account")
                }
            }

            Text("Multi-device Shop Pairing", style = MaterialTheme.typography.titleSmall)
            Text(
                "Create a one-time code on the shop owner's device. Enter that code on another device to connect it to the same shop data.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onCreatePairingCode,
                enabled = !isBusy && !accountEmail.isNullOrBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isBusy) "Please wait..." else "Create Pairing Code (Shop Owner)")
            }
            if (!pairingCode.isNullOrBlank()) {
                Text("Share this code with the other device. It expires in 10 minutes and works once.")
                Text(pairingCode, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            OutlinedTextField(
                value = joinCode,
                onValueChange = { joinCode = it.uppercase().filter { ch -> ch.isLetterOrDigit() || ch == '-' }.take(19) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter Pairing Code") },
                placeholder = { Text("XXXX-XXXX-XXXX-XXXX") },
                singleLine = true
            )
            OutlinedButton(
                onClick = { onJoinShop(joinCode) },
                enabled = !isBusy && joinCode.count { it.isLetterOrDigit() } == 16,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Join Existing Shop")
            }

            if (!message.isNullOrBlank()) {
                Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            Text(
                "Products, sales, expenses, employees, salary payments and shop settings are saved to Firebase automatically. Staff login accounts and passwords stay on this device.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
