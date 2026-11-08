package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_users")
data class AppUser(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,       // ইউজার নেম (যেমন: CONOCK)
    val password: String,       // পাসওয়ার্ড (যেমন: 1219)
    val fullName: String,       // পুরো নাম
    val role: String = "ADMIN", // ADMIN, MANAGER, STAFF
    val phone: String = "",     // মোবাইল নম্বর
    val isActive: Boolean = true, // এক্সেস চালু / বন্ধ (Active / Deactivated)
    
    // এক্সেস পারমিশন কন্ট্রোল
    val canAddStock: Boolean = true,
    val canEditStock: Boolean = true,
    val canDeleteStock: Boolean = false,
    val canSellProduct: Boolean = true,
    val canEditSale: Boolean = false,
    val canDeleteSale: Boolean = false,
    val canManageExpenses: Boolean = false,
    val canManageEmployees: Boolean = false,
    val canViewReports: Boolean = false,
    val canAccessSettings: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isAdmin: Boolean
        get() = role.equals("ADMIN", ignoreCase = true)

    fun canAccess(feature: String): Boolean {
        if (!isActive) return false
        if (isAdmin) return true
        return when (feature) {
            "ADD_STOCK" -> canAddStock
            "EDIT_STOCK" -> canEditStock
            "DELETE_STOCK" -> canDeleteStock
            "SELL_PRODUCT" -> canSellProduct
            "EDIT_SALE" -> canEditSale
            "DELETE_SALE" -> canDeleteSale
            "EXPENSES" -> canManageExpenses
            "EMPLOYEES" -> canManageEmployees
            "REPORTS" -> canViewReports
            "SETTINGS" -> canAccessSettings
            else -> false
        }
    }
}
