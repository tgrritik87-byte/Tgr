package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DatingDatabase
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.CommunityMessageEntity
import com.example.data.local.entities.CommunityRoomEntity
import com.example.data.local.entities.DatingProfileEntity
import com.example.data.local.entities.MatchEntity
import com.example.data.local.entities.UserProfileEntity
import com.example.data.repository.DatingRepository
import com.example.data.repository.DiscoveryFilterState
import com.example.ui.state.DatingGamesData
import com.example.ui.state.LiveVoiceRoom
import com.example.ui.state.VoiceRoomsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppTab {
    DISCOVERY,
    MATCHES_CHAT,
    COMMUNITY_ROOMS,
    VOICE_LOUNGE,
    GAMES_HUB,
    PROFILE_SAFETY
}

enum class ActiveGameType {
    NONE,
    WOULD_YOU_RATHER,
    TRUTH_OR_DARE,
    COMPATIBILITY_QUIZ
}

class DatingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DatingRepository
    private val authService: com.example.data.auth.FirebaseAuthService
    private val syncService: com.example.data.sync.FirestoreSyncService
    private val webRtcSession: com.example.data.webrtc.WebRtcAudioSession

    val authUserState: StateFlow<com.example.data.auth.AuthUserState>
    val syncState: StateFlow<com.example.data.sync.SyncState>
    val webRtcState: StateFlow<com.example.data.webrtc.WebRtcLoungeState>

    private val _authMessage = MutableStateFlow<String?>(null)
    val authMessage: StateFlow<String?> = _authMessage.asStateFlow()

    init {
        val db = DatingDatabase.getInstance(application)
        repository = DatingRepository(db.datingDao())
        authService = com.example.data.auth.FirebaseAuthService(application)
        syncService = com.example.data.sync.FirestoreSyncService(application, db.datingDao())
        syncState = syncService.syncState
        webRtcSession = com.example.data.webrtc.WebRtcAudioSession(application)
        webRtcState = webRtcSession.loungeState

        authUserState = authService.authStateFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            com.example.data.auth.AuthUserState(
                isAuthenticated = true,
                uid = "spark_verified_user_01",
                email = "jordan.rivera@sparkdating.app",
                displayName = "Jordan Rivera",
                isEmailVerified = true,
                isAnonymous = false,
                isFirebaseInitialized = false
            )
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.initializeIfEmpty()
            syncService.startRealtimeSync(viewModelScope)
        }
    }

    fun triggerManualSync() {
        viewModelScope.launch {
            syncService.pushLocalDataToFirestore()
        }
    }

    fun clearAuthMessage() {
        _authMessage.value = null
    }

    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            when (val res = authService.signInWithEmail(email, pass)) {
                is com.example.data.auth.AuthResult.Success -> {
                    _authMessage.value = "Welcome back, ${res.user.displayName ?: "Member"}!"
                }
                is com.example.data.auth.AuthResult.Error -> {
                    _authMessage.value = res.message
                }
            }
        }
    }

    fun signUpWithEmail(email: String, pass: String, name: String) {
        viewModelScope.launch {
            when (val res = authService.signUpWithEmail(email, pass, name)) {
                is com.example.data.auth.AuthResult.Success -> {
                    _authMessage.value = "Account created! Verification link sent to $email."
                    repository.completeVerification("Email & Biometric Verified")
                }
                is com.example.data.auth.AuthResult.Error -> {
                    _authMessage.value = res.message
                }
            }
        }
    }

    fun sendVerificationEmail() {
        viewModelScope.launch {
            val sent = authService.sendEmailVerification()
            _authMessage.value = if (sent) "Verification email sent! Check your inbox."
            else "Failed to send verification email."
        }
    }

    fun signInWithGoogle(activity: android.app.Activity) {
        viewModelScope.launch {
            when (val res = authService.launchGoogleSignIn(activity, null)) {
                is com.example.data.auth.AuthResult.Success -> {
                    _authMessage.value = "Signed in with Google as ${res.user.displayName}"
                    repository.completeVerification("Google Identity Verified")
                }
                is com.example.data.auth.AuthResult.Error -> {
                    _authMessage.value = res.message
                }
            }
        }
    }

    fun signOut() {
        authService.signOut()
        _authMessage.value = "Signed out"
    }

    // Navigation & Tabs
    private val _currentTab = MutableStateFlow(AppTab.DISCOVERY)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // Discovery Filters
    private val _filterState = MutableStateFlow(DiscoveryFilterState())
    val filterState: StateFlow<DiscoveryFilterState> = _filterState.asStateFlow()

    fun updateFilters(maxDistance: Int, minAge: Int, maxAge: Int, verifiedOnly: Boolean) {
        _filterState.value = _filterState.value.copy(
            maxDistanceKm = maxDistance,
            minAge = minAge,
            maxAge = maxAge,
            verifiedOnly = verifiedOnly
        )
    }

    // Discovery Candidate Profiles
    val filteredProfiles: StateFlow<List<DatingProfileEntity>> = combine(
        repository.getActiveProfiles(),
        _filterState
    ) { profiles, filters ->
        profiles.filter { profile ->
            profile.distanceKm <= filters.maxDistanceKm &&
                    profile.age >= filters.minAge &&
                    profile.age <= filters.maxAge &&
                    (!filters.verifiedOnly || profile.isVerified)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProfiles: StateFlow<List<DatingProfileEntity>> = repository.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mutual Match Celebration State
    private val _mutualMatchCelebration = MutableStateFlow<DatingProfileEntity?>(null)
    val mutualMatchCelebration: StateFlow<DatingProfileEntity?> = _mutualMatchCelebration.asStateFlow()

    fun dismissMatchCelebration() {
        _mutualMatchCelebration.value = null
    }

    fun swipe(profileId: String, swipeType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val matchedProfile = repository.handleSwipe(profileId, swipeType)
            if (matchedProfile != null) {
                _mutualMatchCelebration.value = matchedProfile
                val match = repository.getMatches().firstOrNull()?.find { it.profileId == matchedProfile.id }
                if (match != null) {
                    syncService.syncMatchToFirestore(match)
                }
            }
        }
    }

    fun undoSwipe() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.undoSwipe()
        }
    }

    // Matches & Mutual Chat
    val matches: StateFlow<List<MatchEntity>> = repository.getMatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeChatMatchId = MutableStateFlow<String?>(null)
    val activeChatMatchId: StateFlow<String?> = _activeChatMatchId.asStateFlow()

    private val _activeChatMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val activeChatMessages: StateFlow<List<ChatMessageEntity>> = _activeChatMessages.asStateFlow()

    fun openChat(matchId: String) {
        _activeChatMatchId.value = matchId
        viewModelScope.launch {
            repository.markMatchViewed(matchId)
            repository.getMessages(matchId).collect { msgs ->
                _activeChatMessages.value = msgs
            }
        }
    }

    fun closeChat() {
        _activeChatMatchId.value = null
    }

    fun sendTextMessage(matchId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendMessage(
                matchId = matchId,
                senderId = "me",
                text = text.trim()
            )
        }
    }

    fun sendVoiceNote(matchId: String, durationSeconds: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendMessage(
                matchId = matchId,
                senderId = "me",
                text = "Voice Note (${durationSeconds}s)",
                isVoiceNote = true,
                voiceDurationSeconds = durationSeconds
            )
        }
    }

    // Community Rooms
    val communityRooms: StateFlow<List<CommunityRoomEntity>> = repository.getCommunityRooms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeCommunityRoomId = MutableStateFlow<String?>(null)
    val activeCommunityRoomId: StateFlow<String?> = _activeCommunityRoomId.asStateFlow()

    private val _communityMessages = MutableStateFlow<List<CommunityMessageEntity>>(emptyList())
    val communityMessages: StateFlow<List<CommunityMessageEntity>> = _communityMessages.asStateFlow()

    fun openCommunityRoom(roomId: String) {
        _activeCommunityRoomId.value = roomId
        viewModelScope.launch {
            repository.getCommunityMessages(roomId).collect { msgs ->
                _communityMessages.value = msgs
            }
        }
    }

    fun closeCommunityRoom() {
        _activeCommunityRoomId.value = null
    }

    fun sendCommunityMessage(content: String) {
        val roomId = _activeCommunityRoomId.value ?: return
        if (content.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendCommunityMessage(
                roomId = roomId,
                senderName = "Jordan R. (You)",
                senderAvatarHex = 0xFFFF3B6F,
                senderIsVerified = true,
                content = content.trim()
            )
        }
    }

    // Voice Lounges (Dedicated Audio Rooms)
    val voiceRooms: StateFlow<List<LiveVoiceRoom>> = MutableStateFlow(VoiceRoomsData.sampleRooms).asStateFlow()

    private val _activeVoiceRoom = MutableStateFlow<LiveVoiceRoom?>(null)
    val activeVoiceRoom: StateFlow<LiveVoiceRoom?> = _activeVoiceRoom.asStateFlow()

    private val _isMicMuted = MutableStateFlow(true)
    val isMicMuted: StateFlow<Boolean> = _isMicMuted.asStateFlow()

    private val _isHandRaised = MutableStateFlow(false)
    val isHandRaised: StateFlow<Boolean> = _isHandRaised.asStateFlow()

    private val _floatingReactions = MutableStateFlow<List<String>>(emptyList())
    val floatingReactions: StateFlow<List<String>> = _floatingReactions.asStateFlow()

    fun joinVoiceRoom(room: LiveVoiceRoom) {
        _activeVoiceRoom.value = room
        _isMicMuted.value = true
        _isHandRaised.value = false
        webRtcSession.joinLounge(room.id, viewModelScope)
    }

    fun leaveVoiceRoom() {
        webRtcSession.leaveLounge()
        _activeVoiceRoom.value = null
        _isHandRaised.value = false
        _isMicMuted.value = true
    }

    fun toggleMute() {
        val newMuted = webRtcSession.toggleMute()
        _isMicMuted.value = newMuted
    }

    fun toggleSpeakerphone() {
        webRtcSession.toggleSpeakerphone()
    }

    fun toggleRaiseHand() {
        _isHandRaised.value = !_isHandRaised.value
    }

    fun triggerVoiceReaction(emoji: String) {
        _floatingReactions.value = _floatingReactions.value + emoji
    }

    // In-App Dating Games
    private val _selectedGame = MutableStateFlow(ActiveGameType.NONE)
    val selectedGame: StateFlow<ActiveGameType> = _selectedGame.asStateFlow()

    // Would You Rather state
    private val _wyrIndex = MutableStateFlow(0)
    val wyrIndex: StateFlow<Int> = _wyrIndex.asStateFlow()
    private val _wyrChoice = MutableStateFlow<String?>(null)
    val wyrChoice: StateFlow<String?> = _wyrChoice.asStateFlow()

    // Truth or Dare state
    private val _todIndex = MutableStateFlow(0)
    val todIndex: StateFlow<Int> = _todIndex.asStateFlow()

    // Compatibility Quiz state
    private val _quizQuestionIndex = MutableStateFlow(0)
    val quizQuestionIndex: StateFlow<Int> = _quizQuestionIndex.asStateFlow()
    private val _quizAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val quizAnswers: StateFlow<Map<Int, Int>> = _quizAnswers.asStateFlow()
    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted: StateFlow<Boolean> = _quizCompleted.asStateFlow()

    fun selectGame(game: ActiveGameType) {
        _selectedGame.value = game
        if (game == ActiveGameType.WOULD_YOU_RATHER) {
            _wyrChoice.value = null
        }
    }

    fun answerWyr(choice: String) {
        _wyrChoice.value = choice
    }

    fun nextWyr() {
        _wyrIndex.value = (_wyrIndex.value + 1) % DatingGamesData.wouldYouRatherList.size
        _wyrChoice.value = null
    }

    fun nextTruthOrDare() {
        _todIndex.value = (_todIndex.value + 1) % DatingGamesData.truthOrDareList.size
    }

    fun answerQuizQuestion(questionId: Int, optionIndex: Int) {
        val updated = _quizAnswers.value.toMutableMap()
        updated[questionId] = optionIndex
        _quizAnswers.value = updated

        if (_quizQuestionIndex.value < DatingGamesData.compatibilityQuestions.size - 1) {
            _quizQuestionIndex.value = _quizQuestionIndex.value + 1
        } else {
            _quizCompleted.value = true
        }
    }

    fun resetQuiz() {
        _quizQuestionIndex.value = 0
        _quizAnswers.value = emptyMap()
        _quizCompleted.value = false
    }

    // User Profile & Safety Features
    val userProfile: StateFlow<UserProfileEntity?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateUserProfile(name: String, bio: String, occupation: String, location: String) {
        val current = userProfile.value ?: return
        val updated = current.copy(
            name = name,
            bio = bio,
            occupation = occupation,
            location = location
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserProfile(updated)
            syncService.syncUserProfileToFirestore(updated)
        }
    }

    fun completeVerification(poseName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.completeVerification(poseName)
            val current = repository.getUserProfile().firstOrNull()
            if (current != null) {
                syncService.syncUserProfileToFirestore(current)
            }
        }
    }

    fun toggleIncognito() {
        val current = userProfile.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserProfile(
                current.copy(incognitoMode = !current.incognitoMode)
            )
        }
    }

    // Safety: Block & Report
    private val _profileToReport = MutableStateFlow<DatingProfileEntity?>(null)
    val profileToReport: StateFlow<DatingProfileEntity?> = _profileToReport.asStateFlow()

    fun promptReport(profile: DatingProfileEntity) {
        _profileToReport.value = profile
    }

    fun dismissReportPrompt() {
        _profileToReport.value = null
    }

    fun submitReportAndBlock(profileId: String, profileName: String, reason: String, details: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.reportUser(profileId, profileName, reason, details)
            _profileToReport.value = null
            // Also if active chat was open for this profile's match, close it
            _activeChatMatchId.value = null
        }
    }

    fun blockUser(profileId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.blockUser(profileId)
            _activeChatMatchId.value = null
        }
    }
}
