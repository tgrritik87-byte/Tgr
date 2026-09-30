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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ActiveGameType
import com.example.ui.state.DatingGamesData
import com.example.ui.theme.LikeGreen
import com.example.ui.theme.SparkCoral
import com.example.ui.theme.SparkGold
import com.example.ui.theme.SparkPeach
import com.example.ui.theme.SparkViolet
import com.example.ui.theme.SuperLikeCyan

@Composable
fun GamesHubScreen(
    activeGame: ActiveGameType,
    onSelectGame: (ActiveGameType) -> Unit,
    // WYR
    wyrIndex: Int,
    wyrChoice: String?,
    onAnswerWyr: (String) -> Unit,
    onNextWyr: () -> Unit,
    // Truth or Dare
    todIndex: Int,
    onNextTod: () -> Unit,
    // Compatibility Quiz
    quizIndex: Int,
    quizAnswers: Map<Int, Int>,
    quizCompleted: Boolean,
    onAnswerQuiz: (questionId: Int, optionIndex: Int) -> Unit,
    onResetQuiz: () -> Unit,
    onShareToChat: (message: String) -> Unit
) {
    when (activeGame) {
        ActiveGameType.NONE -> {
            GamesHubMenu(onSelectGame = onSelectGame)
        }
        ActiveGameType.WOULD_YOU_RATHER -> {
            WouldYouRatherGameView(
                cardIndex = wyrIndex,
                selectedChoice = wyrChoice,
                onSelectChoice = onAnswerWyr,
                onNext = onNextWyr,
                onBack = { onSelectGame(ActiveGameType.NONE) },
                onShare = {
                    val card = DatingGamesData.wouldYouRatherList[wyrIndex]
                    onShareToChat("Dating Game Dilemma: '${card.optionA}' OR '${card.optionB}'? I chose: $wyrChoice!")
                }
            )
        }
        ActiveGameType.TRUTH_OR_DARE -> {
            TruthOrDareGameView(
                itemIndex = todIndex,
                onNext = onNextTod,
                onBack = { onSelectGame(ActiveGameType.NONE) },
                onShare = {
                    val item = DatingGamesData.truthOrDareList[todIndex]
                    onShareToChat("Spark Truth or Dare (${item.type}): \"${item.prompt}\"")
                }
            )
        }
        ActiveGameType.COMPATIBILITY_QUIZ -> {
            CompatibilityQuizView(
                questionIndex = quizIndex,
                answers = quizAnswers,
                isCompleted = quizCompleted,
                onSelectAnswer = onAnswerQuiz,
                onReset = onResetQuiz,
                onBack = { onSelectGame(ActiveGameType.NONE) },
                onShare = {
                    onShareToChat("I took the Spark Dating Compatibility Quiz and got: 'The Spontaneous Harmonizer' (95% Vibe Match)!")
                }
            )
        }
    }
}

