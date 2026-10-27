package com.newage.blockmania.engine

import androidx.compose.ui.geometry.Rect
import com.newage.blockmania.model.BOARD_SIZE
import com.newage.blockmania.model.Board
import com.newage.blockmania.model.Piece
import kotlin.math.roundToInt

/**
 * Intelligent Drag & Snap Sensitivity Helper.
 *
 * Implements forgiving, smart drag-and-drop mechanics inspired by popular block puzzle
 * games like Block Blast:
 * 1. Converts screen touch coordinates to board fractional coordinates accurately.
 * 2. Clamps piece coordinates to board boundaries so multi-cell blocks never jump or overflow near edges.
 * 3. Primary snap: checks the exact rounded target position.
 * 4. Forgiving magnetic snap: if the primary target is blocked or offset, searches valid neighboring cells
 *    within a generous tolerance radius (1.25 cells) and magnetically snaps to the closest valid move.
 * 5. Clear invalid feedback: when no valid position exists within tolerance, retains the clamped target
 *    and flags it as invalid so the UI can render clear collision/warning feedback.
 */
object DragSensitivityHelper {

    /**
     * Snap tolerance radius in cell units.
     * When the primary rounded cell is blocked, forgiving snap will assist only
     * if the finger is within this distance of an open valid neighbor.
     * If the player is firmly centered on the blocked cell (distance 1.0 to neighbor > 0.95),
     * it does NOT unpredictably jump; it shows the invalid collision indicator in-place.
     */
    const val SNAP_TOLERANCE_CELLS = 0.95f

    /**
     * Edge tolerance margin in cell units outside board placement boundaries.
     * Allows players to easily snap to the outer columns and rows (row 0/7, col 0/7)
     * near screen edges, while correctly ignoring drags far away in the tray or off-screen.
     */
    const val BOARD_EDGE_TOLERANCE_CELLS = 0.85f

    data class PlacementResult(
        val targetRow: Int?,
        val targetCol: Int?,
        val isValid: Boolean,
        val potentialClearedRows: Set<Int> = emptySet(),
        val potentialClearedCols: Set<Int> = emptySet(),
        val isOverBoard: Boolean = false
    )

    /**
     * Calculates the smart grid placement for a dragged piece.
     */
    fun calculatePlacement(
        board: Board,
        boardBounds: Rect,
        boardCellSize: Float,
        piece: Piece,
        touchX: Float,
        touchY: Float,
        touchOffsetX: Float,
        touchOffsetY: Float
    ): PlacementResult {
        if (boardBounds.isEmpty || boardCellSize <= 0f) {
            return PlacementResult(
                targetRow = null,
                targetCol = null,
                isValid = false,
                isOverBoard = false
            )
        }

        // 1. Top-left coordinate of the piece in root window coordinates
        val pieceTopLeftX = touchX - touchOffsetX
        val pieceTopLeftY = touchY - touchOffsetY

        // 2. Convert to fractional grid coordinates relative to board bounds
        val colFloat = (pieceTopLeftX - boardBounds.left) / boardCellSize
        val rowFloat = (pieceTopLeftY - boardBounds.top) / boardCellSize

        val maxRow = BOARD_SIZE - piece.rowCount
        val maxCol = BOARD_SIZE - piece.colCount

        if (maxRow < 0 || maxCol < 0) {
            return PlacementResult(
                targetRow = null,
                targetCol = null,
                isValid = false,
                isOverBoard = false
            )
        }

        // 3. Check if piece is in board interaction zone
        val isOverBoard = rowFloat >= -BOARD_EDGE_TOLERANCE_CELLS &&
                rowFloat <= maxRow + BOARD_EDGE_TOLERANCE_CELLS &&
                colFloat >= -BOARD_EDGE_TOLERANCE_CELLS &&
                colFloat <= maxCol + BOARD_EDGE_TOLERANCE_CELLS

        if (!isOverBoard) {
            return PlacementResult(
                targetRow = null,
                targetCol = null,
                isValid = false,
                isOverBoard = false
            )
        }

        // 4. Clamped ideal target (prevents going outside board boundaries)
        val clampedRowFloat = rowFloat.coerceIn(0f, maxRow.toFloat())
        val clampedColFloat = colFloat.coerceIn(0f, maxCol.toFloat())

        val idealRow = clampedRowFloat.roundToInt().coerceIn(0, maxRow)
        val idealCol = clampedColFloat.roundToInt().coerceIn(0, maxCol)

        // 5. Primary Check: If the primary rounded cell is valid, snap directly to it
        if (GameEngine.canPlacePiece(board, piece, idealRow, idealCol)) {
            val (potRows, potCols) = GameEngine.previewCompletedLines(board, piece, idealRow, idealCol)
            return PlacementResult(
                targetRow = idealRow,
                targetCol = idealCol,
                isValid = true,
                potentialClearedRows = potRows,
                potentialClearedCols = potCols,
                isOverBoard = true
            )
        }

        // 6. Forgiving Smart Snap: If ideal cell is blocked, search neighborhood within SNAP_TOLERANCE_CELLS
        var bestCandidateRow: Int? = null
        var bestCandidateCol: Int? = null
        var minDistanceSq = Float.MAX_VALUE
        val maxDistSq = SNAP_TOLERANCE_CELLS * SNAP_TOLERANCE_CELLS

        val searchRadius = 1
        val minR = (idealRow - searchRadius).coerceAtLeast(0)
        val maxR = (idealRow + searchRadius).coerceAtMost(maxRow)
        val minC = (idealCol - searchRadius).coerceAtLeast(0)
        val maxC = (idealCol + searchRadius).coerceAtMost(maxCol)

        for (r in minR..maxR) {
            for (c in minC..maxC) {
                if (r == idealRow && c == idealCol) continue

                if (GameEngine.canPlacePiece(board, piece, r, c)) {
                    val dr = r - clampedRowFloat
                    val dc = c - clampedColFloat
                    val distSq = dr * dr + dc * dc

                    if (distSq <= maxDistSq && distSq < minDistanceSq) {
                        minDistanceSq = distSq
                        bestCandidateRow = r
                        bestCandidateCol = c
                    }
                }
            }
        }

        // 7. If a valid neighbor is found within tolerance, magnetically snap to it!
        if (bestCandidateRow != null && bestCandidateCol != null) {
            val (potRows, potCols) = GameEngine.previewCompletedLines(board, piece, bestCandidateRow, bestCandidateCol)
            return PlacementResult(
                targetRow = bestCandidateRow,
                targetCol = bestCandidateCol,
                isValid = true,
                potentialClearedRows = potRows,
                potentialClearedCols = potCols,
                isOverBoard = true
            )
        }

        // 8. If no valid placement within tolerance:
        // Retain idealRow & idealCol with isValid = false so the UI can show the invalid hover indicator
        return PlacementResult(
            targetRow = idealRow,
            targetCol = idealCol,
            isValid = false,
            potentialClearedRows = emptySet(),
            potentialClearedCols = emptySet(),
            isOverBoard = true
        )
    }
}
