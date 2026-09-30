package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ActiveGameType
import com.example.ui.AppTab
import com.example.ui.DatingViewModel
import com.example.ui.components.DiscoveryFilterDialog
import com.example.ui.components.ReportBlockDialog
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.CommunityRoomsScreen
import com.example.ui.screens.DiscoveryScreen
import com.example.ui.screens.GamesHubScreen
import com.example.ui.screens.MatchCelebrationDialog
import com.example.ui.screens.MatchesAndChatScreen
import com.example.ui.screens.ProfileAndSafetyScreen
import com.example.ui.screens.VoiceRoomsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SparkCoral

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SparkDatingApp()
            }
        }
    }
}

@Composable
fun SparkDatingApp(datingViewModel: DatingViewModel = viewModel()) {
    val currentTab by datingViewModel.currentTab.collectAsStateWithLifecycle()
    val profiles by datingViewModel.filteredProfiles.collectAsStateWithLifecycle()
    val allProfiles by datingViewModel.allProfiles.collectAsStateWithLifecycle()
    val matches by datingViewModel.matches.collectAsStateWithLifecycle()
    val activeChatMatchId by datingViewModel.activeChatMatchId.collectAsStateWithLifecycle()
    val activeChatMessages by datingViewModel.activeChatMessages.collectAsStateWithLifecycle()
    val mutualMatchCelebration by datingViewModel.mutualMatchCelebration.collectAsStateWithLifecycle()

    val communityRooms by datingViewModel.communityRooms.collectAsStateWithLifecycle()
    val activeCommunityRoomId by datingViewModel.activeCommunityRoomId.collectAsStateWithLifecycle()
    val communityMessages by datingViewModel.communityMessages.collectAsStateWithLifecycle()

    val voiceRooms by datingViewModel.voiceRooms.collectAsStateWithLifecycle()
    val activeVoiceRoom by datingViewModel.activeVoiceRoom.collectAsStateWithLifecycle()
    val isMicMuted by datingViewModel.isMicMuted.collectAsStateWithLifecycle()
    val isHandRaised by datingViewModel.isHandRaised.collectAsStateWithLifecycle()
    val voiceReactions by datingViewModel.floatingReactions.collectAsStateWithLifecycle()

    val selectedGame by datingViewModel.selectedGame.collectAsStateWithLifecycle()
    val wyrIndex by datingViewModel.wyrIndex.collectAsStateWithLifecycle()
    val wyrChoice by datingViewModel.wyrChoice.collectAsStateWithLifecycle()
    val todIndex by datingViewModel.todIndex.collectAsStateWithLifecycle()
    val quizIndex by datingViewModel.quizQuestionIndex.collectAsStateWithLifecycle()
    val quizAnswers by datingViewModel.quizAnswers.collectAsStateWithLifecycle()
    val quizCompleted by datingViewModel.quizCompleted.collectAsStateWithLifecycle()

    val userProfile by datingViewModel.userProfile.collectAsStateWithLifecycle()
    val filterState by datingViewModel.filterState.collectAsStateWithLifecycle()
    val profileToReport by datingViewModel.profileToReport.collectAsStateWithLifecycle()
    val authUserState by datingViewModel.authUserState.collectAsStateWithLifecycle()
    val authMessage by datingViewModel.authMessage.collectAsStateWithLifecycle()
    val syncState by datingViewModel.syncState.collectAsStateWithLifecycle()
    val webRtcState by datingViewModel.webRtcState.collectAsStateWithLifecycle()

    var showFiltersDialog by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }

    val totalUnread = matches.sumOf { it.unreadCount }

    // If active chat is open with a match, find the matched profile
    val activeChatMatch = matches.find { it.matchId == activeChatMatchId }
    val activeChatProfile = activeChatMatch?.let { m -> allProfiles.find { it.id == m.profileId } }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Hide bottom nav when inside a direct 1-on-1 chat or full voice room stage
            if (activeChatMatchId == null && activeVoiceRoom == null && activeCommunityRoomId == null) {
                NavigationBar(
                    modifier = Modifier.testTag("main_bottom_nav"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == AppTab.DISCOVERY,
                        onClick = { datingViewModel.selectTab(AppTab.DISCOVERY) },
                        icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = "Discover") },
                        label = { Text("Discover", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = SparkCoral, indicatorColor = SparkCoral.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_tab_discovery")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.MATCHES_CHAT,
                        onClick = { datingViewModel.selectTab(AppTab.MATCHES_CHAT) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (totalUnread > 0) {
                                        Badge { Text("$totalUnread") }
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.ChatBubble, contentDescription = "Chats")
                            }
                        },
                        label = { Text("Chats", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = SparkCoral, indicatorColor = SparkCoral.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_tab_chat")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.VOICE_LOUNGE,
                        onClick = { datingViewModel.selectTab(AppTab.VOICE_LOUNGE) },
                        icon = { Icon(imageVector = Icons.Default.Headphones, contentDescription = "Voice Lounges") },
                        label = { Text("Voice", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = SparkCoral, indicatorColor = SparkCoral.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_tab_voice")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.COMMUNITY_ROOMS,
                        onClick = { datingViewModel.selectTab(AppTab.COMMUNITY_ROOMS) },
                        icon = { Icon(imageVector = Icons.Default.Groups, contentDescription = "Community") },
                        label = { Text("Rooms", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = SparkCoral, indicatorColor = SparkCoral.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_tab_community")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.GAMES_HUB,
                        onClick = { datingViewModel.selectTab(AppTab.GAMES_HUB) },
                        icon = { Icon(imageVector = Icons.Default.Casino, contentDescription = "Games") },
                        label = { Text("Games", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = SparkCoral, indicatorColor = SparkCoral.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_tab_games")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.PROFILE_SAFETY,
                        onClick = { datingViewModel.selectTab(AppTab.PROFILE_SAFETY) },
                        icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile & Safety") },
                        label = { Text("Profile", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = SparkCoral, indicatorColor = SparkCoral.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_tab_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeChatMatchId != null && activeChatProfile != null) {
                ChatDetailScreen(
                    profile = activeChatProfile,
                    messages = activeChatMessages,
                    onBack = { datingViewModel.closeChat() },
                    onSendMessage = { text ->
                        datingViewModel.sendTextMessage(activeChatMatchId!!, text)
                    },
                    onSendVoiceNote = { duration ->
                        datingViewModel.sendVoiceNote(activeChatMatchId!!, duration)
                    },
                    onReportUser = {
                        datingViewModel.promptReport(activeChatProfile)
                    },
                    onStartVoiceInvite = {
                        datingViewModel.selectTab(AppTab.VOICE_LOUNGE)
                        datingViewModel.closeChat()
                    }
                )
            } else {
                when (currentTab) {
                    AppTab.DISCOVERY -> {
                        DiscoveryScreen(
                            profiles = profiles,
                            onSwipe = { profileId, swipeType ->
                                datingViewModel.swipe(profileId, swipeType)
                            },
                            onUndoSwipe = { datingViewModel.undoSwipe() },
                            onReportUser = { profile ->
                                datingViewModel.promptReport(profile)
                            },
                            onBlockUser = { profileId ->
                                datingViewModel.blockUser(profileId)
                            },
                            onOpenFilters = { showFiltersDialog = true }
                        )
                    }

                    AppTab.MATCHES_CHAT -> {
                        MatchesAndChatScreen(
                            matches = matches,
                            allProfiles = allProfiles,
                            onOpenChat = { matchId -> datingViewModel.openChat(matchId) },
                            onExploreDiscovery = { datingViewModel.selectTab(AppTab.DISCOVERY) }
                        )
                    }

                    AppTab.VOICE_LOUNGE -> {
                        VoiceRoomsScreen(
                            rooms = voiceRooms,
                            activeRoom = activeVoiceRoom,
                            isMuted = isMicMuted,
                            isHandRaised = isHandRaised,
                            floatingReactions = voiceReactions,
                            webRtcState = webRtcState,
                            onJoinRoom = { room -> datingViewModel.joinVoiceRoom(room) },
                            onLeaveRoom = { datingViewModel.leaveVoiceRoom() },
                            onToggleMute = { datingViewModel.toggleMute() },
                            onToggleRaiseHand = { datingViewModel.toggleRaiseHand() },
                            onToggleSpeakerphone = { datingViewModel.toggleSpeakerphone() },
                            onSendReaction = { emoji -> datingViewModel.triggerVoiceReaction(emoji) }
                        )
                    }

                    AppTab.COMMUNITY_ROOMS -> {
                        CommunityRoomsScreen(
                            rooms = communityRooms,
                            activeRoomId = activeCommunityRoomId,
                            messages = communityMessages,
                            onOpenRoom = { roomId -> datingViewModel.openCommunityRoom(roomId) },
                            onCloseRoom = { datingViewModel.closeCommunityRoom() },
                            onSendMessage = { text -> datingViewModel.sendCommunityMessage(text) }
                        )
                    }

                    AppTab.GAMES_HUB -> {
                        GamesHubScreen(
                            activeGame = selectedGame,
                            onSelectGame = { game -> datingViewModel.selectGame(game) },
                            wyrIndex = wyrIndex,
                            wyrChoice = wyrChoice,
                            onAnswerWyr = { choice -> datingViewModel.answerWyr(choice) },
                            onNextWyr = { datingViewModel.nextWyr() },
                            todIndex = todIndex,
                            onNextTod = { datingViewModel.nextTruthOrDare() },
                            quizIndex = quizIndex,
                            quizAnswers = quizAnswers,
                            quizCompleted = quizCompleted,
                            onAnswerQuiz = { qId, optIndex -> datingViewModel.answerQuizQuestion(qId, optIndex) },
                            onResetQuiz = { datingViewModel.resetQuiz() },
                            onShareToChat = { messageText ->
                                val firstMatch = matches.firstOrNull()
                                if (firstMatch != null) {
                                    datingViewModel.sendTextMessage(firstMatch.matchId, messageText)
                                    datingViewModel.openChat(firstMatch.matchId)
                                } else {
                                    datingViewModel.selectTab(AppTab.DISCOVERY)
                                }
                            }
                        )
                    }

                    AppTab.PROFILE_SAFETY -> {
                        ProfileAndSafetyScreen(
                            userProfile = userProfile,
                            authUserState = authUserState,
                            syncState = syncState,
                            onUpdateProfile = { name, bio, occupation, loc ->
                                datingViewModel.updateUserProfile(name, bio, occupation, loc)
                            },
                            onCompleteVerification = { pose ->
                                datingViewModel.completeVerification(pose)
                            },
                            onToggleIncognito = {
                                datingViewModel.toggleIncognito()
                            },
                            onOpenAuthDialog = { showAuthDialog = true },
                            onSendVerificationEmail = { datingViewModel.sendVerificationEmail() },
                            onSignOut = { datingViewModel.signOut() },
                            onTriggerSync = { datingViewModel.triggerManualSync() }
                        )
                    }
                }
            }
        }
    }

    // Mutual Match Celebration Dialog
    mutualMatchCelebration?.let { profile ->
        MatchCelebrationDialog(
            matchedProfile = profile,
            onSendMessage = {
                datingViewModel.dismissMatchCelebration()
                // Find or wait for match
                val createdMatch = matches.find { it.profileId == profile.id }
                if (createdMatch != null) {
                    datingViewModel.openChat(createdMatch.matchId)
                } else {
                    datingViewModel.selectTab(AppTab.MATCHES_CHAT)
                }
            },
            onKeepSwiping = {
                datingViewModel.dismissMatchCelebration()
            }
        )
    }

    // Discovery Filters Dialog
    if (showFiltersDialog) {
        DiscoveryFilterDialog(
            initialFilters = filterState,
            onDismiss = { showFiltersDialog = false },
            onApply = { distance, minAge, maxAge, verifiedOnly ->
                datingViewModel.updateFilters(distance, minAge, maxAge, verifiedOnly)
                showFiltersDialog = false
            }
        )
    }

    // Report and Block Dialog
    profileToReport?.let { profile ->
        ReportBlockDialog(
            profile = profile,
            onDismiss = { datingViewModel.dismissReportPrompt() },
            onSubmitReport = { reason, details ->
                datingViewModel.submitReportAndBlock(profile.id, profile.name, reason, details)
            },
            onJustBlock = {
                datingViewModel.blockUser(profile.id)
                datingViewModel.dismissReportPrompt()
            }
        )
    }

    // Firebase Auth Dialog
    if (showAuthDialog) {
        com.example.ui.components.AuthDialog(
            onDismiss = { showAuthDialog = false },
            onSignInEmail = { email, pass -> datingViewModel.signInWithEmail(email, pass) },
            onSignUpEmail = { email, pass, name -> datingViewModel.signUpWithEmail(email, pass, name) },
            onGoogleSignIn = { activity -> datingViewModel.signInWithGoogle(activity) }
        )
    }

    // Auth Status Message Dialog
    authMessage?.let { msg ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { datingViewModel.clearAuthMessage() },
            title = { Text("Account Notice", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
            text = { Text(msg) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { datingViewModel.clearAuthMessage() }) {
                    Text("OK")
                }
            }
        )
    }
}
