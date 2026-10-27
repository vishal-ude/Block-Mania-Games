package com.newage.blockmania.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newage.blockmania.model.BlockColor
import com.newage.blockmania.model.BlockShape
import com.newage.blockmania.model.Piece
import com.newage.blockmania.theme.GameTheme
import com.newage.blockmania.theme.themed
import com.newage.blockmania.ui.components.PieceRenderer

@Composable
fun StartScreen(
    bestScore: Int,
    isSoundEnabled: Boolean,
    theme: GameTheme = GameTheme.GALAXY,
    hasActiveGame: Boolean = false,
    onSoundToggle: () -> Unit,
    onThemeClick: () -> Unit = {},
    onCompetitiveHubClick: () -> Unit = {},
    onNewGameClick: () -> Unit = {},
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHowToPlay by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Entrance fade-in and scale-in animation
    val entranceAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "entranceAlpha"
    )
    val entranceScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.90f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "entranceScale"
    )

    // Looping animations for floating blocks and button pulse
    val infiniteTransition = rememberInfiniteTransition(label = "onboardingAnimations")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val floatOffset1 by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset1"
    )
    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset2"
    )

    val floatRot1 by infiniteTransition.animateFloat(
        initialValue = -3.5f,
        targetValue = 3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatRot1"
    )
    val floatRot2 by infiniteTransition.animateFloat(
        initialValue = 3.5f,
        targetValue = -3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatRot2"
    )

    // Shimmer beam across play button
    val shimmerPhase by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerPhase"
    )

    // Debounced Play Click
    var lastClickTime by remember { mutableStateOf(0L) }
    val debouncedPlayClick = {
        val now = System.currentTimeMillis()
        if (now - lastClickTime > 600L) {
            lastClickTime = now
            onPlayClick()
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(theme.backgroundGradient))
            .padding(horizontal = 20.dp)
            .padding(top = 4.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        val screenHeight = maxHeight

        // Ambient background glowing orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val glow = theme.ambientGlowColors

            drawCircle(
                color = glow.getOrElse(0) { Color(0xFF00D2D3) }.copy(alpha = 0.07f),
                radius = w * 0.45f,
                center = Offset(w * 0.10f, h * 0.15f)
            )
            drawCircle(
                color = glow.getOrElse(1) { Color(0xFFFF4B6E) }.copy(alpha = 0.07f),
                radius = w * 0.45f,
                center = Offset(w * 0.90f, h * 0.35f)
            )
            drawCircle(
                color = glow.getOrElse(2) { Color(0xFFA55EEA) }.copy(alpha = 0.07f),
                radius = w * 0.50f,
                center = Offset(w * 0.15f, h * 0.80f)
            )
            drawCircle(
                color = glow.getOrElse(3) { Color(0xFFFFC700) }.copy(alpha = 0.06f),
                radius = w * 0.40f,
                center = Offset(w * 0.85f, h * 0.85f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .heightIn(min = screenHeight - 12.dp)
                .verticalScroll(rememberScrollState())
                .alpha(entranceAlpha)
                .scale(entranceScale)
        ) {
            // ================= SECTION 1: HEADER & BRANDING =================
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Action Buttons in top corner: Competitive Hub + Theme + Sound
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Competitive Hub Button (Trophy)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Brush.verticalGradient(theme.cardBackground))
                            .border(1.5.dp, theme.cardBorder, CircleShape)
                            .clickable(onClick = onCompetitiveHubClick),
                        contentAlignment = Alignment.Center
                    ) {
                        com.newage.blockmania.ui.components.VectorIcons.TrophyIcon(
                            tint = theme.bestScoreAccent,
                            size = 18.dp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Theme Selector Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Brush.verticalGradient(theme.cardBackground))
                            .border(1.5.dp, theme.cardBorder, CircleShape)
                            .clickable(onClick = onThemeClick),
                        contentAlignment = Alignment.Center
                    ) {
                        com.newage.blockmania.ui.components.VectorIcons.ThemeIcon(tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Sound Toggle Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Brush.verticalGradient(theme.cardBackground))
                            .border(1.5.dp, theme.cardBorder, CircleShape)
                            .clickable(onClick = onSoundToggle),
                        contentAlignment = Alignment.Center
                    ) {
                        com.newage.blockmania.ui.components.VectorIcons.SoundIcon(
                            isSoundEnabled = isSoundEnabled,
                            tint = Color.White
                        )
                    }
                }

                // Game Logo Emblem
                StartScreenLogoEmblem(
                    previewColors = theme.previewColors,
                    modifier = Modifier.size(58.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Title & Subtitle Branding
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "BLOCK",
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.scoreAccent,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MANIA",
                            fontSize = 39.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.previewColors.getOrElse(0) { Color(0xFFFF4B6E) },
                            letterSpacing = 3.sp
                        )
                    }

                    Text(
                        text = "Classic 8×8 Block Puzzle Fun!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB4B9D2),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // ================= SECTION 2: ANIMATED PREVIEW & BEST SCORE =================
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Interactive Animated Puzzle Board Preview & Floating Blocks
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Floating Piece Left
                    Box(
                        modifier = Modifier
                            .offset(y = floatOffset1.dp)
                            .rotate(floatRot1)
                    ) {
                        PieceRenderer(
                            piece = Piece(shape = BlockShape.CORNER_2X2_TL, color = BlockColor.CYAN),
                            theme = theme,
                            modifier = Modifier.size(52.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Miniature Animated Puzzle Board Preview
                    MiniAnimatedBoardPreview(
                        theme = theme,
                        modifier = Modifier.size(116.dp)
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    // Floating Piece Right
                    Box(
                        modifier = Modifier
                            .offset(y = floatOffset2.dp)
                            .rotate(floatRot2)
                    ) {
                        PieceRenderer(
                            piece = Piece(shape = BlockShape.T_UP, color = BlockColor.MAGENTA),
                            theme = theme,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Best Score Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.verticalGradient(theme.cardBackground))
                        .border(1.5.dp, theme.cardBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(theme.bestScoreAccent.copy(alpha = 0.20f)),
                            contentAlignment = Alignment.Center
                        ) {
                            com.newage.blockmania.ui.components.VectorIcons.TrophyIcon(tint = theme.bestScoreAccent)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "BEST SCORE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = theme.bestScoreAccent,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = String.format("%,d", bestScore),
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // ================= SECTION 3: ACTION BUTTONS =================
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
            ) {
                // Primary Play / Resume Button
                Button(
                    onClick = debouncedPlayClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(22.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(54.dp)
                        .scale(pulseScale)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    theme.primaryButton,
                                    theme.primaryButton.copy(alpha = 0.90f),
                                    theme.primaryButton
                                )
                            )
                        )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val beamX = w * shimmerPhase
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.28f),
                                        Color.Transparent
                                    ),
                                    start = Offset(beamX - w * 0.25f, 0f),
                                    end = Offset(beamX + w * 0.25f, h)
                                ),
                                topLeft = Offset.Zero,
                                size = size
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            com.newage.blockmania.ui.components.VectorIcons.PlayIcon(
                                tint = Color.White,
                                size = 18.dp
                            )
                            Text(
                                text = if (hasActiveGame) "RESUME PUZZLE" else "PLAY NOW",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // New Game button if an active game already exists
                if (hasActiveGame) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onNewGameClick,
                        colors = ButtonDefaults.buttonColors(containerColor = theme.secondaryButton),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .height(44.dp)
                            .border(1.dp, theme.secondaryButtonBorder, RoundedCornerShape(18.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            com.newage.blockmania.ui.components.VectorIcons.RestartIcon(
                                tint = Color.White,
                                size = 16.dp
                            )
                            Text(
                                text = "NEW GAME",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Competitive Hub Button (Challenges & Missions)
                Button(
                    onClick = onCompetitiveHubClick,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.secondaryButton),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(44.dp)
                        .border(1.dp, theme.cardBorder, RoundedCornerShape(18.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        com.newage.blockmania.ui.components.VectorIcons.TrophyIcon(
                            tint = theme.bestScoreAccent,
                            size = 16.dp
                        )
                        Text(
                            text = "CHALLENGES & AWARDS",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = theme.bestScoreAccent,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // How to Play Button
                Button(
                    onClick = { showHowToPlay = true },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.secondaryButton.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(42.dp)
                        .border(1.dp, theme.secondaryButtonBorder.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        com.newage.blockmania.ui.components.VectorIcons.StarIcon(
                            tint = Color(0xFFCBD5E1),
                            size = 14.dp
                        )
                        Text(
                            text = "How To Play (8×8 Board)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }

        // How to Play Dialog
        if (showHowToPlay) {
            HowToPlayDialog(
                theme = theme,
                onDismiss = { showHowToPlay = false }
            )
        }
    }
}

/**
 * Miniature animated puzzle board preview that demonstrates placing blocks
 * and clearing lines in an engaging, lightweight loop.
 */
@Composable
private fun MiniAnimatedBoardPreview(
    theme: GameTheme,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "miniBoardLoop")

    // Timeline: 0.0 to 1.0 over 3800ms
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "miniBoardProgress"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(theme.boardBackground)
            .border(2.dp, theme.boardBorder, RoundedCornerShape(16.dp))
            .padding(6.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val totalW = size.width
            val totalH = size.height
            val gridSize = 5
            val pad = totalW * 0.045f
            val cellW = (totalW - pad * (gridSize - 1)) / gridSize
            val cellH = (totalH - pad * (gridSize - 1)) / gridSize
            val corner = CornerRadius(cellW * 0.22f, cellH * 0.22f)

            // 1. Draw 5x5 empty background cells
            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    val x = c * (cellW + pad)
                    val y = r * (cellH + pad)
                    drawRoundRect(
                        color = theme.emptyCellColor,
                        topLeft = Offset(x, y),
                        size = Size(cellW, cellH),
                        cornerRadius = corner,
                        style = Fill
                    )
                    drawRoundRect(
                        color = theme.emptyCellBorder,
                        topLeft = Offset(x, y),
                        size = Size(cellW, cellH),
                        cornerRadius = corner,
                        style = Stroke(width = 1f)
                    )
                }
            }

            // 2. Base pre-placed blocks on row 2 (cols 0, 1, 4) and row 3 (cols 1, 2)
            val staticBlocks = listOf(
                Triple(2, 0, BlockColor.PURPLE),
                Triple(2, 1, BlockColor.PURPLE),
                Triple(2, 4, BlockColor.PURPLE),
                Triple(3, 1, BlockColor.GOLD),
                Triple(3, 2, BlockColor.GOLD),
                Triple(4, 3, BlockColor.EMERALD)
            )

            // Animation phases:
            // 0.00..0.38: Piece glides from top into row 2 (cols 2 & 3)
            // 0.38..0.48: Piece snaps firmly into place (row 2 completes 5 cells!)
            // 0.48..0.72: Row 2 flashes line clear neon beam!
            // 0.72..1.00: Row 2 dissolves/cleared, resets for next cycle.
            val isPieceSnapping = progress in 0.0f..0.72f
            val isRowClearing = progress in 0.48f..0.72f
            val isRowCleared = progress > 0.72f

            // Draw static blocks
            staticBlocks.forEach { (r, c, color) ->
                if (r == 2 && isRowCleared) return@forEach // Row 2 cleared

                val x = c * (cellW + pad)
                val y = r * (cellH + pad)
                val themed = color.themed(theme)
                val alpha = if (r == 2 && isRowClearing) (1f - (progress - 0.48f) / 0.24f) else 1f

                drawRoundRect(
                    color = themed.primary.copy(alpha = alpha),
                    topLeft = Offset(x, y),
                    size = Size(cellW, cellH),
                    cornerRadius = corner,
                    style = Fill
                )
                // Top shine
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.42f * alpha),
                    topLeft = Offset(x + cellW * 0.16f, y + cellH * 0.12f),
                    size = Size(cellW * 0.68f, cellH * 0.30f),
                    cornerRadius = CornerRadius(cellW * 0.15f, cellH * 0.15f),
                    style = Fill
                )
            }

            // Draw incoming 2-cell piece (glides into row 2, cols 2 & 3)
            if (isPieceSnapping) {
                val glideFraction = (progress / 0.38f).coerceIn(0f, 1f)
                // Eased glide
                val easedY = (1f - (1f - glideFraction) * (1f - glideFraction))
                val targetY = 2 * (cellH + pad)
                val startY = -cellH * 1.5f
                val currentPieceY = startY + (targetY - startY) * easedY

                val pieceAlpha = if (isRowClearing) (1f - (progress - 0.48f) / 0.24f) else 1f
                val coralTheme = BlockColor.CORAL.themed(theme)

                listOf(2, 3).forEach { c ->
                    val x = c * (cellW + pad)
                    drawRoundRect(
                        color = coralTheme.primary.copy(alpha = pieceAlpha),
                        topLeft = Offset(x, currentPieceY),
                        size = Size(cellW, cellH),
                        cornerRadius = corner,
                        style = Fill
                    )
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.45f * pieceAlpha),
                        topLeft = Offset(x + cellW * 0.16f, currentPieceY + cellH * 0.12f),
                        size = Size(cellW * 0.68f, cellH * 0.30f),
                        cornerRadius = CornerRadius(cellW * 0.15f, cellH * 0.15f),
                        style = Fill
                    )
                }
            }

            // Draw Line Clear Flash Beam across Row 2
            if (isRowClearing) {
                val flashProgress = ((progress - 0.48f) / 0.24f).coerceIn(0f, 1f)
                val flashAlpha = (1f - flashProgress) * 0.90f
                val rowY = 2 * (cellH + pad)

                // Radiant neon beam across entire row
                drawRoundRect(
                    color = theme.lineClearGlow.copy(alpha = flashAlpha),
                    topLeft = Offset(0f, rowY - 2f),
                    size = Size(totalW, cellH + 4f),
                    cornerRadius = CornerRadius(cellH * 0.25f, cellH * 0.25f),
                    style = Fill
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = flashAlpha),
                    topLeft = Offset(0f, rowY + cellH * 0.35f),
                    size = Size(totalW, cellH * 0.30f),
                    cornerRadius = CornerRadius(cellH * 0.15f, cellH * 0.15f),
                    style = Fill
                )
            }
        }
    }
}

@Composable
private fun StartScreenLogoEmblem(
    previewColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val total = size.width
        val pad = total * 0.08f
        val cellSize = (total - pad) / 2f
        val corner = CornerRadius(cellSize * 0.28f, cellSize * 0.28f)

        val blocks = listOf(
            Pair(Offset(0f, 0f), previewColors.getOrElse(0) { Color(0xFFFF4B6E) }),
            Pair(Offset(cellSize + pad, 0f), previewColors.getOrElse(1) { Color(0xFFFFC700) }),
            Pair(Offset(0f, cellSize + pad), previewColors.getOrElse(2) { Color(0xFF00D2D3) }),
            Pair(Offset(cellSize + pad, cellSize + pad), previewColors.getOrElse(3) { Color(0xFFA55EEA) })
        )

        blocks.forEach { (offset, color) ->
            drawRoundRect(
                color = color,
                topLeft = offset,
                size = Size(cellSize, cellSize),
                cornerRadius = corner
            )
            // Top gloss shine
            drawRoundRect(
                color = Color.White.copy(alpha = 0.45f),
                topLeft = Offset(offset.x + cellSize * 0.15f, offset.y + cellSize * 0.12f),
                size = Size(cellSize * 0.70f, cellSize * 0.28f),
                cornerRadius = CornerRadius(cellSize * 0.20f, cellSize * 0.20f)
            )
        }
    }
}

@Composable
fun HowToPlayDialog(
    theme: GameTheme = GameTheme.GALAXY,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = theme.primaryButton),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Got It! Let's Play", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                com.newage.blockmania.ui.components.VectorIcons.PlayIcon(
                    tint = theme.scoreAccent,
                    size = 20.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "HOW TO PLAY (8×8)",
                    fontWeight = FontWeight.Black,
                    fontSize = 19.sp,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                RuleItem(
                    iconSlot = { com.newage.blockmania.ui.components.VectorIcons.PlayIcon(tint = theme.scoreAccent, size = 18.dp) },
                    title = "Drag & Drop",
                    desc = "Drag colorful block shapes from the tray onto the spacious 8×8 grid.",
                    theme = theme
                )
                RuleItem(
                    iconSlot = { com.newage.blockmania.ui.components.VectorIcons.StarIcon(tint = theme.bestScoreAccent, size = 18.dp) },
                    title = "Clear Full Lines",
                    desc = "Fill all 8 cells in any horizontal row or vertical column to blast them and score points!",
                    theme = theme
                )
                RuleItem(
                    iconSlot = { com.newage.blockmania.ui.components.VectorIcons.MedalIcon(tint = Color(0xFFFFD700), size = 18.dp) },
                    title = "Combos & Multipliers",
                    desc = "Clear multiple lines in a single drop or consecutive turns for explosive combo multipliers!",
                    theme = theme
                )
                RuleItem(
                    iconSlot = { com.newage.blockmania.ui.components.VectorIcons.UndoIcon(tint = Color.White, size = 18.dp) },
                    title = "Undo Moves",
                    desc = "Made a placement mistake? Tap the undo button anytime to reverse your last move.",
                    theme = theme
                )
                RuleItem(
                    iconSlot = { com.newage.blockmania.ui.components.VectorIcons.TrophyIcon(tint = theme.scoreAccent, size = 18.dp) },
                    title = "Keep Space Open",
                    desc = "The game ends when none of the 3 available pieces can fit on the board.",
                    theme = theme
                )
            }
        },
        shape = RoundedCornerShape(26.dp),
        containerColor = theme.dialogBackground,
        modifier = Modifier.border(1.5.dp, theme.dialogBorder, RoundedCornerShape(26.dp))
    )
}

@Composable
private fun RuleItem(
    iconSlot: @Composable () -> Unit,
    title: String,
    desc: String,
    theme: GameTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(theme.slotBackground)
            .border(1.dp, theme.slotBorder, RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(theme.cardBorder.copy(alpha = 0.35f))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                iconSlot()
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.ExtraBold, fontSize = 13.5.sp, color = Color.White)
                Text(text = desc, fontSize = 11.sp, color = Color(0xFFCBD5E1), lineHeight = 15.sp)
            }
        }
    }
}
