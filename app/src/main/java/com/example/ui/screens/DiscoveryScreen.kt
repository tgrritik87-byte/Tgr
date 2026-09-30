package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entities.DatingProfileEntity
import com.example.ui.components.DiscoveryFilterDialog
import com.example.ui.components.InterestChip
import com.example.ui.components.PromptCard
import com.example.ui.components.ReportBlockDialog
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.LikeGreen
import com.example.ui.theme.PassRed
import com.example.ui.theme.SparkCoral
import com.example.ui.theme.SparkGold
import com.example.ui.theme.SparkPeach
import com.example.ui.theme.SuperLikeCyan
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryScreen(
    profiles: List<DatingProfileEntity>,
    onSwipe: (profileId: String, swipeType: String) -> Unit,
    onUndoSwipe: () -> Unit,
    onReportUser: (profile: DatingProfileEntity) -> Unit,
    onBlockUser: (profileId: String) -> Unit,
    onOpenFilters: () -> Unit
) {
    var expandedProfile by remember { mutableStateOf<DatingProfileEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("discovery_screen")
    ) {
        // Discovery Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Spark",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = SparkCoral
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SparkPeach)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenFilters,
                    modifier = Modifier.testTag("open_filters_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filters",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Card Stack Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (profiles.isEmpty()) {
                EmptyDeckView(
                    onResetFilters = onOpenFilters,
                    onUndo = onUndoSwipe
                )
            } else {
                // Show up to 2 cards in the stack for depth
                val visibleProfiles = profiles.take(2).reversed()
                visibleProfiles.forEachIndexed { index, profile ->
                    val isTopCard = index == visibleProfiles.lastIndex
                    DatingCard(
                        profile = profile,
                        isTopCard = isTopCard,
                        onSwipe = { swipeType ->
                            onSwipe(profile.id, swipeType)
                        },
                        onExpand = {
                            expandedProfile = profile
                        }
                    )
                }
            }
        }

        // Action Buttons Bar
        if (profiles.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentTop = profiles.firstOrNull()

                // Undo
                FilledIconButton(
                    onClick = onUndoSwipe,
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("action_undo_button"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = SparkGold.copy(alpha = 0.15f),
                        contentColor = SparkGold
                    )
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Undo")
                }

                // Pass
                FilledIconButton(
                    onClick = {
                        currentTop?.let { onSwipe(it.id, "PASS") }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("action_pass_button"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = PassRed.copy(alpha = 0.15f),
                        contentColor = PassRed
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Pass",
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Super Like
                FilledIconButton(
                    onClick = {
                        currentTop?.let { onSwipe(it.id, "SUPERLIKE") }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("action_superlike_button"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = SuperLikeCyan.copy(alpha = 0.15f),
                        contentColor = SuperLikeCyan
                    )
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Super Like")
                }

                // Like
                FilledIconButton(
                    onClick = {
                        currentTop?.let { onSwipe(it.id, "LIKE") }
                    },
                    modifier = Modifier
                        .size(62.dp)
                        .testTag("action_like_button"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = SparkCoral,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Info / Expand
                FilledIconButton(
                    onClick = {
                        currentTop?.let { expandedProfile = it }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("action_info_button"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Profile Details")
                }
            }
        }
    }

    // Expanded Profile Sheet
    expandedProfile?.let { profile ->
        ModalBottomSheet(
            onDismissRequest = { expandedProfile = null },
            sheetState = sheetState
        ) {
            ProfileDetailContent(
                profile = profile,
                onLike = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        expandedProfile = null
                        onSwipe(profile.id, "LIKE")
                    }
                },
                onPass = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        expandedProfile = null
                        onSwipe(profile.id, "PASS")
                    }
                },
                onReport = {
                    expandedProfile = null
                    onReportUser(profile)
                }
            )
        }
    }
}

@Composable
fun DatingCard(
    profile: DatingProfileEntity,
    isTopCard: Boolean,
    onSwipe: (String) -> Unit,
    onExpand: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    val rotation = (offsetX.value / 25f).coerceIn(-20f, 20f)
    val likeAlpha = (offsetX.value / 250f).coerceIn(0f, 1f)
    val nopeAlpha = (-offsetX.value / 250f).coerceIn(0f, 1f)
    val superLikeAlpha = (-offsetY.value / 250f).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .graphicsLayer {
                rotationZ = rotation
                if (!isTopCard) {
                    scaleX = 0.96f
                    scaleY = 0.96f
                }
            }
            .then(
                if (isTopCard) {
                    Modifier.pointerInput(profile.id) {
                        detectDragGestures(
                            onDragEnd = {
                                coroutineScope.launch {
                                    if (offsetX.value > 300f) {
                                        offsetX.animateTo(1000f, tween(200))
                                        onSwipe("LIKE")
                                    } else if (offsetX.value < -300f) {
                                        offsetX.animateTo(-1000f, tween(200))
                                        onSwipe("PASS")
                                    } else if (offsetY.value < -350f) {
                                        offsetY.animateTo(-1000f, tween(200))
                                        onSwipe("SUPERLIKE")
                                    } else {
                                        offsetX.animateTo(0f, tween(200))
                                        offsetY.animateTo(0f, tween(200))
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                coroutineScope.launch {
                                    offsetX.snapTo(offsetX.value + dragAmount.x)
                                    offsetY.snapTo(offsetY.value + dragAmount.y)
                                }
                            }
                        )
                    }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isTopCard) 6.dp else 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image or Stylized Gradient
            if (profile.avatarDrawable == "img_avatar_elena") {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.img_avatar_elena),
                    contentDescription = profile.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(profile.gradientStartHex),
                                    Color(profile.gradientEndHex),
                                    Color(0xFF0F0B18)
                                )
                            )
                        )
                ) {
                    // Profile Initials / Abstract Portrait Graphic
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .border(3.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString(""),
                                color = Color.White,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Dark Scrim for readable text
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Swipe Stamp Overlays
            if (isTopCard) {
                // LIKE stamp
                if (likeAlpha > 0f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(28.dp)
                            .rotate(-15f)
                            .border(3.dp, LikeGreen.copy(alpha = likeAlpha), RoundedCornerShape(12.dp))
                            .background(LikeGreen.copy(alpha = likeAlpha * 0.25f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "LIKE",
                            color = LikeGreen.copy(alpha = likeAlpha),
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        )
                    }
                }

                // NOPE stamp
                if (nopeAlpha > 0f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(28.dp)
                            .rotate(15f)
                            .border(3.dp, PassRed.copy(alpha = nopeAlpha), RoundedCornerShape(12.dp))
                            .background(PassRed.copy(alpha = nopeAlpha * 0.25f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "NOPE",
                            color = PassRed.copy(alpha = nopeAlpha),
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        )
                    }
                }

                // SUPER LIKE stamp
                if (superLikeAlpha > 0.15f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 40.dp)
                            .border(3.dp, SuperLikeCyan.copy(alpha = superLikeAlpha), RoundedCornerShape(12.dp))
                            .background(SuperLikeCyan.copy(alpha = superLikeAlpha * 0.25f))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "SUPER LIKE",
                            color = SuperLikeCyan.copy(alpha = superLikeAlpha),
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        )
                    }
                }
            }

            // Bottom Profile Info Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${profile.name}, ${profile.age}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (profile.isVerified) {
                        VerifiedBadge(sizeDp = 20, showLabel = true)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${profile.distanceKm} km away • ${profile.locationName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${profile.occupation} at ${profile.companyOrSchool}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Short bio preview
                Text(
                    text = profile.bio,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileDetailContent(
    profile: DatingProfileEntity,
    onLike: () -> Unit,
    onPass: () -> Unit,
    onReport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // Name & Verification Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${profile.name}, ${profile.age}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (profile.isVerified) {
                        Spacer(modifier = Modifier.width(8.dp))
                        VerifiedBadge(sizeDp = 22, showLabel = true)
                    }
                }
                Text(
                    text = "${profile.occupation} • ${profile.companyOrSchool}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onReport) {
                Icon(
                    imageVector = Icons.Default.Report,
                    contentDescription = "Report User",
                    tint = PassRed
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Relationship Intent Chip
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = SparkCoral.copy(alpha = 0.12f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💘 Looking for: ${profile.relationshipIntent}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SparkCoral
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bio Section
        Text(
            text = "About Me",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = profile.bio,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Prompts Section
        Text(
            text = "Conversation Starters",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        val promptPairs = profile.prompts.split("///")
        promptPairs.forEach { rawPrompt ->
            val parts = rawPrompt.split("||")
            if (parts.size >= 2) {
                PromptCard(question = parts[0], answer = parts[1])
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Interests Tags
        Text(
            text = "Interests & Passions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            profile.interests.split(",").forEach { interest ->
                InterestChip(text = interest.trim())
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Facts
        Text(
            text = "Basics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("📏 Height: ${profile.heightCm} cm", style = MaterialTheme.typography.bodySmall)
            Text("✨ Zodiac: ${profile.zodiac}", style = MaterialTheme.typography.bodySmall)
            Text("🐾 Pets: ${profile.pets}", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Bottom Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onPass,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = PassRed)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pass", color = PassRed, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onLike,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SparkCoral)
            ) {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Like", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun EmptyDeckView(
    onResetFilters: () -> Unit,
    onUndo: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(SparkCoral.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = SparkCoral,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "You're All Caught Up!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "There are no more new profiles nearby right now. Expand your discovery distance or reset filters to meet more people.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onResetFilters,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SparkCoral)
            ) {
                Icon(imageVector = Icons.Default.FilterList, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adjust Filters")
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onUndo,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Rewind Last Swipe")
            }
        }
    }
}
