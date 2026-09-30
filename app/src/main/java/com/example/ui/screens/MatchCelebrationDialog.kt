package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entities.DatingProfileEntity
import com.example.ui.components.SparkAvatar
import com.example.ui.theme.SparkCoral
import com.example.ui.theme.SparkPeach
import com.example.ui.theme.SparkViolet

@Composable
fun MatchCelebrationDialog(
    matchedProfile: DatingProfileEntity,
    onSendMessage: () -> Unit,
    onKeepSwiping: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "heartBeat")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartScale"
    )

    Dialog(
        onDismissRequest = onKeepSwiping,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(28.dp))
                .testTag("match_celebration_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(
                2.dp,
                Brush.linearGradient(listOf(SparkCoral, SparkViolet))
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Sparkle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Sparks",
                        tint = SparkCoral,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "IT'S A MUTUAL MATCH!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = SparkCoral,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You and ${matchedProfile.name} liked each other! Spark something special together.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Photos Side-by-Side with beating heart
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Avatar
                        SparkAvatar(
                            name = "Jordan Rivera",
                            avatarDrawable = null,
                            gradientStartHex = 0xFFFF5E62,
                            gradientEndHex = 0xFFFF9966,
                            isVerified = true,
                            modifier = Modifier.size(92.dp)
                        )

                        Spacer(modifier = Modifier.width(36.dp))

                        // Matched Profile Avatar
                        SparkAvatar(
                            name = matchedProfile.name,
                            avatarDrawable = matchedProfile.avatarDrawable,
                            gradientStartHex = matchedProfile.gradientStartHex,
                            gradientEndHex = matchedProfile.gradientEndHex,
                            isVerified = matchedProfile.isVerified,
                            modifier = Modifier.size(92.dp)
                        )
                    }

                    // Floating Heart Badge in Center
                    Box(
                        modifier = Modifier
                            .scale(heartScale)
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(SparkCoral, SparkPeach))
                            )
                            .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Matched Heart",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Action Buttons
                Button(
                    onClick = onSendMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("match_send_message_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SparkCoral
                    )
                ) {
                    Icon(imageVector = Icons.Default.ChatBubble, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Say Hello to ${matchedProfile.name.split(" ").first()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onKeepSwiping,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("match_keep_swiping_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Keep Swiping",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
