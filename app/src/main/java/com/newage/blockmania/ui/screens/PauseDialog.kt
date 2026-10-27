package com.newage.blockmania.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newage.blockmania.theme.GameTheme

@Composable
fun PauseDialog(
    score: Int,
    bestScore: Int,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit,
    theme: GameTheme = GameTheme.GALAXY
) {
    AlertDialog(
        onDismissRequest = onResume,
        confirmButton = {},
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(theme.scoreAccent.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    com.newage.blockmania.ui.components.VectorIcons.PauseIcon(tint = Color.White, size = 20.dp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "GAME PAUSED",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Score Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = theme.slotBackground),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, theme.slotBorder, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 8.dp)
                        ) {
                            Text(
                                "CURRENT",
                                fontSize = 11.sp,
                                color = theme.scoreAccent,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(String.format("%,d", score), fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = theme.slotBackground),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, theme.slotBorder, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 8.dp)
                        ) {
                            Text(
                                "BEST",
                                fontSize = 11.sp,
                                color = theme.bestScoreAccent,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(String.format("%,d", bestScore), fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primaryButton),
                    shape = RoundedCornerShape(18.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        com.newage.blockmania.ui.components.VectorIcons.PlayIcon(tint = Color.White, size = 16.dp)
                        Text("RESUME", fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 1.sp)
                    }
                }

                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.previewColors.getOrElse(1) { Color(0xFF3B82F6) }),
                    shape = RoundedCornerShape(18.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        com.newage.blockmania.ui.components.VectorIcons.RestartIcon(tint = Color.White, size = 16.dp)
                        Text("RESTART", fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color.White)
                    }
                }

                Button(
                    onClick = onHome,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.secondaryButton),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(1.dp, theme.secondaryButtonBorder, RoundedCornerShape(18.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        com.newage.blockmania.ui.components.VectorIcons.HomeIcon(tint = Color(0xFFE2E8F0), size = 16.dp)
                        Text("MAIN MENU", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFE2E8F0))
                    }
                }
            }
        },
        shape = RoundedCornerShape(26.dp),
        containerColor = theme.dialogBackground,
        modifier = Modifier.border(1.5.dp, theme.dialogBorder, RoundedCornerShape(26.dp))
    )
}
