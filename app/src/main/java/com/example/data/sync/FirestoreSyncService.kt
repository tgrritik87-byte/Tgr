package com.example.data.sync

import android.content.Context
import android.util.Log
import com.example.data.local.dao.DatingDao
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.DatingProfileEntity
import com.example.data.local.entities.MatchEntity
import com.example.data.local.entities.UserProfileEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

enum class SyncStatus {
    IDLE,
    SYNCING,
    SYNCED,
    OFFLINE_LOCAL,
    ERROR
}

data class SyncState(
    val status: SyncStatus = SyncStatus.IDLE,
    val lastSyncTimestamp: Long = 0L,
    val syncedProfilesCount: Int = 0,
    val syncedMatchesCount: Int = 0,
    val message: String = "Sync service initialized"
)

class FirestoreSyncService(
    private val context: Context,
    private val datingDao: DatingDao
) {
    private val tag = "FirestoreSyncService"

    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private var profilesListener: ListenerRegistration? = null
    private var matchesListener: ListenerRegistration? = null

    private val isFirestoreAvailable: Boolean
        get() = try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Exception) {
            false
        }

    private val firestore: FirebaseFirestore?
        get() = if (isFirestoreAvailable) {
            try {
                FirebaseFirestore.getInstance()
            } catch (e: Exception) {
                Log.w(tag, "Firestore instance unavailable: ${e.message}")
                null
            }
        } else {
            null
        }

    /**
     * Starts continuous real-time two-way synchronization between Room and Firestore.
     */
    fun startRealtimeSync(scope: CoroutineScope) {
        val db = firestore
        if (db == null) {
            _syncState.value = SyncState(
                status = SyncStatus.OFFLINE_LOCAL,
                lastSyncTimestamp = System.currentTimeMillis(),
                message = "Offline-First Room storage active (Firestore offline or waiting for credentials)"
            )
            return
        }

        _syncState.value = _syncState.value.copy(
            status = SyncStatus.SYNCING,
            message = "Connecting real-time Firestore listeners..."
        )

        // 1. Listen to real-time changes on candidate dating profiles from Firestore
        profilesListener?.remove()
        profilesListener = db.collection("dating_profiles")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(tag, "Profiles listener error: ${error.message}")
                    _syncState.value = _syncState.value.copy(
                        status = SyncStatus.ERROR,
                        message = "Profiles sync error: ${error.localizedMessage}"
                    )
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    scope.launch(Dispatchers.IO) {
                        try {
                            val remoteProfiles = snapshot.documents.mapNotNull { doc ->
                                try {
                                    DatingProfileEntity(
                                        id = doc.getString("id") ?: doc.id,
                                        name = doc.getString("name") ?: "Member",
                                        age = (doc.getLong("age") ?: 24L).toInt(),
                                        gender = doc.getString("gender") ?: "Not specified",
                                        distanceKm = (doc.getLong("distanceKm") ?: 5L).toInt(),
                                        locationName = doc.getString("locationName") ?: "Nearby",
                                        occupation = doc.getString("occupation") ?: "Creative",
                                        companyOrSchool = doc.getString("companyOrSchool") ?: "Company",
                                        isVerified = doc.getBoolean("isVerified") ?: true,
                                        verificationMethod = doc.getString("verificationMethod") ?: "Selfie Pose Verified",
                                        bio = doc.getString("bio") ?: "",
                                        prompts = doc.getString("prompts") ?: "",
                                        interests = doc.getString("interests") ?: "",
                                        relationshipIntent = doc.getString("relationshipIntent") ?: "Long-term",
                                        avatarDrawable = doc.getString("avatarDrawable") ?: "img_avatar_elena",
                                        gradientStartHex = doc.getLong("gradientStartHex") ?: 0xFFFF4B72,
                                        gradientEndHex = doc.getLong("gradientEndHex") ?: 0xFFFF8359,
                                        likedCurrentUser = doc.getBoolean("likedCurrentUser") ?: true,
                                        heightCm = (doc.getLong("heightCm") ?: 170L).toInt(),
                                        zodiac = doc.getString("zodiac") ?: "Libra",
                                        pets = doc.getString("pets") ?: "Dog lover"
                                    )
                                } catch (e: Exception) {
                                    null
                                }
                            }
                            if (remoteProfiles.isNotEmpty()) {
                                datingDao.insertProfiles(remoteProfiles)
                                _syncState.value = _syncState.value.copy(
                                    status = SyncStatus.SYNCED,
                                    lastSyncTimestamp = System.currentTimeMillis(),
                                    syncedProfilesCount = remoteProfiles.size,
                                    message = "Synced ${remoteProfiles.size} profiles from Cloud Firestore"
                                )
                            }
                        } catch (e: Exception) {
                            Log.e(tag, "Failed to persist remote profiles to Room", e)
                        }
                    }
                }
            }

        // 2. Listen to real-time changes on mutual matches from Firestore
        matchesListener?.remove()
        matchesListener = db.collection("matches")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(tag, "Matches listener error: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    scope.launch(Dispatchers.IO) {
                        try {
                            val remoteMatches = snapshot.documents.mapNotNull { doc ->
                                try {
                                    MatchEntity(
                                        matchId = doc.getString("matchId") ?: doc.id,
                                        profileId = doc.getString("profileId") ?: "",
                                        matchedAt = doc.getLong("matchedAt") ?: System.currentTimeMillis(),
                                        lastMessage = doc.getString("lastMessage") ?: "Matched!",
                                        lastMessageTimestamp = doc.getLong("lastMessageTimestamp") ?: System.currentTimeMillis(),
                                        isNewMatch = doc.getBoolean("isNewMatch") ?: false,
                                        unreadCount = (doc.getLong("unreadCount") ?: 0L).toInt()
                                    )
                                } catch (e: Exception) {
                                    null
                                }
                            }
                            remoteMatches.forEach { match ->
                                datingDao.insertMatch(match)
                            }
                            _syncState.value = _syncState.value.copy(
                                status = SyncStatus.SYNCED,
                                lastSyncTimestamp = System.currentTimeMillis(),
                                syncedMatchesCount = remoteMatches.size,
                                message = "Synced ${remoteMatches.size} mutual matches in real time"
                            )
                        } catch (e: Exception) {
                            Log.e(tag, "Failed to persist remote matches to Room", e)
                        }
                    }
                }
            }

        // 3. Push initial local dataset to Firestore if remote collection is empty
        scope.launch(Dispatchers.IO) {
            pushLocalDataToFirestore()
        }
    }

    /**
     * Pushes current local user profile and local candidate profiles into Firestore
     */
    suspend fun pushLocalDataToFirestore() = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            _syncState.value = _syncState.value.copy(status = SyncStatus.SYNCING, message = "Uploading local data to Firestore...")

            // 1. Sync User Profile
            val user = datingDao.getUserProfile().firstOrNull()
            if (user != null) {
                val userMap = hashMapOf(
                    "userId" to user.userId,
                    "name" to user.name,
                    "age" to user.age,
                    "bio" to user.bio,
                    "occupation" to user.occupation,
                    "location" to user.location,
                    "isVerified" to user.isVerified,
                    "verificationPose" to user.verificationPose,
                    "interests" to user.interests,
                    "lastActive" to System.currentTimeMillis()
                )
                db.collection("users").document(user.userId)
                    .set(userMap, SetOptions.merge()).await()
            }

            // 2. Sync Local Candidate Profiles
            val localProfiles = datingDao.getAllProfiles().firstOrNull() ?: emptyList()
            for (p in localProfiles) {
                val profileMap = hashMapOf(
                    "id" to p.id,
                    "name" to p.name,
                    "age" to p.age,
                    "gender" to p.gender,
                    "distanceKm" to p.distanceKm,
                    "locationName" to p.locationName,
                    "occupation" to p.occupation,
                    "companyOrSchool" to p.companyOrSchool,
                    "isVerified" to p.isVerified,
                    "verificationMethod" to p.verificationMethod,
                    "bio" to p.bio,
                    "prompts" to p.prompts,
                    "interests" to p.interests,
                    "relationshipIntent" to p.relationshipIntent,
                    "avatarDrawable" to p.avatarDrawable,
                    "gradientStartHex" to p.gradientStartHex,
                    "gradientEndHex" to p.gradientEndHex,
                    "likedCurrentUser" to p.likedCurrentUser,
                    "heightCm" to p.heightCm,
                    "zodiac" to p.zodiac,
                    "pets" to p.pets
                )
                db.collection("dating_profiles").document(p.id)
                    .set(profileMap, SetOptions.merge()).await()
            }

            // 3. Sync Matches
            val matches = datingDao.getAllMatches().firstOrNull() ?: emptyList()
            for (m in matches) {
                val matchMap = hashMapOf(
                    "matchId" to m.matchId,
                    "profileId" to m.profileId,
                    "matchedAt" to m.matchedAt,
                    "lastMessage" to m.lastMessage,
                    "lastMessageTimestamp" to m.lastMessageTimestamp,
                    "isNewMatch" to m.isNewMatch,
                    "unreadCount" to m.unreadCount
                )
                db.collection("matches").document(m.matchId)
                    .set(matchMap, SetOptions.merge()).await()
            }

            _syncState.value = SyncState(
                status = SyncStatus.SYNCED,
                lastSyncTimestamp = System.currentTimeMillis(),
                syncedProfilesCount = localProfiles.size,
                syncedMatchesCount = matches.size,
                message = "Real-time sync active: ${localProfiles.size} profiles & ${matches.size} matches"
            )
        } catch (e: Exception) {
            Log.e(tag, "Error pushing data to Firestore", e)
            _syncState.value = _syncState.value.copy(
                status = SyncStatus.ERROR,
                message = "Push to Firestore failed: ${e.localizedMessage}"
            )
        }
    }

    /**
     * Uploads a newly formed mutual match directly to Firestore in real-time.
     */
    suspend fun syncMatchToFirestore(match: MatchEntity) = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            val map = hashMapOf(
                "matchId" to match.matchId,
                "profileId" to match.profileId,
                "matchedAt" to match.matchedAt,
                "lastMessage" to match.lastMessage,
                "lastMessageTimestamp" to match.lastMessageTimestamp,
                "isNewMatch" to match.isNewMatch,
                "unreadCount" to match.unreadCount
            )
            db.collection("matches").document(match.matchId)
                .set(map, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e(tag, "Failed to sync match to Firestore", e)
        }
    }

    /**
     * Uploads an updated user profile to Firestore in real-time.
     */
    suspend fun syncUserProfileToFirestore(user: UserProfileEntity) = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            val map = hashMapOf(
                "userId" to user.userId,
                "name" to user.name,
                "age" to user.age,
                "bio" to user.bio,
                "occupation" to user.occupation,
                "location" to user.location,
                "isVerified" to user.isVerified,
                "verificationPose" to user.verificationPose,
                "interests" to user.interests,
                "lastUpdated" to System.currentTimeMillis()
            )
            db.collection("users").document(user.userId)
                .set(map, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e(tag, "Failed to sync profile to Firestore", e)
        }
    }

    fun stopSync() {
        profilesListener?.remove()
        matchesListener?.remove()
        profilesListener = null
        matchesListener = null
    }
}
