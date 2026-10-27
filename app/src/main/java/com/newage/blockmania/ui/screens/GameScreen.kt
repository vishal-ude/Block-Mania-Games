package com.newage.blockmania.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newage.blockmania.model.ScreenState
import com.newage.blockmania.ui.components.BoardView
import com.newage.blockmania.ui.components.ComboBanner
import com.newage.blockmania.ui.components.DragOverlay
import com.newage.blockmania.ui.components.PieceTray
import com.newage.blockmania.ui.components.TopBar
import com.newage.blockmania.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val activeDragPieceIndex by viewModel.activeDragPieceIndex.collectAsState()
    val dragPreview by viewModel.dragPreviewState.collectAsState()
    var showRestartConfirm by remember { mutableStateOf(false) }
    val theme = gameState.selectedTheme

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(theme.backgroundGradient))
    ) {
        // Subtle ambient decorative shapes tailored to active theme
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val glow = theme.ambientGlowColors

            drawCircle(
                color = glow.getOrElse(0) { Color(0xFF00D2D3) }.copy(alpha = 0.05f),
                radius = w * 0.45f,
                center = Offset(w * 0.05f, h * 0.05f)
            )
            drawCircle(
                color = glow.getOrElse(1) { Color(0xFFFF4B6E) }.copy(alpha = 0.05f),
                radius = w * 0.40f,
                center = Offset(w * 0.95f, h * 0.25f)
            )
            drawCircle(
                color = glow.getOrElse(2) { Color(0xFFA55EEA) }.copy(alpha = 0.05f),
                radius = w * 0.50f,
                center = Offset(w * 0.10f, h * 0.85f)
            )
            drawCircle(
                color = glow.getOrElse(3) { Color(0xFFFFC700) }.copy(alpha = 0.04f),
                radius = w * 0.35f,
                center = Offset(w * 0.80f, h * 0.90f)
            )
        }

        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val isCompactHeight = screenHeight < 680.dp

        // Limit maximum width on tablets so controls and board stay comfortably sized and centered
        val contentMaxWidth = minOf(screenWidth - 16.dp, 520.dp)

        // Calculate responsive component heights
        val comboHeight = if (isCompactHeight) 38.dp else 48.dp
        val trayHeight = if (isCompactHeight) 110.dp else if (screenHeight > 850.dp) 135.dp else 125.dp
        val topPadding = if (isCompactHeight) 2.dp else 4.dp
        val bottomPadding = if (isCompactHeight) 4.dp else 8.dp

        // Reserved height for HUD, Combo, Tray, and Spacers
        val reservedHeight = 64.dp + comboHeight + trayHeight + topPadding + bottomPadding + 16.dp

        // Compute square board size constrained by both available width and height
        val availableWidthForBoard = (contentMaxWidth - 16.dp).coerceAtLeast(180.dp)
        val availableHeightForBoard = (screenHeight - reservedHeight).coerceAtLeast(180.dp)
        val boardSize = minOf(availableWidthForBoard, availableHeightForBoard)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding, bottom = bottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .width(contentMaxWidth)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top HUD
                TopBar(
                    score = gameState.score,
                    bestScore = gameState.bestScore,
                    isSoundEnabled = gameState.isSoundEnabled,
                    canUndo = gameState.canUndo,
                    theme = theme,
                    onSoundToggle = { viewModel.toggleSound() },
                    onUndoClick = { viewModel.undo() },
                    onThemeClick = { viewModel.openThemeDialog() },
                    onRestartClick = { showRestartConfirm = true },
                    onPauseClick = { viewModel.pauseGame() },
                    onCompetitiveHubClick = { viewModel.openCompetitiveHub() }
                )

                // Combo Banner Popup
                Box(
                    modifier = Modifier.height(comboHeight),
                    contentAlignment = Alignment.Center
                ) {
                    ComboBanner(
                        comboPopup = gameState.activeComboPopup,
                        theme = theme
                    )
                }

                // 8x8 Board (64 Cells)
                BoardView(
                    board = gameState.board,
                    dragPreview = dragPreview,
                    activeLineClear = gameState.activeLineClear,
                    lastPlacement = gameState.lastPlacement,
                    theme = theme,
                    modifier = Modifier.size(boardSize),
                    onBoardBoundsCalculated = { bounds, cellSize ->
                        viewModel.onBoardBoundsCalculated(bounds, cellSize)
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 3-Piece Tray
                PieceTray(
                    pieces = gameState.currentPieces,
                    draggedPieceIndex = activeDragPieceIndex,
                    boardCellSize = viewModel.boardCellSize,
                    trayHeight = trayHeight,
                    theme = theme,
                    onDragStart = { index, piece, pointerInRoot, touchOffsetInPiece ->
                        viewModel.onDragStart(index, piece, pointerInRoot, touchOffsetInPiece)
                    },
                    onDrag = { pointerInRoot ->
                        viewModel.onDrag(pointerInRoot)
                    },
                    onDragEnd = {
                        viewModel.onDragEnd()
                    },
                    onDragCancel = {
                        viewModel.onDragCancel()
                    }
                )
            }
        }

        // Drag Overlay rendered on top of everything (reads position directly in Draw Phase)
        DragOverlay(
            piece = viewModel.currentDraggedPiece,
            touchPositionProvider = { viewModel.currentTouchPosition },
            touchOffsetProvider = { viewModel.currentTouchOffset },
            cellSize = if (viewModel.boardCellSize > 0f) viewModel.boardCellSize else 80f,
            theme = theme
        )

        // Pause Dialog
        if (gameState.screenState == ScreenState.PAUSED) {
            PauseDialog(
                score = gameState.score,
                bestScore = gameState.bestScore,
                onResume = { viewModel.resumeGame() },
                onRestart = { viewModel.restartGame() },
                onHome = { viewModel.goToHome() },
                theme = theme
            )
        }

        // Game Over Dialog
        if (gameState.screenState == ScreenState.GAME_OVER) {
            GameOverDialog(
                score = gameState.score,
                bestScore = gameState.bestScore,
                linesCleared = gameState.sessionLinesCleared,
                maxCombo = gameState.sessionMaxCombo,
                isNewHighScore = gameState.isNewHighScore,
                newlyUnlockedAchievements = gameState.newlyUnlockedAchievements,
                canRevive = gameState.canRevive,
                onRevive = { viewModel.reviveGame() },
                onRestart = { viewModel.restartGame() },
                onHome = { viewModel.goToHome() },
                theme = theme
            )
        }

        // Theme Selection Dialog
        if (gameState.showThemeDialog) {
            ThemeSelectionDialog(
                currentTheme = theme,
                onThemeSelect = { viewModel.setTheme(it) },
                isThemeUnlocked = { viewModel.isThemeUnlocked(it) },
                getUnlockRequirement = { viewModel.getThemeUnlockRequirement(it) },
                onDismiss = { viewModel.closeThemeDialog() }
            )
        }

        // Competitive Hub Dialog
        if (gameState.showCompetitiveHub) {
            com.newage.blockmania.ui.components.CompetitiveHubDialog(
                dailyChallenge = viewModel.getDailyChallenge(),
                missions = viewModel.getMissions(),
                achievements = viewModel.getAchievements(),
                playerStats = viewModel.getPlayerStats(),
                theme = theme,
                onDismiss = { viewModel.closeCompetitiveHub() },
                onPlayDaily = {
                    viewModel.closeCompetitiveHub()
                }
            )
        }

        // Restart Confirmation Dialog
        if (showRestartConfirm) {
            AlertDialog(
                onDismissRequest = { showRestartConfirm = false },
                title = {
                    Text(
                        "Restart Game?",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                text = {
                    Text(
                        "Start a fresh puzzle? Your current score and progress will be reset.",
                        fontSize = 14.sp,
                        color = Color(0xFFD1D5DB)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showRestartConfirm = false
                            viewModel.restartGame()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B6E)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Restart", fontWeight = FontWeight.ExtraBold)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showRestartConfirm = false },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.secondaryButton),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = theme.dialogBackground,
                modifier = Modifier.border(1.5.dp, theme.dialogBorder, RoundedCornerShape(24.dp))
            )
        }
    }
}
