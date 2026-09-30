package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FrontHand
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.InterestChip
import com.example.ui.components.SparkAvatar
import com.example.ui.components.VerifiedBadge
import com.example.ui.state.LiveVoiceRoom
import com.example.ui.state.VoiceParticipant
import com.example.ui.theme.LikeGreen
import com.example.ui.theme.PassRed
import com.example.ui.theme.SparkCoral
import com.example.ui.theme.SparkGold
import com.example.ui.theme.SparkPeach
import com.example.ui.theme.SparkViolet

@Composable
fun VoiceRoomsScreen(
    rooms: List<LiveVoiceRoom>,
    activeRoom: LiveVoiceRoom?,
    isMuted: Boolean,
    isHandRaised: Boolean,
    floatingReactions: List<String>,
    webRtcState: com.example.data.webrtc.WebRtcLoungeState? = null,
    onJoinRoom: (LiveVoiceRoom) -> Unit,
    onLeaveRoom: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleRaiseHand: () -> Unit,
    onToggleSpeakerphone: () -> Unit = {},
    onSendReaction: (String) -> Unit
) {
    if (activeRoom != null) {
        ActiveVoiceRoomStage(
            room = activeRoom,
            isMuted = isMuted,
            isHandRaised = isHandRaised,
            floatingReactions = floatingReactions,
            webRtcState = webRtcState,
            onLeave = onLeaveRoom,
            onToggleMute = onToggleMute,
            onToggleRaiseHand = onToggleRaiseHand,
            onToggleSpeakerphone = onToggleSpeakerphone,
            onSendReaction = onSendReaction
        )
    } else {
        VoiceRoomsList(
            rooms = rooms,
            onJoinRoom = onJoinRoom
        )
    }
}

@Composable
fun VoiceRoomsList(
    rooms: List<LiveVoiceRoom>,
    onJoinRoom: (LiveVoiceRoom) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("voice_rooms_list_screen")
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Audio Lounges",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SparkViolet.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(LikeGreen)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LIVE",
                            color = SparkViolet,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Text(
                text = "Drop in to live audio speed dating, casual banter & late night music lounges.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(rooms) { room ->
                VoiceRoomCard(room = room, onJoin = { onJoinRoom(room) })
            }
        }
    }
}

@Composable
fun VoiceRoomCard(
    room: LiveVoiceRoom,
    onJoin: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onJoin)
            .testTag("voice_room_card_${room.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = room.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = room.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = room.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = SparkCoral,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${room.speakerCount + room.listenerCount}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = room.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Speakers Preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-8).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    room.speakers.take(3).forEach { spk ->
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        ) {
                            SparkAvatar(
                                name = spk.name,
                                avatarDrawable = null,
                                gradientStartHex = spk.avatarHex,
                                gradientEndHex = spk.avatarHex,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    if (room.speakers.size > 3) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${room.speakers.size - 3}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Button(
                    onClick = onJoin,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SparkCoral)
                ) {
                    Text("Join Stage", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ActiveVoiceRoomStage(
    room: LiveVoiceRoom,
    isMuted: Boolean,
    isHandRaised: Boolean,
    floatingReactions: List<String>,
    webRtcState: com.example.data.webrtc.WebRtcLoungeState?,
    onLeave: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleRaiseHand: () -> Unit,
    onToggleSpeakerphone: () -> Unit,
    onSendReaction: (String) -> Unit
) {
    BackHandler { onLeave() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("active_voice_room_screen")
    ) {
        // Stage Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onLeave) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Leave"
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = room.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${room.speakerCount} Speakers • ${room.listenerCount} In Audience",
                    style = MaterialTheme.typography.labelSmall,
                    color = SparkCoral
                )
            }

            Button(
                onClick = onLeave,
                colors = ButtonDefaults.buttonColors(containerColor = PassRed.copy(alpha = 0.2f), contentColor = PassRed),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Leave", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // WebRTC Live Connection & Audio Settings Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(LikeGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WebRTC Audio: Live • Opus 48kHz",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = LikeGreen
                    )
                }

                IconButton(
                    onClick = onToggleSpeakerphone,
                    modifier = Modifier.size(28.dp).testTag("speakerphone_toggle_button")
                ) {
                    Icon(
                        imageVector = if (webRtcState?.isSpeakerphoneOn != false) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Speakerphone",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Live Audio Wave Visualizer
        LiveAudioWaveVisualizer(
            audioLevel = webRtcState?.localAudioLevel ?: 0f,
            isMuted = isMuted
        )

        // Floating reactions display
        if (floatingReactions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = floatingReactions.takeLast(4).joinToString(" "),
                    fontSize = 22.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Stage / Speakers Section
            item {
                Text(
                    text = "SPEAKERS ON STAGE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(room.speakers) { spk ->
                        SpeakerAvatarItem(participant = spk)
                    }
                }
            }

            // Audience / Listeners Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "AUDIENCE (${room.listeners.size})",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(room.listeners) { lis ->
                        ListenerAvatarItem(participant = lis)
                    }
                }
            }
        }

        // Voice Controls Bottom Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reaction Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("❤️", "🔥", "👏", "✨").forEach { emoji ->
                        FilledIconButton(
                            onClick = { onSendReaction(emoji) },
                            modifier = Modifier.size(42.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(text = emoji, fontSize = 18.sp)
                        }
                    }
                }

                // Stage Actions: Hand Raise & Mic
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilledIconButton(
                        onClick = onToggleRaiseHand,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("voice_raise_hand_button"),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isHandRaised) SparkGold else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isHandRaised) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(imageVector = Icons.Default.FrontHand, contentDescription = "Raise Hand")
                    }

                    FilledIconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("voice_mute_toggle_button"),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isMuted) PassRed.copy(alpha = 0.2f) else LikeGreen,
                            contentColor = if (isMuted) PassRed else Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (isMuted) "Unmute" else "Mute"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpeakerAvatarItem(participant: VoiceParticipant) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(66.dp)
                .then(
                    if (participant.isSpeaking) {
                        Modifier
                            .scale(pulseScale)
                            .border(3.dp, LikeGreen, CircleShape)
                            .padding(3.dp)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            SparkAvatar(
                name = participant.name,
                avatarDrawable = null,
                gradientStartHex = participant.avatarHex,
                gradientEndHex = participant.avatarHex,
                modifier = Modifier.fillMaxSize()
            )

            if (participant.isMuted) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MicOff,
                        contentDescription = "Muted",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = participant.name.split(" ").first(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        if (participant.isHost) {
            Text(
                text = "Host",
                style = MaterialTheme.typography.labelSmall,
                color = SparkCoral,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun ListenerAvatarItem(participant: VoiceParticipant) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(modifier = Modifier.size(46.dp)) {
            SparkAvatar(
                name = participant.name,
                avatarDrawable = null,
                gradientStartHex = participant.avatarHex,
                gradientEndHex = participant.avatarHex,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = participant.name.split(" ").first(),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}

@Composable
fun LiveAudioWaveVisualizer(audioLevel: Float, isMuted: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp)
            .padding(horizontal = 28.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val barCount = 20
        for (i in 0 until barCount) {
            val factor = if (!isMuted) {
                (0.2f + 0.8f * kotlin.math.abs(kotlin.math.sin((i * 0.45f) + (audioLevel * 8f)))).coerceIn(0.15f, 1f)
            } else {
                0.15f
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .width(3.dp)
                    .height((20.dp * factor))
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (!isMuted) Brush.verticalGradient(listOf(SparkCoral, SparkViolet))
                        else Brush.verticalGradient(listOf(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.outline))
                    )
            )
        }
    }
}
