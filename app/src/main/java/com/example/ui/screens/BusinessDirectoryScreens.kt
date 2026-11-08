package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Product
import com.example.data.model.Sale
import java.text.NumberFormat
import java.util.Locale

private fun taka(value: Double): String = "৳" + NumberFormat.getNumberInstance(Locale("en", "IN")).format(value)

@Composable
fun PurchaseScreen(products: List<Product>, onAddPurchase: () -> Unit) {
    val purchases = products.sortedByDescending { it.createdAt }
    val total = purchases.sumOf { it.purchasePrice }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("ক্রয় ব্যবস্থাপনা", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("মোট পণ্য: ${purchases.size}")
            Text("ক্রয়মূল্য: ${taka(total)}", fontWeight = FontWeight.Bold)
        }
        Button(onClick = onAddPurchase, modifier = Modifier.fillMaxWidth()) { Text("＋ নতুন ক্রয় / পণ্য যোগ") }
        Text("নিচের তালিকাটি বর্তমান স্টকের পণ্যের সংরক্ষিত ক্রয়মূল্য থেকে তৈরি।", style = MaterialTheme.typography.bodySmall)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(purchases, key = { it.id }) { product ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${product.company} ${product.model} ${product.variant}".trim(), fontWeight = FontWeight.SemiBold)
                        Text("সাপ্লায়ার: ${product.supplier.ifBlank { "উল্লেখ নেই" }}")
                        Text("ক্রয়মূল্য: ${taka(product.purchasePrice)}")
                        Text("স্ট্যাটাস: ${product.status}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomersScreen(sales: List<Sale>) {
    val customers = sales.filter { it.customerName.isNotBlank() }
        .groupBy { it.customerPhone.ifBlank { it.customerName.trim().lowercase() } }
        .values.map { it.sortedByDescending(Sale::soldAt).first() }
        .sortedBy { it.customerName.lowercase() }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("কাস্টমার তালিকা", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("বিক্রির ইনভয়েসে সংরক্ষিত ক্রেতাদের তথ্য", style = MaterialTheme.typography.bodySmall)
        Text("মোট কাস্টমার: ${customers.size}", fontWeight = FontWeight.SemiBold)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(customers) { customer ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(customer.customerName, fontWeight = FontWeight.SemiBold)
                        Text("মোবাইল: ${customer.customerPhone.ifBlank { "নেই" }}")
                        Text("ঠিকানা: ${customer.customerAddress.ifBlank { "নেই" }}")
                    }
                }
            }
        }
    }
}

@Composable
fun SuppliersScreen(products: List<Product>) {
    val suppliers = products.filter { it.supplier.isNotBlank() }.groupBy { it.supplier.trim() }.toList().sortedBy { it.first.lowercase() }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("সাপ্লায়ার তালিকা", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("পণ্যের তথ্য থেকে সংগৃহীত সরবরাহকারীর নাম", style = MaterialTheme.typography.bodySmall)
        Text("মোট সাপ্লায়ার: ${suppliers.size}", fontWeight = FontWeight.SemiBold)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(suppliers, key = { it.first }) { (name, items) ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(name, fontWeight = FontWeight.SemiBold)
                        Text("পণ্যের সংখ্যা: ${items.size}")
                        Text("মোট ক্রয়মূল্য: ${taka(items.sumOf { it.purchasePrice })}")
                    }
                }
            }
        }
    }
}

private data class LocalDeliveryAgency(val id: Long, val name: String, val phone: String)
private data class LocalDeliveryOrder(val id: Long, val invoice: String, val customer: String, val phone: String, val agencyId: Long, val tracking: String, val status: String)