@Composable
fun GamesHubMenu(onSelectGame: (ActiveGameType) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("games_hub_menu")
    ) {
        Text(
            text = "Icebreaker Games Hub",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Play interactive dating games to break the ice and check chemistry.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Would You Rather Card
        GameLauncherCard(
            title = "Would You Rather? Dating Edition",
            subtitle = "Spicy, funny, and revealing dilemmas. Compare answers with your matches!",
            icon = "⚖️",
            gradient = listOf(SparkCoral, SparkPeach),
            testTag = "game_launch_wyr",
            onClick = { onSelectGame(ActiveGameType.WOULD_YOU_RATHER) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Truth or Dare Card
        GameLauncherCard(
            title = "Truth or Dare: Spark Edition",
            subtitle = "Fun flirty questions and charming voice dares to spark real laughter.",
            icon = "🎲",
            gradient = listOf(SparkViolet, Color(0xFFEC4899)),
            testTag = "game_launch_tod",
            onClick = { onSelectGame(ActiveGameType.TRUTH_OR_DARE) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Compatibility Quiz Card
        GameLauncherCard(
            title = "Chemistry & Compatibility Quiz",
            subtitle = "5 quick questions to calculate your dating archetype and compatibility score.",
            icon = "🔮",
            gradient = listOf(Color(0xFF06B6D4), Color(0xFF3B82F6)),
            testTag = "game_launch_quiz",
            onClick = { onSelectGame(ActiveGameType.COMPATIBILITY_QUIZ) }
        )
    }
}

@Composable
fun GameLauncherCard(
    title: String,
    subtitle: String,
    icon: String,
    gradient: List<Color>,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(gradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = gradient.first()),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("Play Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WouldYouRatherGameView(
    cardIndex: Int,
    selectedChoice: String?,
    onSelectChoice: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onShare: () -> Unit
) {
    BackHandler { onBack() }
    val card = DatingGamesData.wouldYouRatherList[cardIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("wyr_game_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Would You Rather? (${cardIndex + 1}/${DatingGamesData.wouldYouRatherList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onShare) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = SparkCoral)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Option A Card
        val isA = selectedChoice == "A"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectChoice("A") }
                .testTag("wyr_option_a"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isA) SparkCoral.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
            ),
            border = if (isA) androidx.compose.foundation.BorderStroke(2.dp, SparkCoral) else null
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "OPTION A",
                    style = MaterialTheme.typography.labelSmall,
                    color = SparkCoral,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = card.optionA,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (selectedChoice != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${card.votesA}% of singles chose this",
                        style = MaterialTheme.typography.labelMedium,
                        color = SparkCoral,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "— OR —",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Option B Card
        val isB = selectedChoice == "B"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectChoice("B") }
                .testTag("wyr_option_b"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isB) SparkViolet.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
            ),
            border = if (isB) androidx.compose.foundation.BorderStroke(2.dp, SparkViolet) else null
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "OPTION B",
                    style = MaterialTheme.typography.labelSmall,
                    color = SparkViolet,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = card.optionB,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (selectedChoice != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${card.votesB}% of singles chose this",
                        style = MaterialTheme.typography.labelMedium,
                        color = SparkViolet,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onShare,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share in Chat")
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .testTag("wyr_next_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SparkCoral)
            ) {
                Text("Next Dilemma")
            }
        }
    }
}

@Composable
fun TruthOrDareGameView(
    itemIndex: Int,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onShare: () -> Unit
) {
    BackHandler { onBack() }
    val item = DatingGamesData.truthOrDareList[itemIndex]
    val isTruth = item.type == "TRUTH"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("tod_game_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Spark Truth or Dare",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onShare) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = SparkCoral)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Card Display
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
                2.dp,
                if (isTruth) SparkCoral else SparkViolet
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isTruth) SparkCoral.copy(alpha = 0.15f) else SparkViolet.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${item.type} • ${item.level.uppercase()}",
                        color = if (isTruth) SparkCoral else SparkViolet,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "\"${item.prompt}\"",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onShare,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Send to Chat")
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .testTag("tod_next_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isTruth) SparkCoral else SparkViolet)
            ) {
                Icon(imageVector = Icons.Default.Casino, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Shuffle Card")
            }
        }
    }
}

@Composable
fun CompatibilityQuizView(
    questionIndex: Int,
    answers: Map<Int, Int>,
    isCompleted: Boolean,
    onSelectAnswer: (questionId: Int, optionIndex: Int) -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
    onShare: () -> Unit
) {
    BackHandler { onBack() }

    if (isCompleted) {
        // Quiz Results Archetype View
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .testTag("quiz_result_screen"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Compatibility Result",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onShare) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = SparkCoral)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(SparkCoral, SparkViolet))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "The Spontaneous Harmonizer",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = SparkCoral
            )

            Text(
                text = "95% Dating Chemistry Index",
                style = MaterialTheme.typography.titleMedium,
                color = LikeGreen,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Vibe Analysis:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = SparkCoral
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "You thrive on genuine presence, spontaneous road trips, and open communication. You pair best with partners who value quality time, artistic curiosity, and deep midnight conversations.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onShare,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("quiz_share_badge_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SparkCoral)
            ) {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Result to Match Chat", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retake Quiz")
            }
        }
    } else {
        val currentQuestion = DatingGamesData.compatibilityQuestions[questionIndex]
        val total = DatingGamesData.compatibilityQuestions.size

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .testTag("quiz_questions_screen")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Question ${questionIndex + 1} of $total",
                    style = MaterialTheme.typography.labelLarge,
                    color = SparkCoral,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { (questionIndex + 1) / total.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = SparkCoral,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = currentQuestion.question,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                currentQuestion.options.forEachIndexed { optIndex, optionText ->
                    val isSelected = answers[currentQuestion.id] == optIndex
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectAnswer(currentQuestion.id, optIndex) }
                            .testTag("quiz_option_${optIndex}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) SparkCoral.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, SparkCoral) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) SparkCoral else Color.Transparent)
                                    .border(2.dp, if (isSelected) SparkCoral else MaterialTheme.colorScheme.outline, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
