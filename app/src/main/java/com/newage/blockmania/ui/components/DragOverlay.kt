package com.newage.blockmania.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import com.newage.blockmania.model.DragState
import com.newage.blockmania.theme.GameTheme

import com.newage.blockmania.model.Piece

@Composable
fun DragOverlay(
    piece: Piece?,
    touchPositionProvider: () -> Offset,
    touchOffsetProvider: () -> Offset,
    cellSize: Float,
    theme: GameTheme = GameTheme.GALAXY,
    modifier: Modifier = Modifier
) {
    if (piece == null) return

    var overlayCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                overlayCoordinates = coords
            }
    ) {
        // Read position inside Draw Scope to eliminate recomposition of parent and sibling UI
        val touchPos = touchPositionProvider()
        val touchOffset = touchOffsetProvider()
        val overlayTopLeftInRoot = overlayCoordinates?.positionInRoot() ?: Offset.Zero

        // Piece top-left in root coordinates, strictly preserving initial touch offset
        val pieceRootTopLeft = Offset(
            touchPos.x - touchOffset.x,
            touchPos.y - touchOffset.y
        )

        // Convert root coordinates to local Canvas coordinates
        val localPieceTopLeft = pieceRootTopLeft - overlayTopLeftInRoot

        for (r in 0 until piece.rowCount) {
            for (c in 0 until piece.colCount) {
                if (piece[r, c]) {
                    val blockTopLeft = Offset(
                        localPieceTopLeft.x + c * cellSize,
                        localPieceTopLeft.y + r * cellSize
                    )
                    BlockDrawing.drawOccupiedBlock(
                        drawScope = this,
                        topLeft = blockTopLeft,
                        size = Size(cellSize, cellSize),
                        color = piece.color,
                        theme = theme,
                        alpha = 0.96f,
                        scale = 1.08f,
                        isDragging = true
                    )
                }
            }
        }
    }
}

@Composable
fun DragOverlay(
    dragState: DragState?,
    cellSize: Float,
    theme: GameTheme = GameTheme.GALAXY,
    modifier: Modifier = Modifier
) {
    DragOverlay(
        piece = dragState?.piece,
        touchPositionProvider = { dragState?.let { Offset(it.touchX, it.touchY) } ?: Offset.Zero },
        touchOffsetProvider = { dragState?.let { Offset(it.touchOffsetX, it.touchOffsetY) } ?: Offset.Zero },
        cellSize = cellSize,
        theme = theme,
        modifier = modifier
    )
}
