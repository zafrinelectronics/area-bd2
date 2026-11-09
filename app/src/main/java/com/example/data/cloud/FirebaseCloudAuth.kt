package com.example.data.cloud

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Firebase owner authentication is separate from the app's existing local staff login.
 * Never pass local AppUser credentials to this class or to Firebase.
 */
class FirebaseCloudAuth(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentEmail: String?
        get() = auth.currentUser?.email

    val currentUid: String?
        get() = auth.currentUser?.uid

    val isSignedIn: Boolean
        get() = auth.currentUser != null

    suspend fun signIn(email: String, password: String) {
        require(email.isNotBlank()) { "Enter the Firebase account email." }
        require(password.isNotBlank()) { "Enter the Firebase account password." }
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    fun signOut() {
        auth.signOut()
    }
}
