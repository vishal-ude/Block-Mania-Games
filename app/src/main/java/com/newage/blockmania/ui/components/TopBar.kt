package com.newage.blockmania.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newage.blockmania.theme.GameTheme

@Composable
fun TopBar(
    score: Int,
    bestScore: Int,
    isSoundEnabled: Boolean,
    canUndo: Boolean,
    theme: GameTheme,
    onSoundToggle: () -> Unit,
    onUndoClick: () -> Unit,
    onThemeClick: () -> Unit,
    onRestartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onCompetitiveHubClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Smooth counter animation for score changes
    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "scoreRoll"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .padding(top = 2.dp, bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: Brand Title with Mini-Logo + Control Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MiniBlockLogo(
                    previewColors = theme.previewColors,
                    modifier = Modifier.size(26.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "BLOCK",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.scoreAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "MANIA",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.previewColors.getOrElse(0) { Color.White },
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // Tactile Control Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sound Toggle
                TactileIconButton(
                    onClick = onSoundToggle,
                    theme = theme
                ) {
                    VectorIcons.SoundIcon(
                        isSoundEnabled = isSoundEnabled,
                        tint = Color.White
                    )
                }
                // Undo Button
                TactileIconButton(
                    onClick = onUndoClick,
                    enabled = canUndo,
                    theme = theme
                ) {
                    VectorIcons.UndoIcon(
                        tint = if (canUndo) Color.White else Color.White.copy(alpha = 0.4f)
                    )
                }
                // Theme Selector
                TactileIconButton(
                    onClick = onThemeClick,
                    theme = theme
                ) {
                    VectorIcons.ThemeIcon(tint = Color.White)
                }
                // Restart Button
                TactileIconButton(
                    onClick = onRestartClick,
                    theme = theme
                ) {
                    VectorIcons.RestartIcon(tint = Color.White)
                }
                // Pause Button
                TactileIconButton(
                    onClick = onPauseClick,
                    theme = theme
                ) {
                    VectorIcons.PauseIcon(tint = Color.White)
                }
            }
        }

        // Row 2: Score Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Score Card
            PolishedScoreCard(
                label = "SCORE",
                value = animatedScore,
                accentColor = theme.scoreAccent,
                iconSlot = {
                    VectorIcons.StarIcon(tint = theme.scoreAccent)
                },
                bgGradient = theme.cardBackground,
                borderColor = theme.cardBorder,
                modifier = Modifier.weight(1f)
            )

            // Best Score Card (Tap to view Competitive Hub)
            PolishedScoreCard(
                label = "BEST",
                value = bestScore,
                accentColor = theme.bestScoreAccent,
                iconSlot = {
                    VectorIcons.TrophyIcon(tint = theme.bestScoreAccent)
                },
                bgGradient = theme.cardBackground,
                borderColor = theme.cardBorder,
                onClick = onCompetitiveHubClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MiniBlockLogo(
    previewColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val total = size.width
        val pad = total * 0.08f
        val cellSize = (total - pad) / 2f
        val corner = CornerRadius(cellSize * 0.28f, cellSize * 0.28f)

        val colors = listOf(
            Pair(Offset(0f, 0f), previewColors.getOrElse(0) { Color(0xFFFF4B6E) }),
            Pair(Offset(cellSize + pad, 0f), previewColors.getOrElse(1) { Color(0xFFFFC700) }),
            Pair(Offset(0f, cellSize + pad), previewColors.getOrElse(2) { Color(0xFF00D2D3) }),
            Pair(Offset(cellSize + pad, cellSize + pad), previewColors.getOrElse(3) { Color(0xFFA55EEA) })
        )

        colors.forEach { (offset, color) ->
            drawRoundRect(
                color = color,
                topLeft = offset,
                size = Size(cellSize, cellSize),
                cornerRadius = corner
            )
            // Tiny top shine highlight
            drawRoundRect(
                color = Color.White.copy(alpha = 0.40f),
                topLeft = Offset(offset.x + cellSize * 0.15f, offset.y + cellSize * 0.12f),
                size = Size(cellSize * 0.70f, cellSize * 0.28f),
                cornerRadius = CornerRadius(cellSize * 0.20f, cellSize * 0.20f)
            )
        }
    }
}

@Composable
private fun PolishedScoreCard(
    label: String,
    value: Int,
    accentColor: Color,
    iconSlot: @Composable () -> Unit,
    bgGradient: List<Color>,
    borderColor: Color,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(clickModifier)
            .background(Brush.verticalGradient(bgGradient))
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                iconSlot()
            }

            Column {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor,
                    letterSpacing = 1.sp
                )
                Text(
                    text = String.format("%,d", value),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TactileIconButton(
    onClick: () -> Unit,
    theme: GameTheme,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    iconContent: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.88f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "buttonScale"
    )

    val alpha = if (enabled) 1.0f else 0.38f

    Box(
        modifier = modifier
            .scale(scale)
            .size(36.dp)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        theme.secondaryButton.copy(alpha = alpha),
                        theme.secondaryButton.copy(alpha = 0.70f * alpha)
                    )
                )
            )
            .border(1.5.dp, theme.secondaryButtonBorder.copy(alpha = alpha), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        iconContent()
    }
}
