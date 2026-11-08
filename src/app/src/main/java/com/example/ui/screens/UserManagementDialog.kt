package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppUser
import com.example.ui.theme.AppleBlue

import androidx.compose.foundation.layout.imePadding

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserManagementDialog(
    users: List<AppUser>,
    currentUser: AppUser?,
    onDismiss: () -> Unit,
    onAddUser: (
        fullName: String,
        username: String,
        password: String,
        role: String,
        phone: String,
        canAddStock: Boolean,
        canEditStock: Boolean,
        canDeleteStock: Boolean,
        canSellProduct: Boolean,
        canEditSale: Boolean,
        canDeleteSale: Boolean,
        canManageExpenses: Boolean,
        canManageEmployees: Boolean,
        canViewReports: Boolean,
        canAccessSettings: Boolean
    ) -> Unit,
    onUpdateUser: (AppUser) -> Unit,
    onToggleActive: (AppUser, Boolean) -> Unit,
    onUpdateAdminCredentials: (username: String, password: String, fullName: String) -> Unit,
    onDeleteUser: (AppUser) -> Unit
) {
    var showAddUserDialog by remember { mutableStateOf(false) }
    var showChangeAdminDialog by remember { mutableStateOf(false) }
    var editingUserPermissions by remember { mutableStateOf<AppUser?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .imePadding()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
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
                                .size(40.dp)
                                .background(AppleBlue.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Users",
                                tint = AppleBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ইউজার ও অ্যাক্সেস কন্ট্রোল",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "এডমিন কর্তৃক ইউজার ও পারমিশন নিয়ন্ত্রণ",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_user_management_dialog")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Admin Profile & Credentials Card (CONOCK / 1219 editable anytime)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppleBlue.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = AppleBlue, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "এডমিন প্রোফাইল (${currentUser?.username ?: "CONOCK"})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = AppleBlue
                                    )
                                    Text(
                                        text = "ডিফল্ট: CONOCK | পাসওয়ার্ড: 1219",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { showChangeAdminDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("btn_change_admin_credentials")
                            ) {
                                Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("পরিবর্তন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Add User Button
                Button(
                    onClick = { showAddUserDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("open_add_user_dialog_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add User")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "নতুন ইউজার তৈরি করুন (পাসওয়ার্ড ও পারমিশনসহ)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "সকল ইউজারের তালিকা ও এক্সেস কন্ট্রোল (${users.size} জন):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Users List with Active/Inactive switch and Permission edit
                users.forEach { user ->
                    val isPrimaryAdmin = user.isAdmin
                    val isCurrent = currentUser?.id == user.id

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isPrimaryAdmin) Color(0xFFF8FAFC) else if (!user.isActive) Color(0xFFFEF2F2) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Row 1: Name, Role, and Active Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.fullName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (isCurrent) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = "আপনি",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF047857),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "ইউজার: ${user.username}",
                                            fontSize = 11.5.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (user.role == "ADMIN") Color(0xFFDBEAFE) else Color(0xFFF3F4F6)
                                        ) {
                                            Text(
                                                text = if (user.role == "ADMIN") "এডমিন" else if (user.role == "MANAGER") "ম্যানেজার" else "স্টাফ",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (user.role == "ADMIN") AppleBlue else Color(0xFF4B5563),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                // Active / Deactivated Switch (For non-admin users)
                                if (!isPrimaryAdmin) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = if (user.isActive) "এক্সেস চালু" else "এক্সেস বন্ধ",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (user.isActive) Color(0xFF047857) else Color(0xFFDC2626)
                                        )
                                        Switch(
                                            checked = user.isActive,
                                            onCheckedChange = { onToggleActive(user, it) },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color(0xFF10B981),
                                                uncheckedThumbColor = Color(0xFFEF4444)
                                            ),
                                            modifier = Modifier.testTag("switch_user_active_${user.id}")
                                        )
                                    }
                                }
                            }

                            // Row 2: User Access summary badges
                            if (!isPrimaryAdmin) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    PermissionBadge(label = "স্টক যোগ", allowed = user.canAddStock)
                                    PermissionBadge(label = "স্টক এডিট", allowed = user.canEditStock)
                                    PermissionBadge(label = "বিক্রি", allowed = user.canSellProduct)
                                    PermissionBadge(label = "মেমো এডিট", allowed = user.canEditSale)
                                    PermissionBadge(label = "আয়-ব্যয়", allowed = user.canManageExpenses)
                                    PermissionBadge(label = "কর্মচারী", allowed = user.canManageEmployees)
                                    PermissionBadge(label = "রিপোর্ট", allowed = user.canViewReports)
                                }
                            }

                            // Row 3: Action Buttons (Edit Permissions & Delete)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(4.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (!isPrimaryAdmin) {
                                        OutlinedButton(
                                            onClick = { editingUserPermissions = user },
                                            modifier = Modifier
                                                .height(34.dp)
                                                .testTag("btn_edit_user_perm_${user.id}"),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("পারমিশন এডিট", fontSize = 11.sp)
                                        }
                                    }

                                    if (!isPrimaryAdmin && !isCurrent) {
                                        IconButton(
                                            onClick = { onDeleteUser(user) },
                                            modifier = Modifier
                                                .size(34.dp)
                                                .testTag("delete_user_${user.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete User",
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(18.dp)
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

    // Sub-dialog: Change Admin Credentials (CONOCK / 1219)
    if (showChangeAdminDialog) {
        ChangeAdminCredentialsDialog(
            currentAdmin = currentUser,
            onDismiss = { showChangeAdminDialog = false },
            onConfirm = { newUname, newPass, newName ->
                onUpdateAdminCredentials(newUname, newPass, newName)
                showChangeAdminDialog = false
            }
        )
    }

    // Sub-dialog: Add New User & Password with Permissions
    if (showAddUserDialog) {
        AddUserWithPermissionsDialog(
            onDismiss = { showAddUserDialog = false },
            onConfirm = { name, uName, pass, role, phone, canAdd, canEdit, canDel, canSell, canEditSale, canDelSale, canExp, canEmp, canRep, canSet ->
                onAddUser(name, uName, pass, role, phone, canAdd, canEdit, canDel, canSell, canEditSale, canDelSale, canExp, canEmp, canRep, canSet)
                showAddUserDialog = false
            }
        )
    }

    // Sub-dialog: Edit Existing User Permissions & Password
    editingUserPermissions?.let { user ->
        EditUserPermissionsDialog(
            user = user,
            onDismiss = { editingUserPermissions = null },
            onSave = { updatedUser ->
                onUpdateUser(updatedUser)
                editingUserPermissions = null
            }
        )
    }
}

@Composable
fun PermissionBadge(label: String, allowed: Boolean) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = if (allowed) Color(0xFF10B981).copy(alpha = 0.12f) else Color(0xFF94A3B8).copy(alpha = 0.12f)
    ) {
        Text(
            text = (if (allowed) "✓ " else "✗ ") + label,
            fontSize = 9.sp,
            fontWeight = if (allowed) FontWeight.Bold else FontWeight.Normal,
            color = if (allowed) Color(0xFF047857) else Color(0xFF64748B),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

// Dialog to Change Admin (CONOCK) Username and Password
@Composable
fun ChangeAdminCredentialsDialog(
    currentAdmin: AppUser?,
    onDismiss: () -> Unit,
    onConfirm: (newUsername: String, newPassword: String, fullName: String) -> Unit
) {
    var fullName by remember { mutableStateOf(currentAdmin?.fullName ?: "CONOCK (Admin)") }
    var username by remember { mutableStateOf(currentAdmin?.username ?: "CONOCK") }
    var password by remember { mutableStateOf(currentAdmin?.password ?: "1219") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "এডমিন ইউজার ও পাসওয়ার্ড পরিবর্তন",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppleBlue
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "এডমিন ইউজারনেম ও পাসওয়ার্ড আপনার ইচ্ছামত পরিবর্তন করে নিন।",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("পুরো নাম", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("এডমিন ইউজারনেম (Username)", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("change_admin_username_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("নতুন পাসওয়ার্ড (Password)", fontSize = 12.sp) },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("change_admin_password_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("বাতিল")
                    }

                    Button(
                        onClick = {
                            if (username.isNotBlank() && password.isNotBlank()) {
                                onConfirm(username.trim(), password.trim(), fullName.trim())
                            }
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("btn_save_admin_credentials"),
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("পরিবর্তন সংরক্ষণ করুন", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// Dialog to Add a New User and Configure Access Permissions
@Composable
fun AddUserWithPermissionsDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        fullName: String,
        username: String,
        password: String,
        role: String,
        phone: String,
        canAddStock: Boolean,
        canEditStock: Boolean,
        canDeleteStock: Boolean,
        canSellProduct: Boolean,
        canEditSale: Boolean,
        canDeleteSale: Boolean,
        canManageExpenses: Boolean,
        canManageEmployees: Boolean,
        canViewReports: Boolean,
        canAccessSettings: Boolean
    ) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("STAFF") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Permissions toggles
    var canAddStock by remember { mutableStateOf(true) }
    var canEditStock by remember { mutableStateOf(true) }
    var canDeleteStock by remember { mutableStateOf(false) }
    var canSellProduct by remember { mutableStateOf(true) }
    var canEditSale by remember { mutableStateOf(false) }
    var canDeleteSale by remember { mutableStateOf(false) }
    var canManageExpenses by remember { mutableStateOf(false) }
    var canManageEmployees by remember { mutableStateOf(false) }
    var canViewReports by remember { mutableStateOf(false) }
    var canAccessSettings by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "নতুন ইউজার তৈরি ও অ্যাক্সেস সেট করুন",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("পুরো নাম *", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_user_fullname_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("ইউজারনেম *", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("new_user_username_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("পাসওয়ার্ড *", fontSize = 12.sp) },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("new_user_password_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Text(
                    text = "পদবী / রোল:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("STAFF" to "স্টাফ", "MANAGER" to "ম্যানেজার", "ADMIN" to "এডমিন").forEach { (roleKey, roleLabel) ->
                        val isSelected = selectedRole == roleKey
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedRole = roleKey
                                if (roleKey == "MANAGER") {
                                    canAddStock = true
                                    canEditStock = true
                                    canSellProduct = true
                                    canEditSale = true
                                    canManageExpenses = true
                                } else if (roleKey == "ADMIN") {
                                    canAddStock = true
                                    canEditStock = true
                                    canDeleteStock = true
                                    canSellProduct = true
                                    canEditSale = true
                                    canDeleteSale = true
                                    canManageExpenses = true
                                    canManageEmployees = true
                                    canViewReports = true
                                    canAccessSettings = true
                                }
                            },
                            label = { Text(roleLabel, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AppleBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Text(
                    text = "ইউজারের জন্য অনুমোদিত ফিচারসমূহ (Permissions):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleBlue
                )

                // Permissions Checklist
                PermissionCheckRow("স্টক পণ্য যোগ করার অনুমতি", canAddStock) { canAddStock = it }
                PermissionCheckRow("স্টক পণ্য এডিট করার অনুমতি", canEditStock) { canEditStock = it }
                PermissionCheckRow("স্টক পণ্য ডিলিট করার অনুমতি", canDeleteStock) { canDeleteStock = it }
                PermissionCheckRow("ফোন বিক্রি ও ক্যাশমেমো তৈরি করার অনুমতি", canSellProduct) { canSellProduct = it }
                PermissionCheckRow("বিক্রিত মেমো এডিট করার অনুমতি", canEditSale) { canEditSale = it }
                PermissionCheckRow("বিক্রিত মেমো ডিলিট করার অনুমতি", canDeleteSale) { canDeleteSale = it }
                PermissionCheckRow("দৈনিক আয়-ব্যয় / ক্যাশবুক দেখার অনুমতি", canManageExpenses) { canManageExpenses = it }
                PermissionCheckRow("কর্মচারী ও বেতন হিসাব দেখার অনুমতি", canManageEmployees) { canManageEmployees = it }
                PermissionCheckRow("লাভ-ক্ষতি ও মাসিক সেলস রিপোর্ট দেখার অনুমতি", canViewReports) { canViewReports = it }
                PermissionCheckRow("সেটিংস ও ব্যাকআপ এক্সেস করার অনুমতি", canAccessSettings) { canAccessSettings = it }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("বাতিল")
                    }

                    Button(
                        onClick = {
                            if (fullName.isNotBlank() && username.isNotBlank() && password.isNotBlank()) {
                                onConfirm(
                                    fullName, username, password, selectedRole, phone,
                                    canAddStock, canEditStock, canDeleteStock, canSellProduct,
                                    canEditSale, canDeleteSale, canManageExpenses, canManageEmployees,
                                    canViewReports, canAccessSettings
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("save_new_user_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("ইউজার তৈরি করুন", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// Dialog to Edit User Permissions and Password
@Composable
fun EditUserPermissionsDialog(
    user: AppUser,
    onDismiss: () -> Unit,
    onSave: (AppUser) -> Unit
) {
    var password by remember { mutableStateOf(user.password) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var canAddStock by remember { mutableStateOf(user.canAddStock) }
    var canEditStock by remember { mutableStateOf(user.canEditStock) }
    var canDeleteStock by remember { mutableStateOf(user.canDeleteStock) }
    var canSellProduct by remember { mutableStateOf(user.canSellProduct) }
    var canEditSale by remember { mutableStateOf(user.canEditSale) }
    var canDeleteSale by remember { mutableStateOf(user.canDeleteSale) }
    var canManageExpenses by remember { mutableStateOf(user.canManageExpenses) }
    var canManageEmployees by remember { mutableStateOf(user.canManageEmployees) }
    var canViewReports by remember { mutableStateOf(user.canViewReports) }
    var canAccessSettings by remember { mutableStateOf(user.canAccessSettings) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${user.fullName} - পারমিশন ও পাসওয়ার্ড",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ইউজারনেম: ${user.username}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("ইউজারের পাসওয়ার্ড", fontSize = 12.sp) },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Text(
                    text = "অনুমোদিত ফিচারসমূহ (Permissions):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleBlue
                )

                PermissionCheckRow("স্টক পণ্য যোগ করার অনুমতি", canAddStock) { canAddStock = it }
                PermissionCheckRow("স্টক পণ্য এডিট করার অনুমতি", canEditStock) { canEditStock = it }
                PermissionCheckRow("স্টক পণ্য ডিলিট করার অনুমতি", canDeleteStock) { canDeleteStock = it }
                PermissionCheckRow("ফোন বিক্রি ও ক্যাশমেমো তৈরি করার অনুমতি", canSellProduct) { canSellProduct = it }
                PermissionCheckRow("বিক্রিত মেমো এডিট করার অনুমতি", canEditSale) { canEditSale = it }
                PermissionCheckRow("বিক্রিত মেমো ডিলিট করার অনুমতি", canDeleteSale) { canDeleteSale = it }
                PermissionCheckRow("দৈনিক আয়-ব্যয় / ক্যাশবুক দেখার অনুমতি", canManageExpenses) { canManageExpenses = it }
                PermissionCheckRow("কর্মচারী ও বেতন হিসাব দেখার অনুমতি", canManageEmployees) { canManageEmployees = it }
                PermissionCheckRow("লাভ-ক্ষতি ও মাসিক সেলস রিপোর্ট দেখার অনুমতি", canViewReports) { canViewReports = it }
                PermissionCheckRow("সেটিংস ও ব্যাকআপ এক্সেস করার অনুমতি", canAccessSettings) { canAccessSettings = it }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("বাতিল")
                    }

                    Button(
                        onClick = {
                            val updated = user.copy(
                                password = password.trim(),
                                canAddStock = canAddStock,
                                canEditStock = canEditStock,
                                canDeleteStock = canDeleteStock,
                                canSellProduct = canSellProduct,
                                canEditSale = canEditSale,
                                canDeleteSale = canDeleteSale,
                                canManageExpenses = canManageExpenses,
                                canManageEmployees = canManageEmployees,
                                canViewReports = canViewReports,
                                canAccessSettings = canAccessSettings
                            )
                            onSave(updated)
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("save_user_permissions_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("সংরক্ষণ করুন", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionCheckRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = AppleBlue)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            color = if (checked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
