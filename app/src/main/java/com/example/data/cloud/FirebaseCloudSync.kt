package com.example.data.cloud

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Timestamp
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Date

/**
 * Shop-scoped Firestore sync and secure one-time device pairing.
 * Pairing codes are high-entropy, stored only as SHA-256 hashes, expire after
 * ten minutes, and can be redeemed once in a Firestore transaction.
 */
class FirebaseCloudSync(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collections = listOf("products", "sales", "expenses", "employees", "salaryPayments")
    private val inviteAlphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

    suspend fun ensureOwnerShop(ownerUid: String) {
        require(ownerUid.isNotBlank()) { "Firebase user is not connected." }
        val memberRef = membershipRef(ownerUid, ownerUid)
        val existing = memberRef.get().await()
        if (!existing.exists()) {
            memberRef.set(
                mapOf(
                    "uid" to ownerUid,
                    "role" to "owner",
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    suspend fun isMember(shopUid: String, memberUid: String): Boolean {
        if (shopUid.isBlank() || memberUid.isBlank()) return false
        return membershipRef(shopUid, memberUid).get().await().exists()
    }

    suspend fun createPairingCode(shopUid: String, creatorUid: String): String {
        require(shopUid.isNotBlank() && creatorUid.isNotBlank()) { "Shop is not connected." }
        val member = membershipRef(shopUid, creatorUid).get().await()
        require(member.getString("role") == "owner") {
            "Only the shop owner can create a pairing code."
        }

        val code = buildString {
            repeat(16) { append(inviteAlphabet[SecureRandom().nextInt(inviteAlphabet.length)]) }
        }
        val inviteId = sha256(code)
        firestore.collection("pairingInvites").document(inviteId).set(
            mapOf(
                "ownerUid" to shopUid,
                "createdByUid" to creatorUid,
                "redeemed" to false,
                "createdAt" to FieldValue.serverTimestamp(),
                "expiresAt" to Timestamp(Date(System.currentTimeMillis() + 10 * 60 * 1000L))
            )
        ).await()
        return code.chunked(4).joinToString("-")
    }

    suspend fun joinShopWithCode(rawCode: String, joiningUid: String): String {
        val code = rawCode.uppercase().filter { it.isLetterOrDigit() }
        require(code.length == 16 && code.all { it in inviteAlphabet }) {
            "Enter the complete 16-character pairing code."
        }
        require(joiningUid.isNotBlank()) { "This device is not connected to Firebase." }

        val inviteId = sha256(code)
        val inviteRef = firestore.collection("pairingInvites").document(inviteId)
        val transactionResult = firestore.runTransaction { transaction ->
            val invite = transaction.get(inviteRef)
            check(invite.exists()) { "Pairing code is invalid or has expired." }
            check(invite.getBoolean("redeemed") != true) { "This pairing code has already been used." }
            val expiresAt = invite.getTimestamp("expiresAt")
            check(expiresAt != null && expiresAt.toDate().time > System.currentTimeMillis()) {
                "Pairing code has expired. Ask the shop owner for a new code."
            }
            val shopUid = invite.getString("ownerUid")
                ?: throw IllegalStateException("Pairing code is invalid.")
            transaction.update(
                inviteRef,
                mapOf(
                    "redeemed" to true,
                    "redeemedBy" to joiningUid,
                    "redeemedAt" to FieldValue.serverTimestamp()
                )
            )
            transaction.set(
                membershipRef(shopUid, joiningUid),
                mapOf(
                    "uid" to joiningUid,
                    "role" to "member",
                    "inviteId" to inviteId,
                    "joinedAt" to FieldValue.serverTimestamp()
                )
            )
            shopUid
        }.await()
        return transactionResult
    }

    fun listenForChanges(
        shopUid: String,
        deviceId: String,
        onRemoteChange: () -> Unit
    ): ListenerRegistration {
        return firestore.collection("shops").document(shopUid)
            .collection("cloudSync").document("manifest")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot?.exists() == true &&
                    snapshot.getString("sourceDeviceId") != deviceId
                ) {
                    onRemoteChange()
                }
            }
    }

    suspend fun sync(
        ownerUid: String,
        localBackupJson: String,
        preferRemote: Boolean = false,
        deviceId: String = "unknown-device"
    ): String {
        require(ownerUid.isNotBlank()) { "Firebase shop is not connected." }
        val root = JSONObject(localBackupJson)
        root.remove("users")
        val syncRoot = firestore.collection("shops").document(ownerUid).collection("cloudSync")
        var changedRemote = false

        for (name in collections) {
            val localArray = root.optJSONArray(name) ?: JSONArray()
            val records = syncRoot.document(name).collection("records")
            val remoteSnapshot = records.get().await()
            val remoteById = remoteSnapshot.documents.associateBy { it.id }
            val merged = LinkedHashMap<String, JSONObject>()

            if (preferRemote) {
                for (doc in remoteSnapshot.documents) {
                    val key = doc.getString("syncKey") ?: doc.id
                    val payload = doc.getString("payload") ?: continue
                    try { merged[key] = JSONObject(payload) } catch (_: Exception) {}
                }
            }

            for (i in 0 until localArray.length()) {
                val item = localArray.optJSONObject(i) ?: continue
                val key = stableKey(name, item)
                if (preferRemote) {
                    if (!merged.containsKey(key)) merged[key] = item
                } else {
                    merged[key] = item
                }
            }

            if (!preferRemote) {
                for (doc in remoteSnapshot.documents) {
                    val key = doc.getString("syncKey") ?: doc.id
                    val payload = doc.getString("payload") ?: continue
                    if (!merged.containsKey(key)) {
                        try { merged[key] = JSONObject(payload) } catch (_: Exception) {}
                    }
                }
            }

            val output = JSONArray()
            merged.values.forEach { output.put(it) }
            root.put(name, output)

            for ((key, item) in merged) {
                val documentId = sha256(key)
                val payload = item.toString()
                if (remoteById[documentId]?.getString("payload") != payload) {
                    records.document(documentId).set(
                        mapOf("syncKey" to key, "payload" to payload)
                    ).await()
                    changedRemote = true
                }
            }
        }

        val settingsRef = syncRoot.document("settings")
        val remoteSettings = settingsRef.get().await()
        val localSettings = root.optJSONObject("settings")?.toString()
        val remoteSettingsPayload = remoteSettings.getString("payload")
        if (preferRemote && remoteSettingsPayload != null) {
            root.put("settings", JSONObject(remoteSettingsPayload))
        } else if (localSettings != null && localSettings != remoteSettingsPayload) {
            settingsRef.set(mapOf("payload" to localSettings)).await()
            changedRemote = true
        } else if (localSettings == null && remoteSettingsPayload != null) {
            root.put("settings", JSONObject(remoteSettingsPayload))
        }

        if (changedRemote) {
            syncRoot.document("manifest").set(
                mapOf(
                    "sourceDeviceId" to deviceId,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }

        root.remove("users")
        return root.toString()
    }

    private fun membershipRef(shopUid: String, memberUid: String) =
        firestore.collection("shopMemberships").document(shopUid)
            .collection("members").document(memberUid)

    private fun stableKey(collection: String, item: JSONObject): String {
        return when (collection) {
            "products" -> item.optString("imei").takeIf { it.isNotBlank() }
                ?.let { "imei:$it" }
                ?: "product:${item.optLong("id")}:${item.optLong("createdAt")}"
            "sales" -> item.optString("invoiceNumber").takeIf { it.isNotBlank() }
                ?.let { "invoice:$it" }
                ?: "sale:${item.optLong("id")}:${item.optLong("soldAt")}"
            "expenses" -> item.optString("referenceId").takeIf { it.isNotBlank() && it != "null" }
                ?.let { "reference:$it" }
                ?: "expense:${item.optLong("dateMillis")}:${item.optDouble("amount")}:${item.optString("category")}:${item.optString("note")}"
            "employees" -> item.optString("phone").takeIf { it.isNotBlank() }
                ?.let { "phone:$it" }
                ?: "employee:${item.optString("name").trim().lowercase()}"
            else -> "salary:${item.optString("employeeName")}:${item.optString("monthYear")}:${item.optLong("paymentDateMillis")}:${item.optDouble("amountPaid")}:${item.optString("paymentType")}"
        }
    }

    private fun sha256(value: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
