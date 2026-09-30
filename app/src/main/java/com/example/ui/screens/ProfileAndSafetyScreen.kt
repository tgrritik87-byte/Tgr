package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.UserProfileEntity
import com.example.ui.components.InterestChip
import com.example.ui.components.SparkAvatar
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.LikeGreen
import com.example.ui.theme.PassRed
import com.example.ui.theme.SparkCoral
import com.example.ui.theme.SparkGold
import com.example.ui.theme.SparkPeach
import com.example.ui.theme.VerifiedBlue

@Composable
fun ProfileAndSafetyScreen(
    userProfile: UserProfileEntity?,
    authUserState: com.example.data.auth.AuthUserState?,
    syncState: com.example.data.sync.SyncState?,
    onUpdateProfile: (name: String, bio: String, occupation: String, location: String) -> Unit,
    onCompleteVerification: (poseName: String) -> Unit,
    onToggleIncognito: () -> Unit,
    onOpenAuthDialog: () -> Unit,
    onSendVerificationEmail: () -> Unit,
    onSignOut: () -> Unit,
    onTriggerSync: () -> Unit
) {
    val profile = userProfile ?: return
    var showEditDialog by remember { mutableStateOf(false) }
    var showVerificationFlow by remember { mutableStateOf(false) }
    var showEmergencyModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("profile_safety_screen")
    ) {
        Text(
            text = "Profile & Safety",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Manage your dating profile, verification badge, and safety settings.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Profile Overview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("user_profile_card"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SparkAvatar(
                            name = profile.name,
                            avatarDrawable = null,
                            gradientStartHex = 0xFFFF5E62,
                            gradientEndHex = 0xFFFF9966,
                            isVerified = profile.isVerified,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${profile.name}, ${profile.age}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                if (profile.isVerified) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    VerifiedBadge(sizeDp = 18)
                                }
                            }
                            Text(
                                text = profile.occupation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = profile.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile", tint = SparkCoral)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = profile.bio,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Firebase Authentication & User Verification Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("firebase_auth_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = SparkCoral,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Firebase Auth & Verification",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (authUserState?.isAuthenticated == true) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = LikeGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Active",
                                color = LikeGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val emailStr = authUserState?.email ?: "jordan.rivera@sparkdating.app"
                Text(
                    text = "Signed in as: $emailStr",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isEmailVerified = authUserState?.isEmailVerified == true
                    Icon(
                        imageVector = if (isEmailVerified) Icons.Default.CheckCircle else Icons.Default.Verified,
                        contentDescription = null,
                        tint = if (isEmailVerified) LikeGreen else SparkGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEmailVerified) "Email Verified via Firebase" else "Email Verification Pending",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isEmailVerified) LikeGreen else SparkGold,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (authUserState?.isEmailVerified != true) {
                        Button(
                            onClick = onSendVerificationEmail,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SparkCoral),
                            modifier = Modifier.weight(1f).testTag("send_verification_email_button")
                        ) {
                            Text("Send Verify Email", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = onOpenAuthDialog,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("switch_account_button")
                    ) {
                        Text(if (authUserState?.isAuthenticated == true) "Switch Account" else "Sign In", fontSize = 12.sp)
                    }

                    if (authUserState?.isAuthenticated == true) {
                        OutlinedButton(
                            onClick = onSignOut,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("sign_out_button")
                        ) {
                            Text("Sign Out", fontSize = 12.sp, color = PassRed)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Real-Time Room-to-Firestore Sync Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("firestore_sync_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = Color(0xFF00C9FF),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Cloud Firestore Sync",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = when (syncState?.status) {
                            com.example.data.sync.SyncStatus.SYNCED -> LikeGreen.copy(alpha = 0.15f)
                            com.example.data.sync.SyncStatus.SYNCING -> SparkGold.copy(alpha = 0.15f)
                            com.example.data.sync.SyncStatus.ERROR -> PassRed.copy(alpha = 0.15f)
                            else -> Color(0xFF00C9FF).copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = syncState?.status?.name ?: "ACTIVE",
                            color = when (syncState?.status) {
                                com.example.data.sync.SyncStatus.SYNCED -> LikeGreen
                                com.example.data.sync.SyncStatus.SYNCING -> SparkGold
                                com.example.data.sync.SyncStatus.ERROR -> PassRed
                                else -> Color(0xFF00C9FF)
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = syncState?.message ?: "Room-to-Firestore synchronization active.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profiles: ${syncState?.syncedProfilesCount ?: 6} • Matches: ${syncState?.syncedMatchesCount ?: 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = onTriggerSync,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C9FF).copy(alpha = 0.2f), contentColor = Color(0xFF0091FF)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("trigger_sync_button")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sync Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Photo Verification Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("verification_status_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (profile.isVerified) VerifiedBlue.copy(alpha = 0.1f)
                else SparkGold.copy(alpha = 0.12f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (profile.isVerified) VerifiedBlue.copy(alpha = 0.35f) else SparkGold.copy(alpha = 0.4f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (profile.isVerified) Icons.Default.Verified else Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = if (profile.isVerified) VerifiedBlue else SparkGold,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (profile.isVerified) "Profile Verified ✓" else "Get Verified",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (profile.isVerified) VerifiedBlue else SparkGold
                        )
                        Text(
                            text = if (profile.isVerified) "Verified via ${profile.verificationPose}"
                            else "Take a quick selfie pose to unlock the verified badge and boost match rate.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { showVerificationFlow = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (profile.isVerified) VerifiedBlue else SparkGold
                    ),
                    modifier = Modifier.testTag("verify_now_button")
                ) {
                    Text(
                        text = if (profile.isVerified) "Re-verify" else "Verify",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Privacy: Incognito Mode
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = SparkPeach,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Incognito Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Only show profile to people you've swiped right on",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = profile.incognitoMode,
                    onCheckedChange = { onToggleIncognito() },
                    colors = SwitchDefaults.colors(checkedThumbColor = SparkCoral, checkedTrackColor = SparkCoral.copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("incognito_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dating Safety Checklist
        Text(
            text = "Dating Safety Center",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Essential practices for safe and confident offline meetups.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SafetyCheckItem(
                    title = "Meet in Public Spaces",
                    desc = "Always hold first dates in populated, well-lit venues like cafes or public parks."
                )
                SafetyCheckItem(
                    title = "Inform a Trusted Friend",
                    desc = "Share your date details, location, and planned return time with a friend or roommate."
                )
                SafetyCheckItem(
                    title = "Arrange Your Own Transportation",
                    desc = "Drive your own car, use transit, or book rideshare so you can leave at any time."
                )
                SafetyCheckItem(
                    title = "Guard Personal Identifiers",
                    desc = "Keep financial data, home address, and work schedules private until deep trust is formed."
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Emergency Assistance Button
        Button(
            onClick = { showEmergencyModal = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("emergency_help_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PassRed.copy(alpha = 0.15f), contentColor = PassRed)
        ) {
            Icon(imageVector = Icons.Default.Emergency, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Emergency Safety Resources", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Edit Profile Modal
    if (showEditDialog) {
        var editName by remember { mutableStateOf(profile.name) }
        var editBio by remember { mutableStateOf(profile.bio) }
        var editOccupation by remember { mutableStateOf(profile.occupation) }
        var editLocation by remember { mutableStateOf(profile.location) }

        Dialog(onDismissRequest = { showEditDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("edit_profile_dialog"),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Edit Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editOccupation,
                        onValueChange = { editOccupation = it },
                        label = { Text("Occupation / Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editLocation,
                        onValueChange = { editLocation = it },
                        label = { Text("City / Neighborhood") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("About Me Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(onClick = { showEditDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                onUpdateProfile(editName, editBio, editOccupation, editLocation)
                                showEditDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SparkCoral)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }

    // Photo Selfie Verification Modal
    if (showVerificationFlow) {
        val verificationPoses = listOf("✌️ Two-Finger Peace Sign", "🙃 Gentle Head Tilt", "👍 Thumbs Up & Smile")
        var selectedPose by remember { mutableStateOf(verificationPoses[0]) }
        var isVerifying by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showVerificationFlow = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("verification_flow_dialog"),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(VerifiedBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = VerifiedBlue,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Instant Selfie Verification",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Spark uses real-time pose matching to verify profile authenticity and eliminate bots.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Camera Viewfinder Simulation
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .border(3.dp, VerifiedBlue, CircleShape)
                            .background(Color(0xFF1E1528)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = selectedPose.split(" ").first(), fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Align Face",
                                style = MaterialTheme.typography.labelSmall,
                                color = VerifiedBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Selected Pose: $selectedPose",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            isVerifying = true
                            onCompleteVerification(selectedPose)
                            showVerificationFlow = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_verification_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerifiedBlue)
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Capture & Verify Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Emergency Resources Modal
    if (showEmergencyModal) {
        Dialog(onDismissRequest = { showEmergencyModal = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocalPolice, contentDescription = null, tint = PassRed)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Safety & Emergency Hotlines",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "• Immediate Emergency: Call 911 or your local emergency dispatch.\n" +
                                "• National Domestic Violence Hotline: 1-800-799-SAFE (7233)\n" +
                                "• Crisis Text Line: Text HOME to 741741\n" +
                                "• RAINN 24/7 Sexual Assault Hotline: 1-800-656-4673",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { showEmergencyModal = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SparkCoral)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
fun SafetyCheckItem(title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(LikeGreen.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = LikeGreen,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
