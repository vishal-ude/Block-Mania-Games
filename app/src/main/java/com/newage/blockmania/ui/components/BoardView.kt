package com.newage.blockmania.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.newage.blockmania.model.BOARD_SIZE
import com.newage.blockmania.model.Board
import com.newage.blockmania.model.DragState
import com.newage.blockmania.model.LineClearEffect
import com.newage.blockmania.model.PlacementAnimation
import com.newage.blockmania.theme.GameTheme

@Composable
fun BoardView(
    board: Board,
    dragPreview: com.newage.blockmania.model.DragPreviewState? = null,
    dragState: DragState? = null,
    activeLineClear: LineClearEffect? = null,
    lastPlacement: PlacementAnimation? = null,
    theme: GameTheme = GameTheme.GALAXY,
    modifier: Modifier = Modifier,
    onBoardBoundsCalculated: (Rect, Float) -> Unit
) {
    // Flash animation for line clears
    val lineClearAnim = remember(activeLineClear?.timestamp) { Animatable(1f) }

    LaunchedEffect(activeLineClear?.timestamp) {
        if (activeLineClear != null) {
            lineClearAnim.snapTo(1f)
            lineClearAnim.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
        }
    }

    // Gentle pop bounce animation for newly placed blocks
    val popAnim = remember(lastPlacement?.timestamp) { Animatable(0f) }

    LaunchedEffect(lastPlacement?.timestamp) {
        if (lastPlacement != null) {
            popAnim.snapTo(1f)
            popAnim.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
            )
        }
    }

    val preview = dragPreview ?: dragState?.let {
        com.newage.blockmania.model.DragPreviewState(
            piece = it.piece,
            targetRow = it.targetRow,
            targetCol = it.targetCol,
            isValid = it.isValid,
            potentialClearedRows = it.potentialClearedRows,
            potentialClearedCols = it.potentialClearedCols,
            isOverBoard = it.isOverBoard
        )
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(8.dp)
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInRoot()
                val cellSize = bounds.width / BOARD_SIZE.toFloat()
                onBoardBoundsCalculated(bounds, cellSize)
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val boardWidth = size.width
            val boardHeight = size.height
            val cellSize = boardWidth / BOARD_SIZE.toFloat()
            val boardCornerRadius = CornerRadius(boardWidth * 0.04f, boardHeight * 0.04f)

            // 1. Board background container with shadow and bevel
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.45f),
                topLeft = Offset(0f, 6f),
                size = Size(boardWidth, boardHeight),
                cornerRadius = boardCornerRadius,
                style = Fill
            )
            drawRoundRect(
                color = theme.boardBackground,
                topLeft = Offset.Zero,
                size = Size(boardWidth, boardHeight),
                cornerRadius = boardCornerRadius,
                style = Fill
            )
            drawRoundRect(
                color = theme.boardBorder,
                topLeft = Offset.Zero,
                size = Size(boardWidth, boardHeight),
                cornerRadius = boardCornerRadius,
                style = Stroke(width = 3.5f)
            )

            // 2. Draw 8x8 empty cells (64 cells)
            for (r in 0 until BOARD_SIZE) {
                for (c in 0 until BOARD_SIZE) {
                    val cellTopLeft = Offset(c * cellSize, r * cellSize)
                    val cellSizeObj = Size(cellSize, cellSize)

                    val isLineClearing = activeLineClear != null &&
                            (r in activeLineClear.rows || c in activeLineClear.cols)

                    BlockDrawing.drawEmptyCell(
                        drawScope = this,
                        topLeft = cellTopLeft,
                        size = cellSizeObj,
                        theme = theme,
                        isFlashing = isLineClearing && lineClearAnim.value > 0.3f
                    )
                }
            }

            // 3. Draw occupied blocks on the board
            val recentlyPlaced = lastPlacement?.cells ?: emptyList()

            for (r in 0 until BOARD_SIZE) {
                for (c in 0 until BOARD_SIZE) {
                    val color = board[r, c]
                    if (color != null) {
                        val cellTopLeft = Offset(c * cellSize, r * cellSize)
                        val cellSizeObj = Size(cellSize, cellSize)

                        val isClearing = activeLineClear != null &&
                                (r in activeLineClear.rows || c in activeLineClear.cols)

                        val isJustPlaced = (r to c) in recentlyPlaced && popAnim.value > 0.01f

                        val alpha = if (isClearing) lineClearAnim.value else 1.0f
                        val scale = if (isClearing) {
                            1.0f + (1f - lineClearAnim.value) * 0.28f
                        } else if (isJustPlaced) {
                            1.0f + popAnim.value * 0.16f
                        } else {
                            1.0f
                        }

                        BlockDrawing.drawOccupiedBlock(
                            drawScope = this,
                            topLeft = cellTopLeft,
                            size = cellSizeObj,
                            color = color,
                            theme = theme,
                            alpha = alpha,
                            scale = scale
                        )
                    }
                }
            }

            // 4. Draw Ghost Placement Preview (Instantaneous Snap)
            if (preview != null && preview.isValid && preview.targetRow != null && preview.targetCol != null) {
                val piece = preview.piece
                val startRow = preview.targetRow
                val startCol = preview.targetCol

                for (r in 0 until piece.rowCount) {
                    for (c in 0 until piece.colCount) {
                        if (piece[r, c]) {
                            val boardRow = startRow + r
                            val boardCol = startCol + c
                            val cellTopLeft = Offset(boardCol * cellSize, boardRow * cellSize)
                            val cellSizeObj = Size(cellSize, cellSize)
                            BlockDrawing.drawGhostBlock(
                                drawScope = this,
                                topLeft = cellTopLeft,
                                size = cellSizeObj,
                                color = piece.color,
                                theme = theme
                            )
                        }
                    }
                }

                // 5. Highlight potential lines to clear while hovering
                if (preview.potentialClearedRows.isNotEmpty() || preview.potentialClearedCols.isNotEmpty()) {
                    for (row in preview.potentialClearedRows) {
                        drawRoundRect(
                            color = theme.lineClearGlow.copy(alpha = 0.22f),
                            topLeft = Offset(0f, row * cellSize),
                            size = Size(boardWidth, cellSize),
                            cornerRadius = CornerRadius(4f, 4f),
                            style = Fill
                        )
                    }
                    for (col in preview.potentialClearedCols) {
                        drawRoundRect(
                            color = theme.lineClearGlow.copy(alpha = 0.22f),
                            topLeft = Offset(col * cellSize, 0f),
                            size = Size(cellSize, boardHeight),
                            cornerRadius = CornerRadius(4f, 4f),
                            style = Fill
                        )
                    }
                }
            } else if (preview != null && !preview.isValid && preview.targetRow != null && preview.targetCol != null) {
                // Distinct invalid placement warning hover indicator
                val piece = preview.piece
                val startRow = preview.targetRow
                val startCol = preview.targetCol

                for (r in 0 until piece.rowCount) {
                    for (c in 0 until piece.colCount) {
                        if (piece[r, c]) {
                            val boardRow = startRow + r
                            val boardCol = startCol + c
                            if (boardRow in 0 until BOARD_SIZE && boardCol in 0 until BOARD_SIZE) {
                                val cellTopLeft = Offset(boardCol * cellSize, boardRow * cellSize)
                                val cellSizeObj = Size(cellSize, cellSize)
                                BlockDrawing.drawInvalidHoverBlock(
                                    drawScope = this,
                                    topLeft = cellTopLeft,
                                    size = cellSizeObj
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
