package com.example.data.repository

import com.example.data.InitialData
import com.example.data.local.dao.DatingDao
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.CommunityMessageEntity
import com.example.data.local.entities.CommunityRoomEntity
import com.example.data.local.entities.DatingProfileEntity
import com.example.data.local.entities.MatchEntity
import com.example.data.local.entities.SafetyReportEntity
import com.example.data.local.entities.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

data class DiscoveryFilterState(
    val maxDistanceKm: Int = 30,
    val minAge: Int = 18,
    val maxAge: Int = 35,
    val verifiedOnly: Boolean = false,
    val selectedInterest: String? = null
)

class DatingRepository(private val datingDao: DatingDao) {

    suspend fun initializeIfEmpty() {
        val existing = datingDao.getAllProfiles().firstOrNull()
        if (existing.isNullOrEmpty()) {
            datingDao.insertUserProfile(InitialData.defaultUser)
            datingDao.insertProfiles(InitialData.profiles)
            datingDao.insertCommunityRooms(InitialData.communityRooms)
            for (msg in InitialData.initialCommunityMessages) {
                datingDao.insertCommunityMessage(msg)
            }
            datingDao.insertMatch(InitialData.initialMatch)
            for (msg in InitialData.initialMessages) {
                datingDao.insertMessage(msg)
            }
        }
    }

    fun getActiveProfiles(): Flow<List<DatingProfileEntity>> = datingDao.getActiveProfiles()

    fun getAllProfiles(): Flow<List<DatingProfileEntity>> = datingDao.getAllProfiles()

    suspend fun getProfile(profileId: String): DatingProfileEntity? = datingDao.getProfileById(profileId)

    /**
     * Swipes on a profile.
     * Returns the matched DatingProfileEntity if a mutual match occurs, otherwise null.
     */
    suspend fun handleSwipe(profileId: String, swipeType: String): DatingProfileEntity? {
        val profile = datingDao.getProfileById(profileId)
        datingDao.recordSwipe(profileId, swipeType)

        if ((swipeType == "LIKE" || swipeType == "SUPERLIKE") && profile != null && profile.likedCurrentUser) {
            val matchId = "match_${profile.id}_${System.currentTimeMillis()}"
            val newMatch = MatchEntity(
                matchId = matchId,
                profileId = profile.id,
                matchedAt = System.currentTimeMillis(),
                lastMessage = if (swipeType == "SUPERLIKE") "⭐ Sent a Super Like to you!" else "You matched! Say hello ✨",
                lastMessageTimestamp = System.currentTimeMillis(),
                isNewMatch = true,
                unreadCount = 0
            )
            datingDao.insertMatch(newMatch)
            return profile
        }
        return null
    }

    suspend fun undoSwipe() {
        datingDao.undoLastSwipe()
    }

    suspend fun blockUser(profileId: String) {
        datingDao.blockProfile(profileId)
        datingDao.deleteMatchByProfileId(profileId)
    }

    suspend fun reportUser(profileId: String, profileName: String, reason: String, details: String) {
        val report = SafetyReportEntity(
            reportedProfileId = profileId,
            reportedProfileName = profileName,
            reason = reason,
            details = details,
            timestamp = System.currentTimeMillis()
        )
        datingDao.insertSafetyReport(report)
        // Auto block on report for safety
        datingDao.blockProfile(profileId)
        datingDao.deleteMatchByProfileId(profileId)
    }

    // Mutual Match Chat
    fun getMatches(): Flow<List<MatchEntity>> = datingDao.getAllMatches()

    suspend fun getMatch(matchId: String): MatchEntity? = datingDao.getMatchById(matchId)

    fun getMessages(matchId: String): Flow<List<ChatMessageEntity>> = datingDao.getMessagesForMatch(matchId)

    suspend fun sendMessage(
        matchId: String,
        senderId: String,
        text: String,
        isVoiceNote: Boolean = false,
        voiceDurationSeconds: Int = 0
    ) {
        val msg = ChatMessageEntity(
            messageId = UUID.randomUUID().toString(),
            matchId = matchId,
            senderId = senderId,
            text = text,
            timestamp = System.currentTimeMillis(),
            isVoiceNote = isVoiceNote,
            voiceDurationSeconds = voiceDurationSeconds,
            isRead = true
        )
        datingDao.insertMessage(msg)
        val preview = if (isVoiceNote) "🎙️ Voice note (${voiceDurationSeconds}s)" else text
        datingDao.updateMatchLastMessage(matchId, preview, System.currentTimeMillis())
    }

    suspend fun markMatchViewed(matchId: String) {
        datingDao.markMatchViewed(matchId)
    }

    // Community Rooms
    fun getCommunityRooms(): Flow<List<CommunityRoomEntity>> = datingDao.getAllCommunityRooms()

    fun getCommunityMessages(roomId: String): Flow<List<CommunityMessageEntity>> =
        datingDao.getCommunityMessages(roomId)

    suspend fun sendCommunityMessage(
        roomId: String,
        senderName: String,
        senderAvatarHex: Long,
        senderIsVerified: Boolean,
        content: String
    ) {
        val msg = CommunityMessageEntity(
            roomId = roomId,
            senderName = senderName,
            senderAvatarHex = senderAvatarHex,
            senderIsVerified = senderIsVerified,
            content = content,
            timestamp = System.currentTimeMillis(),
            likesCount = 0
        )
        datingDao.insertCommunityMessage(msg)
    }

    // User Profile & Settings
    fun getUserProfile(): Flow<UserProfileEntity?> = datingDao.getUserProfile()

    suspend fun updateUserProfile(profile: UserProfileEntity) {
        datingDao.updateUserProfile(profile)
    }

    suspend fun completeVerification(poseName: String) {
        val current = datingDao.getUserProfile().firstOrNull() ?: InitialData.defaultUser
        val updated = current.copy(isVerified = true, verificationPose = poseName)
        datingDao.updateUserProfile(updated)
    }
}
