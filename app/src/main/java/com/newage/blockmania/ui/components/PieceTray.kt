package com.newage.blockmania.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newage.blockmania.model.Piece
import com.newage.blockmania.theme.GameTheme

object PieceLayoutHelper {
    data class SlotPieceMetrics(
        val startX: Float,
        val startY: Float,
        val miniCellSize: Float,
        val totalWidth: Float,
        val totalHeight: Float
    )

    fun calculateSlotMetrics(
        availableWidth: Float,
        availableHeight: Float,
        rowCount: Int,
        colCount: Int
    ): SlotPieceMetrics {
        val miniCellSize = minOf(
            availableWidth / (colCount + 0.4f),
            availableHeight / (rowCount + 0.4f),
            availableWidth / 4.2f
        )
        val totalWidth = colCount * miniCellSize
        val totalHeight = rowCount * miniCellSize
        val startX = (availableWidth - totalWidth) / 2f
        val startY = (availableHeight - totalHeight) / 2f
        return SlotPieceMetrics(startX, startY, miniCellSize, totalWidth, totalHeight)
    }
}

@Composable
fun PieceTray(
    pieces: List<Piece>,
    draggedPieceIndex: Int?,
    boardCellSize: Float,
    onDragStart: (Int, Piece, Offset, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    modifier: Modifier = Modifier,
    trayHeight: Dp = 125.dp,
    theme: GameTheme = GameTheme.GALAXY
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.verticalGradient(theme.trayBackground))
            .border(1.5.dp, theme.trayBorder, RoundedCornerShape(22.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(trayHeight)
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (index in 0 until 3) {
                val piece = pieces.getOrNull(index)
                PieceSlot(
                    piece = piece,
                    isBeingDragged = draggedPieceIndex == index,
                    boardCellSize = boardCellSize,
                    theme = theme,
                    onDragStart = { pointerInRoot, touchOffsetInPiece ->
                        if (piece != null && !piece.isUsed) {
                            onDragStart(index, piece, pointerInRoot, touchOffsetInPiece)
                        }
                    },
                    onDrag = onDrag,
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun PieceSlot(
    piece: Piece?,
    isBeingDragged: Boolean,
    boardCellSize: Float,
    theme: GameTheme,
    onDragStart: (Offset, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var slotBoundsInRoot by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }
    var slotCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var slotSize by remember { mutableStateOf(Size.Zero) }

    Box(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(theme.slotBackground)
            .border(1.dp, theme.slotBorder, RoundedCornerShape(16.dp))
            .onGloballyPositioned { coords ->
                slotCoordinates = coords
                slotBoundsInRoot = coords.boundsInRoot()
                slotSize = Size(coords.size.width.toFloat(), coords.size.height.toFloat())
            }
            .pointerInput(piece?.id, piece?.isUsed, boardCellSize) {
                if (piece == null || piece.isUsed) return@pointerInput

                val effectiveCellSize = if (boardCellSize > 0f) boardCellSize else 80f
                // Comfortable ergonomic lift so finger contact point does not obscure the board
                val liftOffset = effectiveCellSize * 0.60f
                val touchOffsetX = (piece.colCount * effectiveCellSize) / 2f
                val touchOffsetY = (piece.rowCount * effectiveCellSize) / 2f + liftOffset
                val touchOffset = Offset(touchOffsetX, touchOffsetY)

                // Low-latency 3dp activation threshold: distinguishes taps from drags without default slop lag
                val dragThresholdPx = 3.dp.toPx()

                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val pointerId = down.id
                    var isDragging = false
                    var totalDisplacement = Offset.Zero

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == pointerId } ?: break

                        if (change.isConsumed) {
                            if (isDragging) {
                                onDragCancel()
                            }
                            break
                        }

                        if (!change.pressed) {
                            change.consume()
                            if (isDragging) {
                                onDragEnd()
                            }
                            break
                        }

                        if (!isDragging) {
                            totalDisplacement += change.position - down.position
                            if (totalDisplacement.getDistance() >= dragThresholdPx) {
                                isDragging = true
                                change.consume()
                                val pointerInRoot = slotCoordinates?.takeIf { it.isAttached }?.localToRoot(change.position)
                                    ?: Offset(slotBoundsInRoot.left + change.position.x, slotBoundsInRoot.top + change.position.y)
                                onDragStart(pointerInRoot, touchOffset)
                            }
                        } else {
                            change.consume()
                            val pointerInRoot = slotCoordinates?.takeIf { it.isAttached }?.localToRoot(change.position)
                                ?: Offset(slotBoundsInRoot.left + change.position.x, slotBoundsInRoot.top + change.position.y)
                            onDrag(pointerInRoot)
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (piece != null && !piece.isUsed && !isBeingDragged) {
            PieceRenderer(
                piece = piece,
                theme = theme,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun PieceRenderer(
    piece: Piece,
    modifier: Modifier = Modifier,
    theme: GameTheme = GameTheme.GALAXY,
    alpha: Float = if (piece.canFit) 1.0f else 0.40f
) {
    Canvas(modifier = modifier) {
        val metrics = PieceLayoutHelper.calculateSlotMetrics(
            availableWidth = size.width,
            availableHeight = size.height,
            rowCount = piece.rowCount,
            colCount = piece.colCount
        )

        for (r in 0 until piece.rowCount) {
            for (c in 0 until piece.colCount) {
                if (piece[r, c]) {
                    val topLeft = Offset(
                        metrics.startX + c * metrics.miniCellSize,
                        metrics.startY + r * metrics.miniCellSize
                    )
                    BlockDrawing.drawOccupiedBlock(
                        drawScope = this,
                        topLeft = topLeft,
                        size = Size(metrics.miniCellSize, metrics.miniCellSize),
                        color = piece.color,
                        theme = theme,
                        alpha = alpha
                    )
                }
            }
        }
    }
}
