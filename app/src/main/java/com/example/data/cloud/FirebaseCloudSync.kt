package com.example.data.cloud

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest

/**
 * Owner-scoped, non-destructive Firestore sync.
 * Local AppUser records and passwords are never copied to Firestore.
 */
class FirebaseCloudSync(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collections = listOf("products", "sales", "expenses", "employees", "salaryPayments")

    suspend fun sync(ownerUid: String, localBackupJson: String): String {
        require(ownerUid.isNotBlank()) { "Firebase owner account is not connected." }
        val root = JSONObject(localBackupJson)
        root.remove("users")
        val syncRoot = firestore.collection("shops").document(ownerUid).collection("cloudSync")

        for (name in collections) {
            val localArray = root.optJSONArray(name) ?: JSONArray()
            val remoteSnapshot = syncRoot.document(name).collection("records").get().await()
            val merged = LinkedHashMap<String, JSONObject>()

            for (i in 0 until localArray.length()) {
                val item = localArray.optJSONObject(i) ?: continue
                merged[stableKey(name, item)] = item
            }
            for (doc in remoteSnapshot.documents) {
                val key = doc.getString("syncKey") ?: doc.id
                val payload = doc.getString("payload") ?: continue
                if (!merged.containsKey(key)) {
                    try { merged[key] = JSONObject(payload) } catch (_: Exception) {}
                }
            }

            val output = JSONArray()
            merged.values.forEach { output.put(it) }
            root.put(name, output)

            val records = syncRoot.document(name).collection("records")
            for ((key, item) in merged) {
                records.document(sha256(key)).set(
                    mapOf("syncKey" to key, "payload" to item.toString())
                ).await()
            }
        }

        val settingsRef = syncRoot.document("settings")
        val remoteSettings = settingsRef.get().await()
        if (root.has("settings")) {
            settingsRef.set(mapOf("payload" to (root.optJSONObject("settings")?.toString() ?: "{}"))).await()
        } else if (remoteSettings.exists()) {
            remoteSettings.getString("payload")?.let { root.put("settings", JSONObject(it)) }
        }

        root.remove("users")
        return root.toString()
    }

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
