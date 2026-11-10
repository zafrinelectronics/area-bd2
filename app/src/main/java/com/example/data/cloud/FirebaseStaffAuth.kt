package com.example.data.cloud

import android.content.Context
import com.example.data.model.AppUser
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Locale
import java.util.UUID

/**
 * Staff credentials are stored only in Firebase Authentication.
 * The shop-scoped profile contains permissions and the opaque Auth UID/email,
 * never a password. This class uses a secondary FirebaseApp so staff sign-in
 * cannot replace the default app's anonymous device-pairing identity.
 */
class FirebaseStaffAuth(
    context: Context,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val staffApp: FirebaseApp =
        FirebaseApp.getApps(context).firstOrNull { it.name == STAFF_APP_NAME }
            ?: FirebaseApp.initializeApp(
                context.applicationContext,
                FirebaseApp.getInstance().options,
                STAFF_APP_NAME
            )

    private val staffAuth: FirebaseAuth = FirebaseAuth.getInstance(staffApp)

    suspend fun createStaff(shopUid: String, user: AppUser, password: String) {
        require(shopUid.isNotBlank()) { "দোকানের Firebase সংযোগ পাওয়া যায়নি।" }
        require(password.length >= 6) { "Firebase পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে।" }
        val usernameKey = usernameKey(user.username)
        val profileRef = staffCollection(shopUid).document(usernameKey)
        require(!profileRef.get().await().exists()) {
            "এই ইউজারনেমটি ইতিমধ্যে ব্যবহার করা হয়েছে।"
        }

        val syntheticEmail = "staff-${UUID.randomUUID()}@appleareashop.firebaseapp.com"
        var createdUid: String? = null
        try {
            val result = staffAuth.createUserWithEmailAndPassword(syntheticEmail, password).await()
            createdUid = result.user?.uid
                ?: throw IllegalStateException("Firebase স্টাফ অ্যাকাউন্ট তৈরি করতে পারেনি।")

            profileRef.set(profileMap(user, createdUid, syntheticEmail)).await()
        } catch (error: Exception) {
            try {
                staffAuth.currentUser?.delete()?.await()
            } catch (_: Exception) {
                // The profile is not published; any orphaned Auth account cannot sign in to this shop.
            }
            throw error
        } finally {
            staffAuth.signOut()
        }
    }

    suspend fun signIn(shopUid: String, username: String, password: String): AppUser {
        val profile = staffCollection(shopUid).document(usernameKey(username)).get().await()
        require(profile.exists()) { "ইউজারনেম পাওয়া যায়নি।" }
        require(profile.getBoolean("isActive") == true) {
            "এই অ্যাকাউন্টটি Admin বন্ধ করে রেখেছেন।"
        }
        val email = profile.getString("authEmail")
            ?: throw IllegalStateException("স্টাফ অ্যাকাউন্টের তথ্য অসম্পূর্ণ।")
        val expectedUid = profile.getString("authUid")
            ?: throw IllegalStateException("স্টাফ অ্যাকাউন্টের তথ্য অসম্পূর্ণ।")

        // Always verify the password on an explicit login attempt, even if a previous
        // secondary-auth session is still present on this device.
        staffAuth.signOut()
        val result = staffAuth.signInWithEmailAndPassword(email, password).await()
        if (result.user?.uid != expectedUid) {
            staffAuth.signOut()
            throw IllegalStateException("লগইন যাচাই করা যায়নি।")
        }
        return profile.toAppUser()
    }

    suspend fun restoreSession(shopUid: String, username: String): AppUser? {
        val profile = staffCollection(shopUid).document(usernameKey(username)).get().await()
        if (!profile.exists() || profile.getBoolean("isActive") != true) return null
        if (profile.getString("authUid") != staffAuth.currentUser?.uid) return null
        return profile.toAppUser()
    }

    suspend fun listStaff(shopUid: String): List<AppUser> {
        return staffCollection(shopUid).get().await().documents
            .map { it.toAppUser() }
            .sortedBy { it.fullName.lowercase(Locale.ROOT) }
    }

    suspend fun updateStaff(shopUid: String, user: AppUser) {
        val ref = staffCollection(shopUid).document(usernameKey(user.username))
        val snapshot = ref.get().await()
        require(snapshot.exists()) { "Firebase-এ এই স্টাফ অ্যাকাউন্ট পাওয়া যায়নি।" }
        ref.update(
            mapOf(
                "fullName" to user.fullName,
                "role" to user.role,
                "phone" to user.phone,
                "isActive" to user.isActive,
                "canAddStock" to user.canAddStock,
                "canEditStock" to user.canEditStock,
                "canDeleteStock" to user.canDeleteStock,
                "canSellProduct" to user.canSellProduct,
                "canEditSale" to user.canEditSale,
                "canDeleteSale" to user.canDeleteSale,
                "canManageExpenses" to user.canManageExpenses,
                "canManageEmployees" to user.canManageEmployees,
                "canViewReports" to user.canViewReports,
                "canAccessSettings" to user.canAccessSettings
            )
        ).await()
    }

    suspend fun deleteStaff(shopUid: String, user: AppUser) {
        val ref = staffCollection(shopUid).document(usernameKey(user.username))
        val snapshot = ref.get().await()
        val uid = snapshot.getString("authUid")
        ref.delete().await()
        if (uid != null && staffAuth.currentUser?.uid == uid) staffAuth.signOut()
    }

    fun signOut() {
        staffAuth.signOut()
    }

    private fun staffCollection(shopUid: String) =
        firestore.collection("shopStaff").document(shopUid).collection("accounts")

    private fun usernameKey(username: String): String {
        val key = username.trim().lowercase(Locale.ROOT)
        require(key.matches(Regex("[a-z0-9._-]{3,32}"))) {
            "ইউজারনেম ৩–৩২ অক্ষরের হতে হবে; ইংরেজি অক্ষর, সংখ্যা, ডট, হাইফেন বা আন্ডারস্কোর ব্যবহার করুন।"
        }
        return key
    }

    private fun profileMap(user: AppUser, uid: String, email: String): Map<String, Any> =
        mapOf(
            "username" to user.username.trim(),
            "authUid" to uid,
            "authEmail" to email,
            "fullName" to user.fullName,
            "role" to user.role,
            "phone" to user.phone,
            "isActive" to true,
            "canAddStock" to user.canAddStock,
            "canEditStock" to user.canEditStock,
            "canDeleteStock" to user.canDeleteStock,
            "canSellProduct" to user.canSellProduct,
            "canEditSale" to user.canEditSale,
            "canDeleteSale" to user.canDeleteSale,
            "canManageExpenses" to user.canManageExpenses,
            "canManageEmployees" to user.canManageEmployees,
            "canViewReports" to user.canViewReports,
            "canAccessSettings" to user.canAccessSettings,
            "createdAt" to System.currentTimeMillis()
        )

    private fun com.google.firebase.firestore.DocumentSnapshot.toAppUser() = AppUser(
        id = 0L,
        username = getString("username").orEmpty(),
        password = "",
        fullName = getString("fullName").orEmpty(),
        role = getString("role") ?: "STAFF",
        phone = getString("phone").orEmpty(),
        isActive = getBoolean("isActive") == true,
        canAddStock = getBoolean("canAddStock") == true,
        canEditStock = getBoolean("canEditStock") == true,
        canDeleteStock = getBoolean("canDeleteStock") == true,
        canSellProduct = getBoolean("canSellProduct") == true,
        canEditSale = getBoolean("canEditSale") == true,
        canDeleteSale = getBoolean("canDeleteSale") == true,
        canManageExpenses = getBoolean("canManageExpenses") == true,
        canManageEmployees = getBoolean("canManageEmployees") == true,
        canViewReports = getBoolean("canViewReports") == true,
        canAccessSettings = getBoolean("canAccessSettings") == true,
        createdAt = getLong("createdAt") ?: System.currentTimeMillis()
    )

    companion object {
        private const val STAFF_APP_NAME = "apple_area_staff_auth"
    }
}