@Composable
fun DeliveryAgencyScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("apple_area_delivery", android.content.Context.MODE_PRIVATE) }
    val agencies = remember { mutableStateListOf<LocalDeliveryAgency>() }
    val orders = remember { mutableStateListOf<LocalDeliveryOrder>() }
    var showAgency by remember { mutableStateOf(false) }
    var showOrder by remember { mutableStateOf(false) }
    var agencyName by remember { mutableStateOf("") }
    var agencyPhone by remember { mutableStateOf("") }
    var invoice by remember { mutableStateOf("") }
    var customer by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var tracking by remember { mutableStateOf("") }
    var selectedAgency by remember { mutableStateOf(0L) }
    var editingAgencyId by remember { mutableStateOf(0L) }

    fun persist() {
        val a = JSONArray(); agencies.forEach { a.put(JSONObject().put("id", it.id).put("name", it.name).put("phone", it.phone)) }
        val o = JSONArray(); orders.forEach { o.put(JSONObject().put("id", it.id).put("invoice", it.invoice).put("customer", it.customer).put("phone", it.phone).put("agencyId", it.agencyId).put("tracking", it.tracking).put("status", it.status)) }
        prefs.edit().putString("agencies", a.toString()).putString("orders", o.toString()).apply()
    }
    LaunchedEffect(Unit) {
        runCatching {
            val a = JSONArray(prefs.getString("agencies", "[]")); for (i in 0 until a.length()) { val x = a.getJSONObject(i); agencies.add(LocalDeliveryAgency(x.getLong("id"), x.getString("name"), x.getString("phone"))) }
            val o = JSONArray(prefs.getString("orders", "[]")); for (i in 0 until o.length()) { val x = o.getJSONObject(i); orders.add(LocalDeliveryOrder(x.getLong("id"), x.getString("invoice"), x.getString("customer"), x.getString("phone"), x.getLong("agencyId"), x.getString("tracking"), x.getString("status"))) }
        }
    }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("ডেলিভারি ব্যবস্থাপনা", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { agencyName = ""; agencyPhone = ""; showAgency = true }, modifier = Modifier.weight(1f)) { Text("＋ এজেন্সি") }
            Button(onClick = { showOrder = true }, enabled = agencies.isNotEmpty(), modifier = Modifier.weight(1f)) { Text("＋ অর্ডার") }
        }
        Text("এজেন্সি: ${agencies.size}   |   ডেলিভারি অর্ডার: ${orders.size}", fontWeight = FontWeight.SemiBold)
        Text("এজেন্সি তালিকা", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (agencies.isEmpty()) Text("এখনো কোনো এজেন্সি যোগ করা হয়নি।")
        agencies.forEach { agency ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(agency.name, fontWeight = FontWeight.SemiBold); Text("ফোন: ${agency.phone.ifBlank { "উল্লেখ নেই" }}")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { editingAgencyId = agency.id; agencyName = agency.name; agencyPhone = agency.phone; showAgency = true }) { Text("সম্পাদনা") }
                        TextButton(onClick = { agencies.remove(agency); orders.removeAll { it.agencyId == agency.id }; persist() }) { Text("মুছুন") }
                    }
                }
            }
        }
        Text("অর্ডার ট্র্যাকিং", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (orders.isEmpty()) Text("এখনো কোনো ডেলিভারি অর্ডার নেই।")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(orders, key = { it.id }) { order ->
                val agency = agencies.firstOrNull { it.id == order.agencyId }
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("ইনভয়েস: ${order.invoice.ifBlank { "নেই" }}", fontWeight = FontWeight.SemiBold)
                        Text("ক্রেতা: ${order.customer} (${order.phone})")
                        Text("এজেন্সি: ${agency?.name ?: "মুছে ফেলা হয়েছে"}  |  ট্র্যাকিং: ${order.tracking.ifBlank { "নেই" }}")
                        Text("অবস্থা: ${order.status}", fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { val next = listOf("অপেক্ষমাণ", "সংগ্রহ হয়েছে", "পথে আছে", "ডেলিভারি সম্পন্ন", "ফেরত"); val index = next.indexOf(order.status); val updated = order.copy(status = next[(index + 1).mod(next.size)]); val i = orders.indexOfFirst { it.id == order.id }; if (i >= 0) orders[i] = updated; persist() }) { Text("স্ট্যাটাস পরিবর্তন") }
                            TextButton(onClick = { orders.remove(order); persist() }) { Text("মুছুন") }
                        }
                    }
                }
            }
        }
    }
    if (showAgency) AlertDialog(onDismissRequest = { showAgency = false }, title = { Text(if (editingAgencyId == 0L) "এজেন্সি যোগ করুন" else "এজেন্সি সম্পাদনা") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(agencyName, { agencyName = it }, label = { Text("এজেন্সির নাম") }, singleLine = true)
            OutlinedTextField(agencyPhone, { agencyPhone = it }, label = { Text("ফোন নম্বর") }, singleLine = true)
        }
    }, confirmButton = { TextButton(onClick = { if (agencyName.isNotBlank()) { val updated = LocalDeliveryAgency(if (editingAgencyId == 0L) System.currentTimeMillis() else editingAgencyId, agencyName.trim(), agencyPhone.trim()); val index = agencies.indexOfFirst { it.id == editingAgencyId }; if (index >= 0) agencies[index] = updated else agencies.add(updated); persist(); editingAgencyId = 0L; showAgency = false } }) { Text("সংরক্ষণ") } }, dismissButton = { TextButton(onClick = { editingAgencyId = 0L; showAgency = false }) { Text("বাতিল") } })
    if (showOrder) AlertDialog(onDismissRequest = { showOrder = false }, title = { Text("ডেলিভারি অর্ডার যোগ") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(invoice, { invoice = it }, label = { Text("ইনভয়েস নম্বর") }, singleLine = true)
            OutlinedTextField(customer, { customer = it }, label = { Text("ক্রেতার নাম") }, singleLine = true)
            OutlinedTextField(customerPhone, { customerPhone = it }, label = { Text("ক্রেতার ফোন") }, singleLine = true)
            OutlinedTextField(tracking, { tracking = it }, label = { Text("ট্র্যাকিং নম্বর") }, singleLine = true)
            Text("এজেন্সি: ${agencies.firstOrNull { it.id == selectedAgency }?.name ?: agencies.first().name}")
            TextButton(onClick = { val idx = agencies.indexOfFirst { it.id == selectedAgency }; selectedAgency = agencies[(idx + 1).mod(agencies.size)].id }) { Text("পরের এজেন্সি নির্বাচন") }
        }
    }, confirmButton = { TextButton(onClick = { if (customer.isNotBlank()) { val agencyId = if (selectedAgency == 0L || agencies.none { it.id == selectedAgency }) agencies.first().id else selectedAgency; orders.add(LocalDeliveryOrder(System.currentTimeMillis(), invoice.trim(), customer.trim(), customerPhone.trim(), agencyId, tracking.trim(), "অপেক্ষমাণ")); persist(); invoice = ""; customer = ""; customerPhone = ""; tracking = ""; showOrder = false } }) { Text("সংরক্ষণ") } }, dismissButton = { TextButton(onClick = { showOrder = false }) { Text("বাতিল") } })
}
