package com.newage.blockmania.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newage.blockmania.model.Achievement
import com.newage.blockmania.theme.GameTheme
import com.newage.blockmania.ui.components.VectorIcons

@Composable
fun GameOverDialog(
    score: Int,
    bestScore: Int,
    linesCleared: Int = 0,
    maxCombo: Int = 0,
    isNewHighScore: Boolean = false,
    newlyUnlockedAchievements: List<Achievement> = emptyList(),
    canRevive: Boolean = false,
    onRevive: () -> Unit = {},
    onRestart: () -> Unit,
    onHome: () -> Unit,
    theme: GameTheme = GameTheme.GALAXY
) {
    val infiniteTransition = rememberInfiniteTransition(label = "highScorePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "gameOverScoreRoll"
    )

    AlertDialog(
        onDismissRequest = { /* Modal: require button click */ },
        confirmButton = {},
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(theme.scoreAccent.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    VectorIcons.TrophyIcon(tint = theme.scoreAccent, size = 26.dp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "NO MORE MOVES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = theme.bestScoreAccent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "GAME OVER",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isNewHighScore) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(pulseScale)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(theme.bestScoreAccent, theme.scoreAccent)
                                )
                            )
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            VectorIcons.StarIcon(tint = Color(0xFF0F172A), size = 16.dp)
                            Text(
                                text = "NEW BEST SCORE!",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A)
                            )
                            VectorIcons.StarIcon(tint = Color(0xFF0F172A), size = 16.dp)
                        }
                    }
                }

                // Dual Scores Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = theme.slotBackground),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, theme.slotBorder, RoundedCornerShape(14.dp))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 6.dp)
                        ) {
                            Text(
                                "FINAL SCORE",
                                fontSize = 10.sp,
                                color = theme.scoreAccent,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                String.format("%,d", animatedScore),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = theme.slotBackground),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, theme.slotBorder, RoundedCornerShape(14.dp))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 6.dp)
                        ) {
                            Text(
                                "BEST",
                                fontSize = 10.sp,
                                color = theme.bestScoreAccent,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                String.format("%,d", bestScore),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }

                // Match Statistics Summary (Lines Cleared & Max Combo)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(theme.slotBackground.copy(alpha = 0.7f))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lines Cleared: $linesCleared",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(theme.slotBackground.copy(alpha = 0.7f))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Max Combo: ${maxCombo}x",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.bestScoreAccent
                        )
                    }
                }

                // Unlocked Achievements Banner
                if (newlyUnlockedAchievements.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = theme.bestScoreAccent.copy(alpha = 0.15f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, theme.bestScoreAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VectorIcons.MedalIcon(tint = theme.bestScoreAccent, size = 20.dp)
                            Column {
                                Text(
                                    text = "ACHIEVEMENT UNLOCKED!",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = theme.bestScoreAccent
                                )
                                Text(
                                    text = newlyUnlockedAchievements.first().title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Optional Rewarded Continue Feature (Subject to compliance & availability)
                if (canRevive) {
                    Button(
                        onClick = onRevive,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VectorIcons.AdRewardIcon(tint = Color(0xFF0F172A), size = 20.dp)
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    "CONTINUE (WATCH AD)",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color(0xFF0F172A),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    "Clear board center & get 3 fresh pieces",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF0F172A).copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }

                // Primary Play Again Button
                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primaryButton),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VectorIcons.RestartIcon(tint = Color.White, size = 18.dp)
                        Text(
                            "PLAY AGAIN",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Return to Main Menu
                Button(
                    onClick = onHome,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.secondaryButton),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .border(1.dp, theme.secondaryButtonBorder, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VectorIcons.HomeIcon(tint = Color(0xFFE2E8F0), size = 16.dp)
                        Text(
                            "MAIN MENU",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFE2E8F0)
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
