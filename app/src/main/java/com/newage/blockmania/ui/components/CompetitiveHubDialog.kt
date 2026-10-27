package com.newage.blockmania.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newage.blockmania.model.Achievement
import com.newage.blockmania.model.DailyChallenge
import com.newage.blockmania.model.Mission
import com.newage.blockmania.model.PlayerStats
import com.newage.blockmania.theme.GameTheme

enum class CompetitiveTab(val title: String) {
    DAILY("DAILY"),
    MISSIONS("MISSIONS"),
    ACHIEVEMENTS("AWARDS"),
    STATS("STATS")
}

@Composable
fun CompetitiveHubDialog(
    dailyChallenge: DailyChallenge,
    missions: List<Mission>,
    achievements: List<Achievement>,
    playerStats: PlayerStats,
    theme: GameTheme,
    onDismiss: () -> Unit,
    onPlayDaily: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = CompetitiveTab.entries

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = theme.primaryButton),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("CLOSE", fontWeight = FontWeight.Black, fontSize = 15.sp, letterSpacing = 1.sp)
            }
        },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(theme.bestScoreAccent.copy(alpha = 0.20f)),
                    contentAlignment = Alignment.Center
                ) {
                    VectorIcons.TrophyIcon(tint = theme.bestScoreAccent, size = 24.dp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "COMPETITIVE HUB",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Challenges, Missions & Career Records",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Selector Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(theme.slotBackground)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(11.dp))
                                .background(if (isSelected) theme.scoreAccent else Color.Transparent)
                                .clickable { selectedTab = index }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) Color(0xFF0F172A) else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(150))
                    },
                    label = "hubTabTransition"
                ) { tabIndex ->
                    when (tabs[tabIndex]) {
                        CompetitiveTab.DAILY -> DailyChallengeTabContent(
                            challenge = dailyChallenge,
                            theme = theme,
                            onPlayDaily = onPlayDaily
                        )
                        CompetitiveTab.MISSIONS -> MissionsTabContent(
                            missions = missions,
                            theme = theme
                        )
                        CompetitiveTab.ACHIEVEMENTS -> AchievementsTabContent(
                            achievements = achievements,
                            theme = theme
                        )
                        CompetitiveTab.STATS -> PlayerStatsTabContent(
                            stats = playerStats,
                            theme = theme
                        )
                    }
                }
            }
        },
        shape = RoundedCornerShape(26.dp),
        containerColor = theme.dialogBackground,
        modifier = Modifier.border(1.5.dp, theme.dialogBorder, RoundedCornerShape(26.dp))
    )
}

@Composable
private fun DailyChallengeTabContent(
    challenge: DailyChallenge,
    theme: GameTheme,
    onPlayDaily: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = theme.slotBackground),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, theme.slotBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        VectorIcons.CalendarIcon(tint = theme.scoreAccent, size = 16.dp)
                        Text(
                            text = challenge.dateString,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.scoreAccent
                        )
                    }
                    if (challenge.isCompleted) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("COMPLETED", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = challenge.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = challenge.description,
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Score Target Progress
                Text(
                    text = "Score: ${challenge.currentScore} / ${challenge.targetScore} pts",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE2E8F0)
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { challenge.scoreProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = theme.scoreAccent,
                    trackColor = Color.Black.copy(alpha = 0.35f),
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Lines Target Progress
                Text(
                    text = "Lines Cleared: ${challenge.currentLines} / ${challenge.targetLines}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE2E8F0)
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { challenge.linesProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = theme.bestScoreAccent,
                    trackColor = Color.Black.copy(alpha = 0.35f),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    VectorIcons.MedalIcon(tint = Color(0xFFFFD700), size = 16.dp)
                    Text(
                        text = "Reward: ${challenge.rewardDescription}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }
            }
        }

        if (!challenge.isCompleted) {
            Button(
                onClick = onPlayDaily,
                colors = ButtonDefaults.buttonColors(containerColor = theme.scoreAccent),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    VectorIcons.PlayIcon(tint = Color(0xFF0F172A), size = 14.dp)
                    Text("PLAY TODAY'S CHALLENGE", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color(0xFF0F172A))
                }
            }
        }
    }
}

@Composable
private fun MissionsTabContent(
    missions: List<Mission>,
    theme: GameTheme
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        missions.forEach { mission ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = theme.slotBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, theme.slotBorder, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mission.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        if (mission.isCompleted) {
                            VectorIcons.CheckmarkIcon(tint = Color(0xFF10B981), size = 16.dp)
                        } else {
                            Text(
                                text = "${mission.currentProgress} / ${mission.targetProgress}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.scoreAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = mission.description,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { mission.progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (mission.isCompleted) Color(0xFF10B981) else theme.scoreAccent,
                        trackColor = Color.Black.copy(alpha = 0.35f),
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Reward: ${mission.rewardDescription}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFDE047)
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementsTabContent(
    achievements: List<Achievement>,
    theme: GameTheme
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        achievements.forEach { ach ->
            val isUnlocked = ach.isUnlocked
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) theme.slotBackground else theme.slotBackground.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isUnlocked) theme.bestScoreAccent.copy(alpha = 0.4f) else theme.slotBorder,
                        RoundedCornerShape(14.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isUnlocked) theme.bestScoreAccent.copy(alpha = 0.22f)
                                else Color.White.copy(alpha = 0.08f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlocked) {
                            when (ach.iconType) {
                                "trophy" -> VectorIcons.TrophyIcon(tint = theme.bestScoreAccent, size = 18.dp)
                                "medal" -> VectorIcons.MedalIcon(tint = theme.bestScoreAccent, size = 18.dp)
                                "calendar" -> VectorIcons.CalendarIcon(tint = theme.bestScoreAccent, size = 18.dp)
                                else -> VectorIcons.StarIcon(tint = theme.bestScoreAccent, size = 18.dp)
                            }
                        } else {
                            VectorIcons.LockIcon(tint = Color(0xFF64748B), size = 16.dp)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ach.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) Color.White else Color(0xFF94A3B8)
                        )
                        Text(
                            text = ach.description,
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                        if (!isUnlocked) {
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { ach.progressPercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = theme.scoreAccent,
                                trackColor = Color.Black.copy(alpha = 0.35f),
                            )
                        }
                    }

                    if (isUnlocked) {
                        VectorIcons.CheckmarkIcon(tint = Color(0xFF10B981), size = 16.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerStatsTabContent(
    stats: PlayerStats,
    theme: GameTheme
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatItemCard(label = "GAMES PLAYED", value = stats.gamesPlayed.toString(), theme = theme)
        StatItemCard(label = "TOTAL LINES CLEARED", value = stats.totalLinesCleared.toString(), theme = theme)
        StatItemCard(label = "HIGHEST COMBO STREAK", value = "${stats.highestCombo}x", theme = theme)
        StatItemCard(label = "TOTAL CAREER SCORE", value = String.format("%,d", stats.totalScore), theme = theme)
        StatItemCard(label = "DAILY CHALLENGES WON", value = stats.dailyChallengesCompleted.toString(), theme = theme)
    }
}

@Composable
private fun StatItemCard(
    label: String,
    value: String,
    theme: GameTheme
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = theme.slotBackground),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, theme.slotBorder, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.5.sp
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = theme.scoreAccent
            )
        }
    }
}
