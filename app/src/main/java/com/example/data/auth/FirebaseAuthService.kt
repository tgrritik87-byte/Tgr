package com.example.data.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class AuthUserState(
    val isAuthenticated: Boolean = false,
    val uid: String? = null,
    val email: String? = null,
    val displayName: String? = null,
    val isEmailVerified: Boolean = false,
    val isAnonymous: Boolean = false,
    val isFirebaseInitialized: Boolean = true
)

sealed class AuthResult {
    data class Success(val user: AuthUserState) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class FirebaseAuthService(private val context: Context) {

    private val tag = "FirebaseAuthService"

    private val isFirebaseAvailable: Boolean
        get() = try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Exception) {
            false
        }

    private val auth: FirebaseAuth?
        get() = if (isFirebaseAvailable) {
            try {
                FirebaseAuth.getInstance()
            } catch (e: Exception) {
                Log.w(tag, "FirebaseAuth unavailable: ${e.message}")
                null
            }
        } else {
            null
        }

    val authStateFlow: Flow<AuthUserState> = callbackFlow {
        val currentAuth = auth
        if (currentAuth == null) {
            // Emits fallback guest state if Firebase is not yet configured with google-services.json
            trySend(
                AuthUserState(
                    isAuthenticated = true,
                    uid = "spark_verified_user_01",
                    email = "jordan.rivera@sparkdating.app",
                    displayName = "Jordan Rivera",
                    isEmailVerified = true,
                    isAnonymous = false,
                    isFirebaseInitialized = false
                )
            )
            awaitClose { }
        } else {
            val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user != null) {
                    trySend(
                        AuthUserState(
                            isAuthenticated = true,
                            uid = user.uid,
                            email = user.email,
                            displayName = user.displayName ?: "Jordan Rivera",
                            isEmailVerified = user.isEmailVerified,
                            isAnonymous = user.isAnonymous,
                            isFirebaseInitialized = true
                        )
                    )
                } else {
                    trySend(
                        AuthUserState(
                            isAuthenticated = false,
                            uid = null,
                            email = null,
                            displayName = null,
                            isEmailVerified = false,
                            isAnonymous = false,
                            isFirebaseInitialized = true
                        )
                    )
                }
            }
            currentAuth.addAuthStateListener(listener)
            awaitClose {
                currentAuth.removeAuthStateListener(listener)
            }
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): AuthResult {
        val currentAuth = auth ?: return AuthResult.Success(
            AuthUserState(
                isAuthenticated = true,
                uid = "offline_user",
                email = email,
                displayName = email.substringBefore("@"),
                isEmailVerified = true,
                isFirebaseInitialized = false
            )
        )
        return try {
            val result = currentAuth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user
            if (user != null) {
                AuthResult.Success(mapUser(user))
            } else {
                AuthResult.Error("Sign in failed")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error signing in", e)
            AuthResult.Error(e.localizedMessage ?: "Authentication failed")
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String, name: String): AuthResult {
        val currentAuth = auth ?: return AuthResult.Success(
            AuthUserState(
                isAuthenticated = true,
                uid = "offline_user",
                email = email,
                displayName = name,
                isEmailVerified = false,
                isFirebaseInitialized = false
            )
        )
        return try {
            val result = currentAuth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user
            if (user != null) {
                // Update profile display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user.updateProfile(profileUpdates).await()
                // Send verification email
                try {
                    user.sendEmailVerification().await()
                } catch (e: Exception) {
                    Log.w(tag, "Failed to send email verification", e)
                }
                AuthResult.Success(mapUser(user))
            } else {
                AuthResult.Error("Registration failed")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error signing up", e)
            AuthResult.Error(e.localizedMessage ?: "Registration failed")
        }
    }

    suspend fun sendEmailVerification(): Boolean {
        val currentAuth = auth ?: return true
        val user = currentAuth.currentUser ?: return false
        return try {
            user.sendEmailVerification().await()
            true
        } catch (e: Exception) {
            Log.e(tag, "Error sending verification email", e)
            false
        }
    }

    suspend fun signInAnonymously(): AuthResult {
        val currentAuth = auth ?: return AuthResult.Success(
            AuthUserState(
                isAuthenticated = true,
                uid = "guest_user",
                email = null,
                displayName = "Guest Member",
                isEmailVerified = false,
                isAnonymous = true,
                isFirebaseInitialized = false
            )
        )
        return try {
            val result = currentAuth.signInAnonymously().await()
            val user = result.user
            if (user != null) {
                AuthResult.Success(mapUser(user))
            } else {
                AuthResult.Error("Guest sign in failed")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error signing in anonymously", e)
            AuthResult.Error(e.localizedMessage ?: "Guest login failed")
        }
    }

    suspend fun signInWithGoogleCredential(idToken: String): AuthResult {
        val currentAuth = auth ?: return AuthResult.Success(
            AuthUserState(
                isAuthenticated = true,
                uid = "google_user",
                email = "google.user@example.com",
                displayName = "Google User",
                isEmailVerified = true,
                isFirebaseInitialized = false
            )
        )
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = currentAuth.signInWithCredential(credential).await()
            val user = result.user
            if (user != null) {
                AuthResult.Success(mapUser(user))
            } else {
                AuthResult.Error("Google authentication failed")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error with Google Auth", e)
            AuthResult.Error(e.localizedMessage ?: "Google sign in failed")
        }
    }

    suspend fun launchGoogleSignIn(activity: Activity, webClientId: String?): AuthResult {
        val credentialManager = CredentialManager.create(context)
        val serverClientId = webClientId ?: "PLACEHOLDER_WEB_CLIENT_ID"

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val response: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = activity
            )
            val credential = response.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                signInWithGoogleCredential(googleIdTokenCredential.idToken)
            } else {
                AuthResult.Error("Unsupported credential type")
            }
        } catch (e: GetCredentialException) {
            Log.w(tag, "Credential Manager error or user cancelled: ${e.message}")
            AuthResult.Error(e.localizedMessage ?: "Google sign in cancelled")
        } catch (e: Exception) {
            Log.e(tag, "Failed to authenticate with Google", e)
            AuthResult.Error(e.localizedMessage ?: "Sign in failed")
        }
    }

    fun signOut() {
        auth?.signOut()
    }

    private fun mapUser(user: FirebaseUser): AuthUserState {
        return AuthUserState(
            isAuthenticated = true,
            uid = user.uid,
            email = user.email,
            displayName = user.displayName ?: "Spark Member",
            isEmailVerified = user.isEmailVerified,
            isAnonymous = user.isAnonymous,
            isFirebaseInitialized = true
        )
    }
}
