package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.CommunityMessageEntity
import com.example.data.local.entities.CommunityRoomEntity
import com.example.data.local.entities.DatingProfileEntity
import com.example.data.local.entities.MatchEntity
import com.example.data.local.entities.SafetyReportEntity
import com.example.data.local.entities.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DatingDao {

    // Dating Profiles (Discovery Deck)
    @Query("SELECT * FROM dating_profiles WHERE isSwiped = 0 AND isBlocked = 0")
    fun getActiveProfiles(): Flow<List<DatingProfileEntity>>

    @Query("SELECT * FROM dating_profiles WHERE id = :profileId LIMIT 1")
    suspend fun getProfileById(profileId: String): DatingProfileEntity?

    @Query("SELECT * FROM dating_profiles WHERE isBlocked = 0")
    fun getAllProfiles(): Flow<List<DatingProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProfiles(profiles: List<DatingProfileEntity>)

    @Update
    suspend fun updateProfile(profile: DatingProfileEntity)

    @Query("UPDATE dating_profiles SET isSwiped = 1, swipeType = :swipeType WHERE id = :profileId")
    suspend fun recordSwipe(profileId: String, swipeType: String)

    @Query("UPDATE dating_profiles SET isSwiped = 0, swipeType = NULL WHERE id = (SELECT id FROM dating_profiles WHERE isSwiped = 1 ORDER BY ROWID DESC LIMIT 1)")
    suspend fun undoLastSwipe()

    @Query("UPDATE dating_profiles SET isBlocked = 1 WHERE id = :profileId")
    suspend fun blockProfile(profileId: String)

    // Matches
    @Query("SELECT * FROM matches ORDER BY matchedAt DESC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE matchId = :matchId LIMIT 1")
    suspend fun getMatchById(matchId: String): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)

    @Query("DELETE FROM matches WHERE profileId = :profileId")
    suspend fun deleteMatchByProfileId(profileId: String)

    @Query("UPDATE matches SET isNewMatch = 0 WHERE matchId = :matchId")
    suspend fun markMatchViewed(matchId: String)

    @Query("UPDATE matches SET lastMessage = :lastMessage, lastMessageTimestamp = :timestamp, unreadCount = 0 WHERE matchId = :matchId")
    suspend fun updateMatchLastMessage(matchId: String, lastMessage: String, timestamp: Long)

    // Mutual Chat Messages
    @Query("SELECT * FROM chat_messages WHERE matchId = :matchId ORDER BY timestamp ASC")
    fun getMessagesForMatch(matchId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE matchId = :matchId")
    suspend fun deleteMessagesForMatch(matchId: String)

    // Community Rooms
    @Query("SELECT * FROM community_rooms")
    fun getAllCommunityRooms(): Flow<List<CommunityRoomEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCommunityRooms(rooms: List<CommunityRoomEntity>)

    @Query("SELECT * FROM community_messages WHERE roomId = :roomId ORDER BY timestamp ASC")
    fun getCommunityMessages(roomId: String): Flow<List<CommunityMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityMessage(message: CommunityMessageEntity)

    // Safety Reports
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSafetyReport(report: SafetyReportEntity)

    @Query("SELECT * FROM safety_reports ORDER BY timestamp DESC")
    fun getAllSafetyReports(): Flow<List<SafetyReportEntity>>

    // User Profile & Preferences
    @Query("SELECT * FROM user_profile WHERE userId = 'current_user' LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateUserProfile(profile: UserProfileEntity)
}
