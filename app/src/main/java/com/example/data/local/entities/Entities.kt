package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dating_profiles")
data class DatingProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val age: Int,
    val gender: String,
    val distanceKm: Int,
    val locationName: String,
    val occupation: String,
    val companyOrSchool: String,
    val isVerified: Boolean,
    val verificationMethod: String,
    val bio: String,
    val prompts: String, // format: "PromptQ||PromptA///PromptQ2||PromptA2"
    val interests: String, // comma separated: "Coffee, Hiking, Indie Rock"
    val relationshipIntent: String,
    val avatarDrawable: String, // drawable name or identifier
    val gradientStartHex: Long,
    val gradientEndHex: Long,
    val likedCurrentUser: Boolean,
    val isSwiped: Boolean = false,
    val swipeType: String? = null, // "LIKE", "PASS", "SUPERLIKE"
    val isBlocked: Boolean = false,
    val heightCm: Int = 172,
    val zodiac: String = "Leo",
    val pets: String = "Dog Lover"
)

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val matchId: String,
    val profileId: String,
    val matchedAt: Long,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
    val isNewMatch: Boolean = true,
    val unreadCount: Int = 0
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val messageId: String,
    val matchId: String,
    val senderId: String, // "me" or profileId
    val text: String,
    val timestamp: Long,
    val isVoiceNote: Boolean = false,
    val voiceDurationSeconds: Int = 0,
    val isRead: Boolean = true
)

@Entity(tableName = "community_rooms")
data class CommunityRoomEntity(
    @PrimaryKey val roomId: String,
    val title: String,
    val category: String,
    val description: String,
    val emoji: String,
    val activeUsersCount: Int,
    val tags: String
)

@Entity(tableName = "community_messages")
data class CommunityMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomId: String,
    val senderName: String,
    val senderAvatarHex: Long,
    val senderIsVerified: Boolean,
    val content: String,
    val timestamp: Long,
    val likesCount: Int = 0
)

@Entity(tableName = "safety_reports")
data class SafetyReportEntity(
    @PrimaryKey(autoGenerate = true) val reportId: Long = 0,
    val reportedProfileId: String,
    val reportedProfileName: String,
    val reason: String,
    val details: String,
    val timestamp: Long
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val userId: String = "current_user",
    val name: String = "Jordan Rivera",
    val age: Int = 25,
    val bio: String = "Architect by day, coffee aficionado by weekend. Seeking someone who enjoys spontaneous road trips, live concerts, and good conversations.",
    val occupation: String = "UX & Spatial Designer",
    val location: String = "San Francisco, CA",
    val isVerified: Boolean = true,
    val verificationPose: String = "Selfie Tilt & Smile",
    val maxDistanceKm: Int = 30,
    val minAge: Int = 21,
    val maxAge: Int = 32,
    val verifiedOnly: Boolean = false,
    val incognitoMode: Boolean = false,
    val interests: String = "Architecture, Live Music, Specialty Coffee, Bouldering, Film Photography"
)
