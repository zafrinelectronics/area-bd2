package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.AppTab

private data class MenuEntry(val title: String, val subtitle: String, val target: AppTab?)

@Composable
fun MoreScreen(onNavigate: (AppTab) -> Unit, onLogout: () -> Unit) {
    val entries = listOf(
        MenuEntry("বিক্রয় (Sales)", "বিক্রির তালিকা ও ইনভয়েস", AppTab.SALES),
        MenuEntry("ক্রয় (Purchase)", "পণ্যের ক্রয়মূল্য ও ক্রয় তালিকা", AppTab.PURCHASE),
        MenuEntry("পণ্য (Product)", "পণ্য যোগ, সম্পাদনা ও স্টক", AppTab.STOCK),
        MenuEntry("কাস্টমার (Customers)", "বিক্রির রেকর্ডে থাকা ক্রেতার তথ্য", AppTab.CUSTOMERS),
        MenuEntry("সাপ্লায়ার (Suppliers)", "পণ্যের সরবরাহকারীর তালিকা", AppTab.SUPPLIERS),
        MenuEntry("লেনদেন (Transactions)", "বিক্রয়, আয় ও ব্যয়ের একত্রিত তালিকা", AppTab.TRANSACTIONS),
        MenuEntry("রিপোর্ট (Reports)", "বিক্রি ও লাভের প্রতিবেদন", AppTab.REPORTS),
        MenuEntry("কর্মচারী (Employees)", "কর্মচারী ও বেতন", AppTab.EMPLOYEES),
        MenuEntry("HRM", "কর্মচারী ব্যবস্থাপনা ও বেতন", AppTab.EMPLOYEES),
        MenuEntry("ডেলিভারি এজেন্সি", "ডেলিভারি এজেন্সির তথ্য ও বর্তমান অবস্থা", AppTab.DELIVERY),
        MenuEntry("সেটিংস", "দোকান ও অ্যাপের সেটিংস", AppTab.SETTINGS)
    )
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("আরও অপশন", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("নিচের অপশনগুলো থেকে অ্যাপের বিভাগ খুলুন", style = MaterialTheme.typography.bodyMedium)
        }
        items(entries) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    entry.target?.let(onNavigate)
                },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(entry.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(entry.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(if (entry.target == null) "শীঘ্রই" else "খুলুন", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onLogout)) {
                Text("লগআউট", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
        }
    }
}
