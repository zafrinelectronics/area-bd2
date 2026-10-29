package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ShopSettings
import com.example.ui.theme.AppleBlue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    currentSettings: ShopSettings,
    onSaveSettings: (ShopSettings) -> Unit,
    onResetDefaults: () -> Unit
) {
    var shopName by remember(currentSettings) { mutableStateOf(currentSettings.shopName) }
    var shopTagline by remember(currentSettings) { mutableStateOf(currentSettings.shopTagline) }
    var shopPhone by remember(currentSettings) { mutableStateOf(currentSettings.shopPhone) }
    var shopAddress by remember(currentSettings) { mutableStateOf(currentSettings.shopAddress) }

    var warrantyReplacement by remember(currentSettings) { mutableStateOf(currentSettings.warrantyReplacement) }
    var warrantyService by remember(currentSettings) { mutableStateOf(currentSettings.warrantyService) }
    var warrantyExchangeDeduction by remember(currentSettings) { mutableStateOf(currentSettings.warrantyExchangeDeduction) }

    var companyList by remember(currentSettings) { mutableStateOf(currentSettings.companyPresets) }
    var newCompanyInput by remember { mutableStateOf("") }

    var variantList by remember(currentSettings) { mutableStateOf(currentSettings.variantPresets) }
    var newVariantInput by remember { mutableStateOf("") }

    var colorList by remember(currentSettings) { mutableStateOf(currentSettings.colorPresets) }
    var newColorInput by remember { mutableStateOf("") }

    var boxList by remember(currentSettings) { mutableStateOf(currentSettings.boxConditionPresets) }
    var newBoxInput by remember { mutableStateOf("") }

    var supplierList by remember(currentSettings) { mutableStateOf(currentSettings.supplierPresets) }
    var newSupplierInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
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
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "সেটিংস ও কাস্টমাইজেশন",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ক্যাশ মেমো, ওয়ারেন্টি ও বক্স অপশন",
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
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = AppleBlue)
                    }
                }
            }
        }

        // Section 1: Shop Profile Info
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = AppleBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "১. দোকানের তথ্য ও মেমো হেডার", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedTextField(
                        value = shopName,
                        onValueChange = { shopName = it },
                        label = { Text("দোকানের নাম") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = shopTagline,
                        onValueChange = { shopTagline = it },
                        label = { Text("ট্যাগলাইন / উপাধি") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = shopPhone,
                            onValueChange = { shopPhone = it },
                            label = { Text("ফোন নম্বর") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = shopAddress,
                            onValueChange = { shopAddress = it },
                            label = { Text("ঠিকানা") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Section 2: Warranty Card & Cash Memo Policy Customization
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "২. ওয়ারেন্টি কার্ড ও পলিসি কাস্টমাইজেশন", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "ক্যাশ মেমো এবং ইনভয়েসে প্রিন্ট হওয়ার জন্য ওয়ারেন্টি শর্তাবলী সেট করুন:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 1. Replacement policy
                    OutlinedTextField(
                        value = warrantyReplacement,
                        onValueChange = { warrantyReplacement = it },
                        label = { Text("১. রিপ্লেসমেন্ট গ্যারান্টি শর্ত") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 2. Service warranty
                    OutlinedTextField(
                        value = warrantyService,
                        onValueChange = { warrantyService = it },
                        label = { Text("২. সার্ভিস ওয়ারেন্টি শর্ত") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 3. Exchange / Deduction policy
                    OutlinedTextField(
                        value = warrantyExchangeDeduction,
                        onValueChange = { warrantyExchangeDeduction = it },
                        label = { Text("৩. এক্সচেঞ্জ ও সেল ডিডাকশন পলিসি") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Live Preview box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = AppleBlue.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "লাইভ ক্যাশ মেমো প্রিভিউ:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppleBlue)
                            Text(text = "• $warrantyReplacement", fontSize = 11.sp)
                            Text(text = "• $warrantyService", fontSize = 11.sp)
                            Text(text = "• $warrantyExchangeDeduction", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Section 3: Customize Input Windows & Selection Boxes (Presets)
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Widgets, contentDescription = null, tint = AppleBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "৩. ইনপুট উইন্ডো ও বক্স অপশন পরিবর্তন", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    // Company presets
                    PresetChipEditor(
                        title = "কোম্পানি / ব্র্যান্ড অপশন",
                        items = companyList,
                        onRemove = { item -> companyList = companyList.filter { it != item } },
                        newInputValue = newCompanyInput,
                        onNewInputChange = { newCompanyInput = it },
                        onAdd = {
                            if (newCompanyInput.isNotBlank()) {
                                companyList = companyList + newCompanyInput.trim()
                                newCompanyInput = ""
                            }
                        }
                    )
                    HorizontalDivider()

                    // Variant presets
                    PresetChipEditor(
                        title = "ভ্যারিয়েন্ট / স্টোরেজ অপশন",
                        items = variantList,
                        onRemove = { item -> variantList = variantList.filter { it != item } },
                        newInputValue = newVariantInput,
                        onNewInputChange = { newVariantInput = it },
                        onAdd = {
                            if (newVariantInput.isNotBlank()) {
                                variantList = variantList + newVariantInput.trim()
                                newVariantInput = ""
                            }
                        }
                    )
                    HorizontalDivider()

                    // Color presets
                    PresetChipEditor(
                        title = "কালার অপশন",
                        items = colorList,
                        onRemove = { item -> colorList = colorList.filter { it != item } },
                        newInputValue = newColorInput,
                        onNewInputChange = { newColorInput = it },
                        onAdd = {
                            if (newColorInput.isNotBlank()) {
                                colorList = colorList + newColorInput.trim()
                                newColorInput = ""
                            }
                        }
                    )
                    HorizontalDivider()

                    // Box condition presets
                    PresetChipEditor(
                        title = "বক্সের অবস্থা অপশন",
                        items = boxList,
                        onRemove = { item -> boxList = boxList.filter { it != item } },
                        newInputValue = newBoxInput,
                        onNewInputChange = { newBoxInput = it },
                        onAdd = {
                            if (newBoxInput.isNotBlank()) {
                                boxList = boxList + newBoxInput.trim()
                                newBoxInput = ""
                            }
                        }
                    )
                    HorizontalDivider()

                    // Supplier presets
                    PresetChipEditor(
                        title = "সাপ্লায়ার অপশন",
                        items = supplierList,
                        onRemove = { item -> supplierList = supplierList.filter { it != item } },
                        newInputValue = newSupplierInput,
                        onNewInputChange = { newSupplierInput = it },
                        onAdd = {
                            if (newSupplierInput.isNotBlank()) {
                                supplierList = supplierList + newSupplierInput.trim()
                                newSupplierInput = ""
                            }
                        }
                    )
                }
            }
        }

        // Action Buttons: Save Settings & Reset
        item {
            Button(
                onClick = {
                    val updated = currentSettings.copy(
                        shopName = shopName.trim(),
                        shopTagline = shopTagline.trim(),
                        shopPhone = shopPhone.trim(),
                        shopAddress = shopAddress.trim(),
                        warrantyReplacement = warrantyReplacement.trim(),
                        warrantyService = warrantyService.trim(),
                        warrantyExchangeDeduction = warrantyExchangeDeduction.trim(),
                        companyPresets = companyList,
                        variantPresets = variantList,
                        colorPresets = colorList,
                        boxConditionPresets = boxList,
                        supplierPresets = supplierList
                    )
                    onSaveSettings(updated)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_settings_button"),
                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                Spacer(modifier = Modifier.width(8.dp))
                Text("সব পরিবর্তন সেভ করুন (Save Settings)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onResetDefaults,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("reset_defaults_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.RestartAlt, contentDescription = "Reset")
                Spacer(modifier = Modifier.width(6.dp))
                Text("ডিফল্ট সেটিংসে রিসেট করুন", fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PresetChipEditor(
    title: String,
    items: List<String>,
    onRemove: (String) -> Unit,
    newInputValue: String,
    onNewInputChange: (String) -> Unit,
    onAdd: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 2.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = item, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = { onRemove(item) },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newInputValue,
                onValueChange = onNewInputChange,
                placeholder = { Text("নতুন $title লিখুন...", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
            Button(
                onClick = onAdd,
                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Text("যোগ", fontSize = 11.sp)
            }
        }
    }
}
