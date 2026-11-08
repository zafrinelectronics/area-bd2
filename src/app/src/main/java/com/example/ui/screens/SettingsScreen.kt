package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.ui.theme.parseColorHex
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppUser
import com.example.data.model.ShopSettings
import com.example.ui.LocalAppStrings
import com.example.ui.theme.AppleBlue
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    currentSettings: ShopSettings,
    onSaveSettings: (ShopSettings) -> Unit,
    onResetDefaults: () -> Unit,
    onClearAllData: () -> Unit = {},
    onLoadDemoData: () -> Unit = {},
    onOpenGitHubSync: () -> Unit = {},
    lastSyncTime: Long = 0L,
    currentUser: AppUser? = null,
    onOpenUserManagement: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val strings = LocalAppStrings.current
    val isEnglish = strings.tabDashboard == "Dashboard"

    // Text scale state (default 0.85f for small/compact interface as requested)
    var textScale by remember(currentSettings.textScale) { mutableFloatStateOf(currentSettings.textScale) }

    // Preset options management state (Company, Color, Supplier, Variant, Battery, Box, Payment)
    var selectedPresetTab by remember { mutableStateOf("company") }
    var showAddPresetDialog by remember { mutableStateOf(false) }
    var newPresetText by remember { mutableStateOf("") }
    var editingPresetIndex by remember { mutableStateOf<Int?>(null) }
    var editingPresetText by remember { mutableStateOf("") }
    var presetToDelete by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var showResetCategoryDialog by remember { mutableStateOf(false) }

    // Shop Details state
    var shopName by remember(currentSettings.shopName) { mutableStateOf(currentSettings.shopName) }
    var shopTagline by remember(currentSettings.shopTagline) { mutableStateOf(currentSettings.shopTagline) }
    var shopPhone by remember(currentSettings.shopPhone) { mutableStateOf(currentSettings.shopPhone) }
    var shopAddress by remember(currentSettings.shopAddress) { mutableStateOf(currentSettings.shopAddress) }
    var shopInfoSaved by remember { mutableStateOf(false) }

    // Warranty & Cash Memo terms state
    var warrantyReplacement by remember(currentSettings.warrantyReplacement) { mutableStateOf(currentSettings.warrantyReplacement) }
    var warrantyService by remember(currentSettings.warrantyService) { mutableStateOf(currentSettings.warrantyService) }
    var warrantyExchangeDeduction by remember(currentSettings.warrantyExchangeDeduction) { mutableStateOf(currentSettings.warrantyExchangeDeduction) }
    var memoFooterNote by remember(currentSettings.memoFooterNote) { mutableStateOf(currentSettings.memoFooterNote) }
    var warrantyInfoSaved by remember { mutableStateOf(false) }

    // Custom Tabs state
    var tabDashboard by remember(currentSettings.customTabDashboard) { mutableStateOf(currentSettings.customTabDashboard) }
    var tabStock by remember(currentSettings.customTabStock) { mutableStateOf(currentSettings.customTabStock) }
    var tabExpenses by remember(currentSettings.customTabExpenses) { mutableStateOf(currentSettings.customTabExpenses) }
    var tabEmployees by remember(currentSettings.customTabEmployees) { mutableStateOf(currentSettings.customTabEmployees) }
    var tabSales by remember(currentSettings.customTabSales) { mutableStateOf(currentSettings.customTabSales) }
    var tabSettings by remember(currentSettings.customTabSettings) { mutableStateOf(currentSettings.customTabSettings) }
    var showExpensesTab by remember(currentSettings.showExpensesTab) { mutableStateOf(currentSettings.showExpensesTab) }
    var showEmployeesTab by remember(currentSettings.showEmployeesTab) { mutableStateOf(currentSettings.showEmployeesTab) }
    var tabsSaved by remember { mutableStateOf(false) }

    // 1. Add Preset Dialog
    if (showAddPresetDialog) {
        val catName = when (selectedPresetTab) {
            "company" -> if (isEnglish) "Company Brand" else "কোম্পানির নাম (Company)"
            "color" -> if (isEnglish) "Device Color" else "কালার (Color)"
            "supplier" -> if (isEnglish) "Supplier / Source" else "সাপ্লায়ার (Supplier)"
            "variant" -> if (isEnglish) "Storage Variant" else "ভ্যারিয়েন্ট (Storage)"
            "battery" -> if (isEnglish) "Battery Health" else "ব্যাটারি হেলথ"
            "box" -> if (isEnglish) "Box Condition" else "বক্সের অবস্থা"
            "payment" -> if (isEnglish) "Payment Method" else "পেমেন্ট মাধ্যম"
            else -> ""
        }
        AlertDialog(
            modifier = Modifier.imePadding(),
            onDismissRequest = {
                showAddPresetDialog = false
                newPresetText = ""
            },
            title = {
                Text(
                    text = if (isEnglish) "Add New $catName" else "নতুন $catName যোগ করুন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isEnglish) "Enter option name to add to dropdowns:" else "ড্রপডাউন ও বাটনে যুক্ত করার জন্য নাম লিখুন:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newPresetText,
                        onValueChange = { newPresetText = it },
                        placeholder = { Text(if (isEnglish) "e.g. Titanium Blue..." else "যেমন: নতুন নাম লিখুন...") },
                        modifier = Modifier.fillMaxWidth().testTag("input_new_preset"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = newPresetText.trim()
                        if (trimmed.isNotBlank()) {
                            when (selectedPresetTab) {
                                "company" -> if (!currentSettings.companyPresets.contains(trimmed)) {
                                    onSaveSettings(currentSettings.copy(companyPresets = currentSettings.companyPresets + trimmed))
                                }
                                "color" -> if (!currentSettings.colorPresets.contains(trimmed)) {
                                    onSaveSettings(currentSettings.copy(colorPresets = currentSettings.colorPresets + trimmed))
                                }
                                "supplier" -> if (!currentSettings.supplierPresets.contains(trimmed)) {
                                    onSaveSettings(currentSettings.copy(supplierPresets = currentSettings.supplierPresets + trimmed))
                                }
                                "variant" -> if (!currentSettings.variantPresets.contains(trimmed)) {
                                    onSaveSettings(currentSettings.copy(variantPresets = currentSettings.variantPresets + trimmed))
                                }
                                "battery" -> if (!currentSettings.batteryPresets.contains(trimmed)) {
                                    onSaveSettings(currentSettings.copy(batteryPresets = currentSettings.batteryPresets + trimmed))
                                }
                                "box" -> if (!currentSettings.boxConditionPresets.contains(trimmed)) {
                                    onSaveSettings(currentSettings.copy(boxConditionPresets = currentSettings.boxConditionPresets + trimmed))
                                }
                                "payment" -> if (!currentSettings.paymentMethodPresets.contains(trimmed)) {
                                    onSaveSettings(currentSettings.copy(paymentMethodPresets = currentSettings.paymentMethodPresets + trimmed))
                                }
                            }
                        }
                        showAddPresetDialog = false
                        newPresetText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue)
                ) {
                    Text(if (isEnglish) "Add Option" else "যোগ করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showAddPresetDialog = false
                    newPresetText = ""
                }) {
                    Text(if (isEnglish) "Cancel" else "বাতিল")
                }
            }
        )
    }

    // 2. Edit Preset Dialog
    editingPresetIndex?.let { index ->
        val catName = when (selectedPresetTab) {
            "company" -> if (isEnglish) "Company Brand" else "কোম্পানির নাম"
            "color" -> if (isEnglish) "Device Color" else "কালার"
            "supplier" -> if (isEnglish) "Supplier / Source" else "সাপ্লায়ার"
            "variant" -> if (isEnglish) "Storage Variant" else "ভ্যারিয়েন্ট"
            "battery" -> if (isEnglish) "Battery Health" else "ব্যাটারি হেলথ"
            "box" -> if (isEnglish) "Box Condition" else "বক্সের অবস্থা"
            "payment" -> if (isEnglish) "Payment Method" else "পেমেন্ট মাধ্যম"
            else -> ""
        }
        AlertDialog(
            modifier = Modifier.imePadding(),
            onDismissRequest = { editingPresetIndex = null; editingPresetText = "" },
            title = {
                Text(
                    text = if (isEnglish) "Edit $catName" else "$catName এডিট করুন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isEnglish) "Update option name:" else "অপশনের নতুন নাম লিখুন:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = editingPresetText,
                        onValueChange = { editingPresetText = it },
                        modifier = Modifier.fillMaxWidth().testTag("input_edit_preset"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = editingPresetText.trim()
                        if (trimmed.isNotBlank()) {
                            when (selectedPresetTab) {
                                "company" -> if (index in currentSettings.companyPresets.indices) {
                                    val updated = currentSettings.companyPresets.toMutableList().also { it[index] = trimmed }
                                    onSaveSettings(currentSettings.copy(companyPresets = updated))
                                }
                                "color" -> if (index in currentSettings.colorPresets.indices) {
                                    val updated = currentSettings.colorPresets.toMutableList().also { it[index] = trimmed }
                                    onSaveSettings(currentSettings.copy(colorPresets = updated))
                                }
                                "supplier" -> if (index in currentSettings.supplierPresets.indices) {
                                    val updated = currentSettings.supplierPresets.toMutableList().also { it[index] = trimmed }
                                    onSaveSettings(currentSettings.copy(supplierPresets = updated))
                                }
                                "variant" -> if (index in currentSettings.variantPresets.indices) {
                                    val updated = currentSettings.variantPresets.toMutableList().also { it[index] = trimmed }
                                    onSaveSettings(currentSettings.copy(variantPresets = updated))
                                }
                                "battery" -> if (index in currentSettings.batteryPresets.indices) {
                                    val updated = currentSettings.batteryPresets.toMutableList().also { it[index] = trimmed }
                                    onSaveSettings(currentSettings.copy(batteryPresets = updated))
                                }
                                "box" -> if (index in currentSettings.boxConditionPresets.indices) {
                                    val updated = currentSettings.boxConditionPresets.toMutableList().also { it[index] = trimmed }
                                    onSaveSettings(currentSettings.copy(boxConditionPresets = updated))
                                }
                                "payment" -> if (index in currentSettings.paymentMethodPresets.indices) {
                                    val updated = currentSettings.paymentMethodPresets.toMutableList().also { it[index] = trimmed }
                                    onSaveSettings(currentSettings.copy(paymentMethodPresets = updated))
                                }
                            }
                        }
                        editingPresetIndex = null
                        editingPresetText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue)
                ) {
                    Text(if (isEnglish) "Save Changes" else "সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { editingPresetIndex = null; editingPresetText = "" }) {
                    Text(if (isEnglish) "Cancel" else "বাতিল")
                }
            }
        )
    }

    // 3. Delete Preset Dialog
    presetToDelete?.let { (index, name) ->
        val catName = when (selectedPresetTab) {
            "company" -> if (isEnglish) "Company" else "কোম্পানি"
            "color" -> if (isEnglish) "Color" else "কালার"
            "supplier" -> if (isEnglish) "Supplier" else "সাপ্লায়ার"
            "variant" -> if (isEnglish) "Variant" else "ভ্যারিয়েন্ট"
            "battery" -> if (isEnglish) "Battery" else "ব্যাটারি হেলথ"
            "box" -> if (isEnglish) "Box Condition" else "বক্সের অবস্থা"
            "payment" -> if (isEnglish) "Payment" else "পেমেন্ট মাধ্যম"
            else -> ""
        }
        AlertDialog(
            onDismissRequest = { presetToDelete = null },
            title = {
                Text(
                    text = if (isEnglish) "Delete Option" else "অপশন ডিলিট",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = if (isEnglish) "Are you sure you want to delete \"$name\" from $catName options?" else "আপনি কি নিশ্চিতভাবে $catName তালিকা থেকে \"$name\" অপশনটি ডিলিট করতে চান?",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (selectedPresetTab) {
                            "company" -> if (index in currentSettings.companyPresets.indices) {
                                val updated = currentSettings.companyPresets.toMutableList().also { it.removeAt(index) }
                                onSaveSettings(currentSettings.copy(companyPresets = updated))
                            }
                            "color" -> if (index in currentSettings.colorPresets.indices) {
                                val updated = currentSettings.colorPresets.toMutableList().also { it.removeAt(index) }
                                onSaveSettings(currentSettings.copy(colorPresets = updated))
                            }
                            "supplier" -> if (index in currentSettings.supplierPresets.indices) {
                                val updated = currentSettings.supplierPresets.toMutableList().also { it.removeAt(index) }
                                onSaveSettings(currentSettings.copy(supplierPresets = updated))
                            }
                            "variant" -> if (index in currentSettings.variantPresets.indices) {
                                val updated = currentSettings.variantPresets.toMutableList().also { it.removeAt(index) }
                                onSaveSettings(currentSettings.copy(variantPresets = updated))
                            }
                            "battery" -> if (index in currentSettings.batteryPresets.indices) {
                                val updated = currentSettings.batteryPresets.toMutableList().also { it.removeAt(index) }
                                onSaveSettings(currentSettings.copy(batteryPresets = updated))
                            }
                            "box" -> if (index in currentSettings.boxConditionPresets.indices) {
                                val updated = currentSettings.boxConditionPresets.toMutableList().also { it.removeAt(index) }
                                onSaveSettings(currentSettings.copy(boxConditionPresets = updated))
                            }
                            "payment" -> if (index in currentSettings.paymentMethodPresets.indices) {
                                val updated = currentSettings.paymentMethodPresets.toMutableList().also { it.removeAt(index) }
                                onSaveSettings(currentSettings.copy(paymentMethodPresets = updated))
                            }
                        }
                        presetToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text(if (isEnglish) "Delete" else "ডিলিট করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { presetToDelete = null }) {
                    Text(if (isEnglish) "Cancel" else "বাতিল")
                }
            }
        )
    }

    // 4. Reset Category Dialog
    if (showResetCategoryDialog) {
        val catName = when (selectedPresetTab) {
            "company" -> if (isEnglish) "Companies" else "কোম্পানি"
            "color" -> if (isEnglish) "Colors" else "কালার"
            "supplier" -> if (isEnglish) "Suppliers" else "সাপ্লায়ার"
            "variant" -> if (isEnglish) "Storage Variants" else "ভ্যারিয়েন্ট"
            "battery" -> if (isEnglish) "Battery Health" else "ব্যাটারি হেলথ"
            "box" -> if (isEnglish) "Box Conditions" else "বক্সের অবস্থা"
            "payment" -> if (isEnglish) "Payment Methods" else "পেমেন্ট মাধ্যম"
            else -> ""
        }
        AlertDialog(
            onDismissRequest = { showResetCategoryDialog = false },
            title = {
                Text(
                    text = if (isEnglish) "Reset to Defaults" else "ডিফল্ট রিস্টোর",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = if (isEnglish) "Restore the initial preset list for $catName?" else "আপনি কি $catName এর মূল ডিফল্ট অপশনগুলো ফিরিয়ে আনতে চান?",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (selectedPresetTab) {
                            "company" -> onSaveSettings(currentSettings.copy(companyPresets = listOf("Apple", "Samsung", "Google", "Xiaomi", "OnePlus", "Vivo", "Oppo", "Realme")))
                            "color" -> onSaveSettings(currentSettings.copy(colorPresets = listOf("Natural Titanium", "Desert Titanium", "Black Titanium", "White Titanium", "Deep Purple", "Midnight", "Starlight", "Blue", "Space Gray")))
                            "supplier" -> onSaveSettings(currentSettings.copy(supplierPresets = listOf("Dubai Stock", "USA LL/A", "Singapore ZP/A", "Japan J/A", "Local Dealer", "Customer Exchange")))
                            "variant" -> onSaveSettings(currentSettings.copy(variantPresets = listOf("64GB", "128GB", "256GB", "512GB", "1TB")))
                            "battery" -> onSaveSettings(currentSettings.copy(batteryPresets = listOf("100% (Brand New)", "98%", "95%", "92%", "89%", "85%")))
                            "box" -> onSaveSettings(currentSettings.copy(boxConditionPresets = listOf("সিল প্যাক (Sealed Box)", "বক্স সহ (Original Box)", "আইএমইআই ম্যাচিং বক্স", "বক্স ছাড়া (Device Only)", "চার্জার সহ")))
                            "payment" -> onSaveSettings(currentSettings.copy(paymentMethodPresets = listOf("ক্যাশ (Cash)", "বিকাশ (bKash)", "নগদ (Nagad)", "ব্যাংক কার্ড", "ক্যাশ + বিকাশ")))
                        }
                        showResetCategoryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue)
                ) {
                    Text(if (isEnglish) "Reset" else "রিস্টোর করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetCategoryDialog = false }) {
                    Text(if (isEnglish) "Cancel" else "বাতিল")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Screen Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.shop_logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .height(46.dp)
                                .width(76.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.settingsTitle,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isEnglish) "Text Size & Interface Scale" else "অ্যাপের লেখার সাইজ ছোট করার সেটিংস",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(AppleBlue.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.FormatSize, contentDescription = null, tint = AppleBlue)
                    }
                }
            }
        }

        // Account & Security Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AppleBlue.copy(alpha = 0.12f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (currentUser?.isAdmin == true) Icons.Default.Shield else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = AppleBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = currentUser?.fullName ?: "CONOCK",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "লগইন: ${currentUser?.username ?: "CONOCK"} (${if (currentUser?.isAdmin == true) "এডমিন" else "স্টাফ"})",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (currentUser?.isAdmin == true) {
                            Button(
                                onClick = onOpenUserManagement,
                                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("ইউজার কন্ট্রোল", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = onLogout,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f))
                        ) {
                            Text("লগআউট", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Language Selection Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color(0xFF8B5CF6).copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = Color(0xFF8B5CF6),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "App Language" else "অ্যাপের ভাষা পরিবর্তন",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isEnglish) "Choose Bengali or English" else "বাংলা অথবা ইংরেজি ভাষা নির্বাচন করুন",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val isBn = currentSettings.appLanguage.equals("BN", ignoreCase = true)
                        FilterChip(
                            selected = isBn,
                            onClick = {
                                onSaveSettings(currentSettings.copy(appLanguage = "BN"))
                            },
                            label = {
                                Text("🇧🇩 বাংলা (Bengali)", fontSize = 12.sp, fontWeight = if (isBn) FontWeight.Bold else FontWeight.Medium)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF047857),
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("lang_chip_bn")
                        )

                        val isEn = currentSettings.appLanguage.equals("EN", ignoreCase = true)
                        FilterChip(
                            selected = isEn,
                            onClick = {
                                onSaveSettings(currentSettings.copy(appLanguage = "EN"))
                            },
                            label = {
                                Text("🇺🇸 English (ইংরেজি)", fontSize = 12.sp, fontWeight = if (isEn) FontWeight.Bold else FontWeight.Medium)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AppleBlue,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("lang_chip_en")
                        )
                    }
                }
            }
        }

        // PRIMARY DEDICATED SECTION: App Text Size Adjustment
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(AppleBlue.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TextFields,
                                    contentDescription = null,
                                    tint = AppleBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "App Interface Text Size" else "অ্যাপের লেখার সাইজ (Text Size)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isEnglish) "Make app text smaller or larger" else "অ্যাপের সকল টেক্সট ছোট করার অপশন",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Current Scale Percentage Badge
                        val percent = (textScale * 100).roundToInt()
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AppleBlue.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "$percent%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AppleBlue,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // 1. App Interface Theme Color Swatches
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEnglish) "App Interface Theme Color:" else "অ্যাপ ইন্টারফেসের রঙ (Theme Color):",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val activeColorName = when (currentSettings.themePrimaryColorHex.uppercase()) {
                                "#7C3AED" -> if (isEnglish) "Royal Purple" else "রাজকীয় বেগুনি"
                                "#059669" -> if (isEnglish) "Emerald Green" else "পান্না সবুজ"
                                "#E11D48" -> if (isEnglish) "Crimson Red" else "রুবি লাল"
                                "#D97706" -> if (isEnglish) "Amber Gold" else "সানসেট অ্যাম্বার"
                                "#0891B2" -> if (isEnglish) "Deep Cyan" else "ওশান টিল"
                                "#334155" -> if (isEnglish) "Slate Gray" else "মার্জিত স্লেট"
                                "#C026D3" -> if (isEnglish) "Rose Magenta" else "ম্যাজেন্টা পিংক"
                                else -> if (isEnglish) "Apple Blue" else "অ্যাপল ব্লু"
                            }
                            Text(
                                text = activeColorName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        val themeColorSwatches = listOf(
                            Triple("#0071E3", "অ্যাপল ব্লু", Color(0xFF0071E3)),
                            Triple("#7C3AED", "বেগুনি", Color(0xFF7C3AED)),
                            Triple("#059669", "সবুজ", Color(0xFF059669)),
                            Triple("#E11D48", "লাল", Color(0xFFE11D48)),
                            Triple("#D97706", "অ্যাম্বার", Color(0xFFD97706)),
                            Triple("#0891B2", "টিল", Color(0xFF0891B2)),
                            Triple("#334155", "স্লেট", Color(0xFF334155)),
                            Triple("#C026D3", "ম্যাজেন্টা", Color(0xFFC026D3))
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            themeColorSwatches.forEach { (hex, name, colorVal) ->
                                val isSelected = currentSettings.themePrimaryColorHex.equals(hex, ignoreCase = true)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .clickable {
                                            onSaveSettings(currentSettings.copy(themePrimaryColorHex = hex))
                                        }
                                        .testTag("color_swatch_$hex")
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(colorVal, CircleShape)
                                            .border(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = name,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text(
                        text = if (isEnglish) "Quick Presets:" else "দ্রুত সাইজ নির্বাচন করুন:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Preset Chips (Focused on smaller text sizes)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val presets = listOf(
                            Triple(0.75f, "অতি ছোট (75%)", "Extra Small (75%)"),
                            Triple(0.85f, "ছোট (85%)", "Small (85%)"),
                            Triple(0.92f, "কম্প্যাক্ট (92%)", "Compact (92%)"),
                            Triple(1.00f, "স্বাভাবিক (100%)", "Normal (100%)")
                        )

                        presets.forEach { (presetVal, bnLabel, enLabel) ->
                            val isSelected = (textScale - presetVal) in -0.02f..0.02f
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    textScale = presetVal
                                    onSaveSettings(currentSettings.copy(textScale = presetVal))
                                },
                                label = {
                                    Text(
                                        text = if (isEnglish) enLabel else bnLabel,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AppleBlue,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("text_scale_preset_${(presetVal * 100).toInt()}")
                            )
                        }
                    }

                    // Interactive Slider for Fine-Tuning
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isEnglish) "Fine-tune size:" else "স্লাইডার দিয়ে সাইজ নিয়ন্ত্রণ করুন:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val label = when {
                                textScale <= 0.78f -> if (isEnglish) "Extra Small" else "অতি ছোট"
                                textScale <= 0.88f -> if (isEnglish) "Small (Recommended)" else "ছোট (সুপারিশকৃত)"
                                textScale <= 0.96f -> if (isEnglish) "Compact" else "কম্প্যাক্ট"
                                else -> if (isEnglish) "Normal" else "স্বাভাবিক"
                            }
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppleBlue
                            )
                        }

                        Slider(
                            value = textScale,
                            onValueChange = { textScale = it },
                            onValueChangeFinished = {
                                onSaveSettings(currentSettings.copy(textScale = textScale))
                            },
                            valueRange = 0.70f..1.15f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = AppleBlue,
                                activeTrackColor = AppleBlue
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("text_scale_slider")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("৭০% (খুব ছোট)", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            Text("১০০% (স্বাভাবিক)", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            Text("১১৫% (বড়)", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }

                    // Live Interactive Preview Box
                    Text(
                        text = if (isEnglish) "Live Text Preview:" else "লাইভ টেক্সট প্রিভিউ (কেমন দেখাবে):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Apple Area Shop",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleBlue
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "স্টকে আছে",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF047857),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "iPhone 15 Pro Max (256GB, Natural Titanium)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "ক্রয়মূল্য: ৳ ১,৩৮,০০০",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "বিক্রয়মূল্য: ৳ ১,৪৮,০০০",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )
                            }

                            Text(
                                text = "IMEI: 354891098234125  |  ব্যাটারি: ১০০%  |  সিল প্যাক",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "• লেখার সাইজ ছোট করলে এক স্ক্রিনেই অনেক বেশি তথ্য পরিষ্কারভাবে দেখা যায়।",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    // Save / Apply Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                textScale = 0.85f
                                onSaveSettings(currentSettings.copy(textScale = 0.85f))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("reset_text_scale_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isEnglish) "Default (85%)" else "ডিফল্ট (৮৫%)", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onSaveSettings(currentSettings.copy(textScale = textScale))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("apply_text_scale_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isEnglish) "Apply Size" else "প্রয়োগ করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // SHOP DETAILS SECTION:
        // দোকানের নাম, ট্যাগলাইন, হটলাইন নাম্বার ও ঠিকানা এডিট
        // -------------------------------------------------------------
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shop_details_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "Shop Name & Information" else "দোকানের নাম ও তথ্য এডিট",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isEnglish) "Cash memo, invoice, and header branding" else "ক্যাশ মেমো, ইনভয়েস ও হেডারে প্রদর্শিত হবে",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Shop Name
                    OutlinedTextField(
                        value = shopName,
                        onValueChange = {
                            shopName = it
                            shopInfoSaved = false
                        },
                        label = { Text(if (isEnglish) "Shop Name *" else "দোকানের নাম *", fontSize = 12.sp) },
                        placeholder = { Text("Apple Area Shop", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("shop_name_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Shop Tagline
                    OutlinedTextField(
                        value = shopTagline,
                        onValueChange = {
                            shopTagline = it
                            shopInfoSaved = false
                        },
                        label = { Text(if (isEnglish) "Tagline / Slogan" else "ট্যাগলাইন / স্লোগান", fontSize = 12.sp) },
                        placeholder = { Text("Premium Gadget & Apple Specialist", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("shop_tagline_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Shop Phone / Hotline
                    OutlinedTextField(
                        value = shopPhone,
                        onValueChange = {
                            shopPhone = it
                            shopInfoSaved = false
                        },
                        label = { Text(if (isEnglish) "Hotline / Phone Number" else "হটলাইন / মোবাইল নাম্বার", fontSize = 12.sp) },
                        placeholder = { Text("+880 1888-000000", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("shop_phone_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Shop Address
                    OutlinedTextField(
                        value = shopAddress,
                        onValueChange = {
                            shopAddress = it
                            shopInfoSaved = false
                        },
                        label = { Text(if (isEnglish) "Full Shop Address" else "দোকানের সম্পূর্ণ ঠিকানা", fontSize = 12.sp) },
                        placeholder = { Text("Level 4, Block C, Shop #42, Jamuna Future Park, Dhaka", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("shop_address_input"),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2,
                        maxLines = 3
                    )

                    if (shopInfoSaved) {
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEnglish) "Shop details saved successfully!" else "দোকানের তথ্য সফলভাবে সংরক্ষিত হয়েছে!",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF065F46),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Action Buttons: Reset & Save
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                shopName = "Apple Area Shop"
                                shopTagline = "Premium Gadget & Apple Specialist"
                                shopPhone = "+880 1888-000000"
                                shopAddress = "Level 4, Block C, Jamuna Future Park, Dhaka"
                                onSaveSettings(
                                    currentSettings.copy(
                                        shopName = shopName,
                                        shopTagline = shopTagline,
                                        shopPhone = shopPhone,
                                        shopAddress = shopAddress
                                    )
                                )
                                shopInfoSaved = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("reset_shop_info_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isEnglish) "Reset" else "রিসেট", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onSaveSettings(
                                    currentSettings.copy(
                                        shopName = shopName.trim().ifEmpty { "Apple Area Shop" },
                                        shopTagline = shopTagline.trim(),
                                        shopPhone = shopPhone.trim(),
                                        shopAddress = shopAddress.trim()
                                    )
                                )
                                shopInfoSaved = true
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("save_shop_info_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isEnglish) "Save Details" else "সংরক্ষণ করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // CASH MEMO & WARRANTY CARD TERMS SECTION:
        // রিপ্লেসমেন্ট গ্যারান্টি, সার্ভিস ওয়ারেন্টি, এক্সচেঞ্জ কর্তন, মেমো ফুটনোট
        // -------------------------------------------------------------
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("warranty_memo_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF8B5CF6).copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = Color(0xFF7C3AED),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "Cash Memo & Warranty Card Terms" else "ক্যাশ মেমো ও ওয়ারেন্টি কার্ড শর্তাবলী",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isEnglish) "Warranty rules and memo footer notes" else "ওয়ারেন্টি পলিসি, এক্সচেঞ্জ নিয়ম ও মেমো ফুটনোট এডিট",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Replacement Guarantee
                    OutlinedTextField(
                        value = warrantyReplacement,
                        onValueChange = {
                            warrantyReplacement = it
                            warrantyInfoSaved = false
                        },
                        label = { Text(if (isEnglish) "Replacement Guarantee Policy" else "১. রিপ্লেসমেন্ট গ্যারান্টি শর্ত", fontSize = 12.sp) },
                        placeholder = { Text("৭ দিন রিপ্লেসমেন্ট গ্যারান্টি (ডিসপ্লে ও পানি ব্যতীত)", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("warranty_replacement_input"),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )

                    // Service Warranty
                    OutlinedTextField(
                        value = warrantyService,
                        onValueChange = {
                            warrantyService = it
                            warrantyInfoSaved = false
                        },
                        label = { Text(if (isEnglish) "Service Warranty Policy" else "২. সার্ভিস ওয়ারেন্টি শর্ত", fontSize = 12.sp) },
                        placeholder = { Text("২ বছর ফ্রি সার্ভিস ওয়ারেন্টি (পার্টস ব্যতীত)", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("warranty_service_input"),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )

                    // Exchange Deduction
                    OutlinedTextField(
                        value = warrantyExchangeDeduction,
                        onValueChange = {
                            warrantyExchangeDeduction = it
                            warrantyInfoSaved = false
                        },
                        label = { Text(if (isEnglish) "Exchange & Return Deduction Policy" else "৩. এক্সচেঞ্জ ও মূল্য কর্তন পলিসি", fontSize = 12.sp) },
                        placeholder = { Text("১৫ দিন পর পণ্য ফেরত বা এক্সচেঞ্জে ২০% কর্তন করা হবে", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("warranty_deduction_input"),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )

                    // Memo Footer Note
                    OutlinedTextField(
                        value = memoFooterNote,
                        onValueChange = {
                            memoFooterNote = it
                            warrantyInfoSaved = false
                        },
                        label = { Text(if (isEnglish) "Memo Footer / Disclaimer Note" else "৪. মেমো ফুটনোট বা বিশেষ বার্তা", fontSize = 12.sp) },
                        placeholder = { Text("বিক্রিত পণ্য ফেরতযোগ্য নয়। মেমো ও বক্স ব্যতীত কোনো ওয়ারেন্টি দাবি কার্যকর হবে না।", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("memo_footer_note_input"),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )

                    // Live Memo Preview Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isEnglish) "📄 Live Cash Memo Preview:" else "📄 মেমোতে যেভাবে শর্তাবলী প্রিন্ট হবে:",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "• $warrantyReplacement",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "• $warrantyService",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "• $warrantyExchangeDeduction",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (memoFooterNote.isNotBlank()) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 2.dp))
                                Text(
                                    text = "নোট: $memoFooterNote",
                                    fontSize = 10.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (warrantyInfoSaved) {
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEnglish) "Warranty & memo terms saved successfully!" else "মেমো ও ওয়ারেন্টির শর্তাবলী সংরক্ষিত হয়েছে!",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF065F46),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Action Buttons: Reset & Save
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                warrantyReplacement = "৭ দিন রিপ্লেসমেন্ট গ্যারান্টি (ডিসপ্লে ও পানি ব্যতীত)"
                                warrantyService = "২ বছর ফ্রি সার্ভিস ওয়ারেন্টি (পার্টস ব্যতীত)"
                                warrantyExchangeDeduction = "১৫ দিন পর পণ্য ফেরত বা এক্সচেঞ্জে ২০% কর্তন করা হবে"
                                memoFooterNote = "বিক্রিত পণ্য ফেরতযোগ্য নয়। মেমো ও বক্স ব্যতীত কোনো ওয়ারেন্টি দাবি কার্যকর হবে না। আমাদের সাথে থাকার জন্য ধন্যবাদ।"
                                onSaveSettings(
                                    currentSettings.copy(
                                        warrantyReplacement = warrantyReplacement,
                                        warrantyService = warrantyService,
                                        warrantyExchangeDeduction = warrantyExchangeDeduction,
                                        memoFooterNote = memoFooterNote
                                    )
                                )
                                warrantyInfoSaved = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("reset_warranty_info_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isEnglish) "Reset" else "রিসেট", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onSaveSettings(
                                    currentSettings.copy(
                                        warrantyReplacement = warrantyReplacement.trim(),
                                        warrantyService = warrantyService.trim(),
                                        warrantyExchangeDeduction = warrantyExchangeDeduction.trim(),
                                        memoFooterNote = memoFooterNote.trim()
                                    )
                                )
                                warrantyInfoSaved = true
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("save_warranty_info_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isEnglish) "Save Terms" else "সংরক্ষণ করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // CUSTOM TABS SECTION:
        // নিচের বিভিন্ন ট্যাবগুলোর নাম পরিবর্তন ও প্রদর্শন নিয়ন্ত্রণ
        // -------------------------------------------------------------
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_tabs_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF10B981).copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tab,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "Bottom Navigation Tabs Editor" else "অ্যাপের বিভিন্ন ট্যাব এডিট ও কন্ট্রোল",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isEnglish) "Rename tabs & customize visibility" else "ট্যাবের নাম পরিবর্তন ও চালু/বন্ধ করার অপশন",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text(
                        text = if (isEnglish) "Tab Names (Leave empty to use default localized label):" else "ট্যাবের নামসমূহ (খালি রাখলে স্বয়ংক্রিয় ডিফল্ট নাম দেখাবে):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // 1. Dashboard Tab Name
                    OutlinedTextField(
                        value = tabDashboard,
                        onValueChange = {
                            tabDashboard = it
                            tabsSaved = false
                        },
                        label = { Text(if (isEnglish) "1. Dashboard Tab Name" else "১. ড্যাশবোর্ড ট্যাব নাম", fontSize = 12.sp) },
                        placeholder = { Text(if (isEnglish) "Dashboard" else "ড্যাশবোর্ড", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tab_name_dashboard_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 2. Stock Tab Name
                    OutlinedTextField(
                        value = tabStock,
                        onValueChange = {
                            tabStock = it
                            tabsSaved = false
                        },
                        label = { Text(if (isEnglish) "2. Stock Inventory Tab Name" else "২. স্টক ইনভেন্টরি ট্যাব নাম", fontSize = 12.sp) },
                        placeholder = { Text(if (isEnglish) "Stock" else "স্টক", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tab_name_stock_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 3. Sales Tab Name
                    OutlinedTextField(
                        value = tabSales,
                        onValueChange = {
                            tabSales = it
                            tabsSaved = false
                        },
                        label = { Text(if (isEnglish) "3. Sales Tab Name" else "৩. সেলস ট্যাব নাম", fontSize = 12.sp) },
                        placeholder = { Text(if (isEnglish) "Sales" else "সেলস", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tab_name_sales_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 4. Expenses Tab Name
                    OutlinedTextField(
                        value = tabExpenses,
                        onValueChange = {
                            tabExpenses = it
                            tabsSaved = false
                        },
                        label = { Text(if (isEnglish) "4. Expenses Tab Name" else "৪. খরচ (Expense) ট্যাব নাম", fontSize = 12.sp) },
                        placeholder = { Text(if (isEnglish) "Expenses" else "খরচ", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tab_name_expenses_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 5. Employees Tab Name
                    OutlinedTextField(
                        value = tabEmployees,
                        onValueChange = {
                            tabEmployees = it
                            tabsSaved = false
                        },
                        label = { Text(if (isEnglish) "5. Employees Tab Name" else "৫. কর্মচারী (Staff) ট্যাব নাম", fontSize = 12.sp) },
                        placeholder = { Text(if (isEnglish) "Employees" else "কর্মচারী", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tab_name_employees_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 6. Settings Tab Name
                    OutlinedTextField(
                        value = tabSettings,
                        onValueChange = {
                            tabSettings = it
                            tabsSaved = false
                        },
                        label = { Text(if (isEnglish) "6. Settings Tab Name" else "৬. সেটিংস ট্যাব নাম", fontSize = 12.sp) },
                        placeholder = { Text(if (isEnglish) "Settings" else "সেটিংস", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tab_name_settings_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text(
                        text = if (isEnglish) "Optional Tabs Visibility:" else "ট্যাব চালু বা বন্ধ করার অপশন:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Toggle Expenses Tab
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEnglish) "Show Expenses Tab" else "খরচ (Expenses) ট্যাব চালু রাখুন",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isEnglish) "Daily store expense management" else "দোকানের দৈনিক খরচ ব্যবস্থাপনা ট্যাব",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = showExpensesTab,
                            onCheckedChange = {
                                showExpensesTab = it
                                tabsSaved = false
                            },
                            modifier = Modifier.testTag("toggle_expenses_tab_switch"),
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
                        )
                    }

                    // Toggle Employees Tab
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEnglish) "Show Employees Tab" else "কর্মচারী (Employees) ট্যাব চালু রাখুন",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isEnglish) "Staff, attendance & salary ledger" else "স্টাফ হাজিরা ও বেতন ব্যবস্থাপনা ট্যাব",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = showEmployeesTab,
                            onCheckedChange = {
                                showEmployeesTab = it
                                tabsSaved = false
                            },
                            modifier = Modifier.testTag("toggle_employees_tab_switch"),
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
                        )
                    }

                    if (tabsSaved) {
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEnglish) "Tabs configuration saved successfully!" else "ট্যাবের সেটিংস সফলভাবে সংরক্ষিত হয়েছে!",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF065F46),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Action Buttons: Reset & Save
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                tabDashboard = ""
                                tabStock = ""
                                tabSales = ""
                                tabExpenses = ""
                                tabEmployees = ""
                                tabSettings = ""
                                showExpensesTab = true
                                showEmployeesTab = true
                                onSaveSettings(
                                    currentSettings.copy(
                                        customTabDashboard = "",
                                        customTabStock = "",
                                        customTabSales = "",
                                        customTabExpenses = "",
                                        customTabEmployees = "",
                                        customTabSettings = "",
                                        showExpensesTab = true,
                                        showEmployeesTab = true
                                    )
                                )
                                tabsSaved = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("reset_tabs_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isEnglish) "Reset Tabs" else "ডিফল্ট ট্যাব", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onSaveSettings(
                                    currentSettings.copy(
                                        customTabDashboard = tabDashboard.trim(),
                                        customTabStock = tabStock.trim(),
                                        customTabSales = tabSales.trim(),
                                        customTabExpenses = tabExpenses.trim(),
                                        customTabEmployees = tabEmployees.trim(),
                                        customTabSettings = tabSettings.trim(),
                                        showExpensesTab = showExpensesTab,
                                        showEmployeesTab = showEmployeesTab
                                    )
                                )
                                tabsSaved = true
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("save_tabs_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isEnglish) "Save Tabs" else "ট্যাব সংরক্ষণ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // PRESET MANAGEMENT SECTION:
        // কোম্পানি, কালার, সাপ্লায়ার, ভ্যারিয়েন্ট, ব্যাটারি, বক্সের অবস্থা এডিট এবং ডিলিট
        // -------------------------------------------------------------
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("preset_management_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF0EA5E9).copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "Stock Presets & Dropdown Options" else "স্টক অপশন ও প্রিসেট ম্যানেজমেন্ট",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isEnglish) "Edit, delete, or add companies, colors, suppliers..." else "কোম্পানি, কালার, সাপ্লায়ার, ভ্যারিয়েন্ট এডিট ও ডিলিট করুন",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Horizontal Scrollable Category Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categories = listOf(
                            Triple("company", "🏢 কোম্পানি", "Company"),
                            Triple("color", "🎨 কালার", "Color"),
                            Triple("supplier", "🚚 সাপ্লায়ার", "Supplier"),
                            Triple("variant", "💾 ভ্যারিয়েন্ট", "Variant"),
                            Triple("battery", "🔋 ব্যাটারি", "Battery"),
                            Triple("box", "📦 বক্সের অবস্থা", "Box"),
                            Triple("payment", "💳 পেমেন্ট মাধ্যম", "Payment")
                        )

                        categories.forEach { (catId, bnLabel, enLabel) ->
                            val isSelected = selectedPresetTab == catId
                            val count = when (catId) {
                                "company" -> currentSettings.companyPresets.size
                                "color" -> currentSettings.colorPresets.size
                                "supplier" -> currentSettings.supplierPresets.size
                                "variant" -> currentSettings.variantPresets.size
                                "battery" -> currentSettings.batteryPresets.size
                                "box" -> currentSettings.boxConditionPresets.size
                                "payment" -> currentSettings.paymentMethodPresets.size
                                else -> 0
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedPresetTab = catId },
                                label = {
                                    Text(
                                        text = if (isEnglish) "$enLabel ($count)" else "$bnLabel ($count)",
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AppleBlue,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("preset_tab_$catId")
                            )
                        }
                    }

                    // Active Category Details & List
                    val activeList = when (selectedPresetTab) {
                        "company" -> currentSettings.companyPresets
                        "color" -> currentSettings.colorPresets
                        "supplier" -> currentSettings.supplierPresets
                        "variant" -> currentSettings.variantPresets
                        "battery" -> currentSettings.batteryPresets
                        "box" -> currentSettings.boxConditionPresets
                        "payment" -> currentSettings.paymentMethodPresets
                        else -> currentSettings.companyPresets
                    }

                    val catTitle = when (selectedPresetTab) {
                        "company" -> if (isEnglish) "Company Brand List" else "কোম্পানির তালিকা (Company)"
                        "color" -> if (isEnglish) "Color Options List" else "কালার তালিকা (Color)"
                        "supplier" -> if (isEnglish) "Suppliers & Sources" else "সাপ্লায়ার বা লট কেনার সোর্স (Supplier)"
                        "variant" -> if (isEnglish) "Storage Variants (RAM/ROM)" else "স্টোরেজ ভ্যারিয়েন্ট (Variant)"
                        "battery" -> if (isEnglish) "Battery Health Percentages" else "ব্যাটারি হেলথ প্রিসেট (Battery)"
                        "box" -> if (isEnglish) "Box & Package Conditions" else "বক্সের অবস্থা (Box Condition)"
                        "payment" -> if (isEnglish) "Payment Method Presets" else "পেমেন্ট মাধ্যম (Payment Methods)"
                        else -> ""
                    }

                    // Action bar inside active category
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = catTitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isEnglish) "${activeList.size} options active" else "মোট ${activeList.size} টি অপশন সক্রিয় আছে",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { showResetCategoryDialog = true },
                                modifier = Modifier.height(34.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isEnglish) "Reset" else "রিস্টোর", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { showAddPresetDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("btn_add_preset_option"),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isEnglish) "+ Add New" else "+ নতুন যোগ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Item Cards List
                    if (activeList.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = if (isEnglish) "No options currently in this category. Click '+ Add New' to create one." else "এই ক্যাটাগরিতে কোনো অপশন নেই। নতুন অপশন দিতে উপরের '+ নতুন যোগ' চাপুন।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            activeList.forEachIndexed { index, item ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    border = CardDefaults.outlinedCardBorder()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = AppleBlue.copy(alpha = 0.12f),
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "${index + 1}",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = AppleBlue
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = item,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            // Edit Button
                                            IconButton(
                                                onClick = {
                                                    editingPresetIndex = index
                                                    editingPresetText = item
                                                },
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .testTag("edit_preset_${selectedPresetTab}_$index")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Edit",
                                                    tint = AppleBlue,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            // Delete Button
                                            IconButton(
                                                onClick = {
                                                    presetToDelete = Pair(index, item)
                                                },
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .testTag("delete_preset_${selectedPresetTab}_$index")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = Color(0xFFEF4444),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // GitHub.com Automatic Backup & Repository Sync Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color(0xFF24292F), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "GitHub.com অটো-সেভ ও সিঙ্ক",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "কোনো অ্যাপ লগিন ছাড়াই সরাসরি গিটহাবে ব্যাকআপ",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (lastSyncTime > 0L) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFF3B82F6).copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (lastSyncTime > 0L) Color(0xFF059669) else Color(0xFF2563EB),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (lastSyncTime > 0L) "সিঙ্ক সক্রিয়" else "অটো-পুশ প্রস্তুত",
                                    color = if (lastSyncTime > 0L) Color(0xFF059669) else Color(0xFF1D4ED8),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "দোকানের লোকাল ডাটাবেসের যাবতীয় ফোন স্টক, বিক্রয় চালান, আয়-ব্যয় ও কর্মচারীর বেতন সরাসরি আপনার github.com রিপোজিটরিতে স্বয়ংক্রিয়ভাবে সেভ হবে।",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Button(
                        onClick = onOpenGitHubSync,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("settings_open_github_sync_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24292F)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GitHub.com রিপোজিটরি সিঙ্ক ও ব্যাকআপ ডায়ালগ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
