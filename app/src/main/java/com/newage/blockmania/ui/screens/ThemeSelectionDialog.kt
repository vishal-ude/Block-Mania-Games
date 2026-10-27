package com.newage.blockmania.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newage.blockmania.theme.GameTheme
import com.newage.blockmania.ui.components.VectorIcons

@Composable
fun ThemeSelectionDialog(
    currentTheme: GameTheme,
    onThemeSelect: (GameTheme) -> Unit,
    isThemeUnlocked: (GameTheme) -> Boolean = { true },
    getUnlockRequirement: (GameTheme) -> String = { "" },
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primaryButton),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "DONE",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )
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
                        .background(currentTheme.scoreAccent.copy(alpha = 0.20f)),
                    contentAlignment = Alignment.Center
                ) {
                    VectorIcons.ThemeIcon(tint = Color.White, size = 22.dp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "CHOOSE THEME",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Unlock themes through competitive gameplay",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF94A3B8)
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                GameTheme.entries.forEach { theme ->
                    val isSelected = theme == currentTheme
                    val isUnlocked = isThemeUnlocked(theme)
                    val requirement = getUnlockRequirement(theme)

                    ThemeCard(
                        theme = theme,
                        isSelected = isSelected,
                        isUnlocked = isUnlocked,
                        unlockRequirement = requirement,
                        onSelect = {
                            if (isUnlocked) {
                                onThemeSelect(theme)
                            }
                        }
                    )
                }
            }
        },
        shape = RoundedCornerShape(26.dp),
        containerColor = currentTheme.dialogBackground,
        modifier = Modifier.border(1.5.dp, currentTheme.dialogBorder, RoundedCornerShape(26.dp))
    )
}

@Composable
private fun ThemeCard(
    theme: GameTheme,
    isSelected: Boolean,
    isUnlocked: Boolean,
    unlockRequirement: String,
    onSelect: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && isUnlocked) 0.96f else 1.0f,
        animationSpec = tween(100),
        label = "themeCardScale"
    )

    val borderModifier = if (isSelected) {
        Modifier.border(2.5.dp, theme.scoreAccent, RoundedCornerShape(18.dp))
    } else {
        Modifier.border(1.dp, theme.cardBorder.copy(alpha = if (isUnlocked) 0.70f else 0.35f), RoundedCornerShape(18.dp))
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .then(borderModifier)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(theme.cardBackground))
            .alpha(if (isUnlocked) 1.0f else 0.65f)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isUnlocked,
                onClick = onSelect
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = theme.displayName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(theme.scoreAccent)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A)
                            )
                        }
                    } else if (!isUnlocked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.4f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                VectorIcons.LockIcon(tint = Color(0xFF94A3B8), size = 10.dp)
                                Text(
                                    text = "LOCKED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isUnlocked) theme.description else "🔒 $unlockRequirement",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (isUnlocked) Color(0xFFCBD5E1) else Color(0xFFFDE047)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Color Swatches
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    theme.previewColors.forEach { col ->
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(col)
                                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(5.dp))
                        )
                    }
                }
            }

            // Radio / Check or Lock Indicator
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) theme.scoreAccent
                        else if (!isUnlocked) Color.Black.copy(alpha = 0.35f)
                        else Color.Transparent
                    )
                    .border(
                        2.dp,
                        if (isSelected) theme.scoreAccent
                        else if (!isUnlocked) Color(0xFF475569)
                        else Color(0xFF64748B),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    VectorIcons.CheckmarkIcon(tint = Color(0xFF0F172A), size = 15.dp)
                } else if (!isUnlocked) {
                    VectorIcons.LockIcon(tint = Color(0xFF94A3B8), size = 14.dp)
                }
            }
        }
    }
}
