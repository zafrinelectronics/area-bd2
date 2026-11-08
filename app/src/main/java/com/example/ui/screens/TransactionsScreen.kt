package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Expense
import com.example.data.model.Sale
import com.example.data.model.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class TransactionRow(
    val id: String,
    val time: Long,
    val kind: String,
    val title: String,
    val detail: String,
    val amount: Double,
    val paid: Double? = null,
    val due: Double? = null
)

private fun transactionMoney(value: Double): String = "৳" + NumberFormat.getNumberInstance(Locale("en", "IN")).format(value)

@Composable
fun TransactionsScreen(sales: List<Sale>, expenses: List<Expense>) {
    var selected by remember { mutableStateOf("সব") }
    val rows = remember(sales, expenses) {
        buildList {
            sales.forEach { sale ->
                add(TransactionRow("sale-${sale.id}", sale.soldAt, "বিক্রয়", sale.customerName.ifBlank { "বিক্রয়" }, "ইনভয়েস: ${sale.invoiceNumber} • ${sale.productDetails}", sale.salePrice, sale.paidAmount, sale.dueAmount))
            }
            expenses.forEach { expense ->
                val isIncome = expense.type == TransactionType.INCOME.name
                add(TransactionRow("entry-${expense.id}", expense.dateMillis, if (isIncome) "আয়" else "খরচ", expense.category, expense.note.ifBlank { expense.paymentMethod }, expense.amount))
            }
        }.sortedByDescending { it.time }
    }
    val visible = when (selected) {
        "বিক্রয়" -> rows.filter { it.kind == "বিক্রয়" }
        "আয়" -> rows.filter { it.kind == "আয়" }
        "খরচ" -> rows.filter { it.kind == "খরচ" }
        else -> rows
    }
    val salesTotal = sales.sumOf { it.salePrice }
    val incomeTotal = expenses.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }
    val expenseTotal = expenses.filter { it.type != TransactionType.INCOME.name }.sumOf { it.amount }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("লেনদেনের সারসংক্ষেপ", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("মোট বিক্রয়")
                    Text(transactionMoney(salesTotal), fontWeight = FontWeight.SemiBold)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("অন্যান্য আয়")
                    Text(transactionMoney(incomeTotal), fontWeight = FontWeight.SemiBold)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("খরচ")
                    Text(transactionMoney(expenseTotal), fontWeight = FontWeight.SemiBold)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("বিক্রয় + অন্যান্য আয় − খরচ", fontWeight = FontWeight.Bold)
                    Text(transactionMoney(salesTotal + incomeTotal - expenseTotal), fontWeight = FontWeight.Bold)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("সব", "বিক্রয়", "আয়", "খরচ").forEach { filter ->
                FilterChip(selected = selected == filter, onClick = { selected = filter }, label = { Text(filter) })
            }
        }
        Text("লেনদেনের তালিকা (${visible.size})", fontWeight = FontWeight.SemiBold)
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(visible, key = { it.id }) { row ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${row.kind}: ${row.title}", fontWeight = FontWeight.SemiBold)
                            Text(transactionMoney(row.amount), fontWeight = FontWeight.Bold)
                        }
                        Text(row.detail, style = MaterialTheme.typography.bodySmall)
                        if (row.paid != null) Text("পরিশোধ: ${transactionMoney(row.paid)} • বকেয়া: ${transactionMoney(row.due ?: 0.0)}", style = MaterialTheme.typography.bodySmall)
                        Text(SimpleDateFormat("dd-MM-yyyy  hh:mm a", Locale.getDefault()).format(Date(row.time)), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            if (visible.isEmpty()) item { Text("এই বিভাগে কোনো লেনদেন পাওয়া যায়নি।", modifier = Modifier.padding(12.dp)) }
        }
    }
}
