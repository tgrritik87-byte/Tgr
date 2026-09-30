package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.repository.DiscoveryFilterState
import com.example.ui.theme.SparkCoral
import kotlin.math.roundToInt

@Composable
fun DiscoveryFilterDialog(
    initialFilters: DiscoveryFilterState,
    onDismiss: () -> Unit,
    onApply: (maxDistance: Int, minAge: Int, maxAge: Int, verifiedOnly: Boolean) -> Unit
) {
    var distance by remember { mutableFloatStateOf(initialFilters.maxDistanceKm.toFloat()) }
    var ageRange by remember {
        mutableStateOf(initialFilters.minAge.toFloat()..initialFilters.maxAge.toFloat())
    }
    var verifiedOnly by remember { mutableStateOf(initialFilters.verifiedOnly) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .testTag("discovery_filters_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Discovery Filters",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Distance Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Maximum Distance",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${distance.roundToInt()} km",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SparkCoral,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = distance,
                    onValueChange = { distance = it },
                    valueRange = 2f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = SparkCoral,
                        activeTrackColor = SparkCoral
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Age Range Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Age Preference",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${ageRange.start.roundToInt()} - ${ageRange.endInclusive.roundToInt()} yrs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SparkCoral,
                        fontWeight = FontWeight.Bold
                    )
                }

                RangeSlider(
                    value = ageRange,
                    onValueChange = { ageRange = it },
                    valueRange = 18f..60f,
                    colors = SliderDefaults.colors(
                        thumbColor = SparkCoral,
                        activeTrackColor = SparkCoral
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Verified Only Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Verified Profiles Only",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Only show people with photo selfie verification badge",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = verifiedOnly,
                        onCheckedChange = { verifiedOnly = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SparkCoral,
                            checkedTrackColor = SparkCoral.copy(alpha = 0.4f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onApply(
                                distance.roundToInt(),
                                ageRange.start.roundToInt(),
                                ageRange.endInclusive.roundToInt(),
                                verifiedOnly
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("apply_filters_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SparkCoral)
                    ) {
                        Text("Apply", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
