package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.DatingProfileEntity
import com.example.ui.components.SparkAvatar
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.PassRed
import com.example.ui.theme.SparkCoral
import com.example.ui.theme.SparkPeach
import com.example.ui.theme.SparkViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    profile: DatingProfileEntity,
    messages: List<ChatMessageEntity>,
    onBack: () -> Unit,
    onSendMessage: (text: String) -> Unit,
    onSendVoiceNote: (durationSeconds: Int) -> Unit,
    onReportUser: () -> Unit,
    onStartVoiceInvite: () -> Unit
) {
    BackHandler { onBack() }

    var inputText by remember { mutableStateOf("") }
    var showIcebreakers by remember { mutableStateOf(false) }
    var isPlayingVoice by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    val icebreakers = listOf(
        "What's your absolute go-to comfort food in the city?",
        "If we could take off on a spontaneous weekend road trip right now, where to?",
        "What's the best live music concert you've ever seen?",
        "Pineapple on pizza: dealbreaker or delicious?",
        "Morning espresso routine or late-night dessert runs?"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .testTag("chat_detail_screen")
    ) {
        // Chat Top Bar
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SparkAvatar(
                        name = profile.name,
                        avatarDrawable = profile.avatarDrawable,
                        gradientStartHex = profile.gradientStartHex,
                        gradientEndHex = profile.gradientEndHex,
                        isVerified = profile.isVerified,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile.name.split(" ").first(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (profile.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                VerifiedBadge(sizeDp = 16)
                            }
                        }
                        Text(
                            text = "Active Now • ${profile.distanceKm} km away",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("chat_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                // Audio Call / Voice Lounge invite
                IconButton(onClick = onStartVoiceInvite, modifier = Modifier.testTag("chat_audio_call_button")) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Audio Stage Invite",
                        tint = SparkViolet
                    )
                }

                // Safety Shield (Report / Block)
                IconButton(onClick = onReportUser, modifier = Modifier.testTag("chat_report_button")) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Safety & Report",
                        tint = PassRed
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Safety tips banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = SparkCoral,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Safe dating reminder: Keep your contact info private until you feel comfortable.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }

        // Messages Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == "me"
                MessageBubble(
                    message = msg,
                    isMe = isMe,
                    isPlaying = isPlayingVoice == msg.messageId,
                    onToggleVoice = {
                        isPlayingVoice = if (isPlayingVoice == msg.messageId) null else msg.messageId
                    }
                )
            }
        }

        // Icebreakers Toggle Drawer
        AnimatedVisibility(visible = showIcebreakers) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = SparkCoral,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Icebreaker Starters",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = SparkCoral
                            )
                        }
                        Text(
                            text = "Tap to send",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(icebreakers) { icebreaker ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    SparkCoral.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.clickable {
                                    onSendMessage(icebreaker)
                                    showIcebreakers = false
                                }
                            ) {
                                Text(
                                    text = icebreaker,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Chat Input Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icebreaker lightbulb button
                IconButton(
                    onClick = { showIcebreakers = !showIcebreakers },
                    modifier = Modifier.testTag("chat_icebreakers_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Icebreakers",
                        tint = if (showIcebreakers) SparkCoral else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Voice Note button
                IconButton(
                    onClick = { onSendVoiceNote(7) },
                    modifier = Modifier.testTag("chat_voice_note_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Send Voice Note",
                        tint = SparkPeach
                    )
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Say something genuine...", fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilledIconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("chat_send_button"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = SparkCoral,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessageEntity,
    isMe: Boolean,
    isPlaying: Boolean,
    onToggleVoice: () -> Unit
) {
    val bubbleColor = if (isMe) SparkCoral else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isMe) 18.dp else 4.dp,
                    bottomEnd = if (isMe) 4.dp else 18.dp
                ),
                color = bubbleColor,
                modifier = Modifier.testTag("message_bubble_${message.messageId}")
            ) {
                if (message.isVoiceNote) {
                    // Voice Note Player bubble
                    Row(
                        modifier = Modifier
                            .clickable(onClick = onToggleVoice)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isMe) Color.White.copy(alpha = 0.25f) else SparkCoral.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Mic else Icons.Default.PlayArrow,
                                contentDescription = "Play voice note",
                                tint = if (isMe) Color.White else SparkCoral,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isPlaying) "Playing audio..." else "Voice Note",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor
                            )
                            Text(
                                text = "0:0${message.voiceDurationSeconds} • Waveform ready",
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha = 0.8f)
                            )
                        }
                    }
                } else {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))
                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
                if (isMe) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Read",
                        tint = if (message.isRead) Color(0xFF00C9FF) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}
