package com.newage.blockmania.engine

import com.newage.blockmania.model.BOARD_SIZE
import com.newage.blockmania.model.Board
import com.newage.blockmania.model.Piece

data class ScoreResult(
    val pointsEarned: Int,
    val linesCleared: Int,
    val newStreak: Int,
    val comboBonus: Int
)

data class PlacementResult(
    val success: Boolean,
    val newBoard: Board,
    val clearedRows: Set<Int>,
    val clearedCols: Set<Int>,
    val scoreResult: ScoreResult,
    val updatedPieces: List<Piece>,
    val isHandCompleted: Boolean,
    val isGameOver: Boolean
)

object GameEngine {

    /**
     * Checks if a piece can be legally placed on the board at the given top-left offset.
     */
    fun canPlacePiece(board: Board, piece: Piece, startRow: Int, startCol: Int): Boolean {
        for (r in 0 until piece.rowCount) {
            for (c in 0 until piece.colCount) {
                if (piece[r, c]) {
                    val boardRow = startRow + r
                    val boardCol = startCol + c

                    // Bounds check
                    if (boardRow !in 0 until BOARD_SIZE || boardCol !in 0 until BOARD_SIZE) {
                        return false
                    }

                    // Collision check
                    if (board.isOccupied(boardRow, boardCol)) {
                        return false
                    }
                }
            }
        }
        return true
    }

    /**
     * Places a piece on the board without line clearing.
     */
    fun placePieceOnBoard(board: Board, piece: Piece, startRow: Int, startCol: Int): Board {
        val placements = mutableListOf<Triple<Int, Int, com.newage.blockmania.model.BlockColor>>()
        for (r in 0 until piece.rowCount) {
            for (c in 0 until piece.colCount) {
                if (piece[r, c]) {
                    placements.add(Triple(startRow + r, startCol + c, piece.color))
                }
            }
        }
        return board.withPlacements(placements)
    }

    /**
     * Identifies all fully occupied horizontal rows and vertical columns.
     */
    fun findCompletedLines(board: Board): Pair<Set<Int>, Set<Int>> {
        val fullRows = mutableSetOf<Int>()
        val fullCols = mutableSetOf<Int>()

        // Check horizontal rows
        for (r in 0 until BOARD_SIZE) {
            var rowFull = true
            for (c in 0 until BOARD_SIZE) {
                if (board.isEmpty(r, c)) {
                    rowFull = false
                    break
                }
            }
            if (rowFull) fullRows.add(r)
        }

        // Check vertical columns
        for (c in 0 until BOARD_SIZE) {
            var colFull = true
            for (r in 0 until BOARD_SIZE) {
                if (board.isEmpty(r, c)) {
                    colFull = false
                    break
                }
            }
            if (colFull) fullCols.add(c)
        }

        return Pair(fullRows, fullCols)
    }

    /**
     * Preview hypothetical line clears if a piece were placed at (startRow, startCol).
     */
    fun previewCompletedLines(
        board: Board,
        piece: Piece,
        startRow: Int,
        startCol: Int
    ): Pair<Set<Int>, Set<Int>> {
        if (!canPlacePiece(board, piece, startRow, startCol)) {
            return Pair(emptySet(), emptySet())
        }
        val hypotheticalBoard = placePieceOnBoard(board, piece, startRow, startCol)
        return findCompletedLines(hypotheticalBoard)
    }

    /**
     * Checks if a piece can fit in at least one position on the board.
     */
    fun canPieceFitAnywhere(board: Board, piece: Piece): Boolean {
        val maxRow = BOARD_SIZE - piece.rowCount
        val maxCol = BOARD_SIZE - piece.colCount

        if (maxRow < 0 || maxCol < 0) return false

        for (r in 0..maxRow) {
            for (c in 0..maxCol) {
                if (canPlacePiece(board, piece, r, c)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Updates the canFit status for all pieces.
     */
    fun updatePiecesFitStatus(board: Board, pieces: List<Piece>): List<Piece> {
        return pieces.map { piece ->
            if (piece.isUsed) {
                piece.copy(canFit = false)
            } else {
                piece.copy(canFit = canPieceFitAnywhere(board, piece))
            }
        }
    }

    /**
     * Calculates score and combo bonuses for a move.
     */
    fun calculateScore(placedCells: Int, linesCleared: Int, currentStreak: Int): ScoreResult {
        var points = placedCells

        if (linesCleared == 0) {
            return ScoreResult(
                pointsEarned = points,
                linesCleared = 0,
                newStreak = 0,
                comboBonus = 0
            )
        }

        // Base line clear points
        val lineBonus = when (linesCleared) {
            1 -> 10
            2 -> 30
            3 -> 60
            4 -> 100
            5 -> 150
            else -> 150 + (linesCleared - 5) * 50
        }

        val newStreak = currentStreak + 1
        val comboBonus = if (newStreak > 1) {
            (newStreak - 1) * 15 * linesCleared
        } else {
            0
        }

        points += lineBonus + comboBonus

        return ScoreResult(
            pointsEarned = points,
            linesCleared = linesCleared,
            newStreak = newStreak,
            comboBonus = comboBonus
        )
    }

    /**
     * Executes a full piece placement move.
     */
    fun executePlacement(
        board: Board,
        pieces: List<Piece>,
        pieceIndex: Int,
        startRow: Int,
        startCol: Int,
        currentStreak: Int
    ): PlacementResult {
        val piece = pieces.getOrNull(pieceIndex)
        if (piece == null || piece.isUsed || !canPlacePiece(board, piece, startRow, startCol)) {
            return PlacementResult(
                success = false,
                newBoard = board,
                clearedRows = emptySet(),
                clearedCols = emptySet(),
                scoreResult = ScoreResult(0, 0, currentStreak, 0),
                updatedPieces = pieces,
                isHandCompleted = false,
                isGameOver = false
            )
        }

        // 1. Place blocks onto board
        val boardWithPiece = placePieceOnBoard(board, piece, startRow, startCol)

        // 2. Detect completed lines (simultaneous row and col clears)
        val (clearedRows, clearedCols) = findCompletedLines(boardWithPiece)
        val totalLinesCleared = clearedRows.size + clearedCols.size

        // 3. Clear lines from board
        val boardAfterClears = boardWithPiece.clearLines(clearedRows, clearedCols)

        // 4. Calculate score
        val scoreResult = calculateScore(piece.cellCount, totalLinesCleared, currentStreak)

        // 5. Mark piece as used
        val updatedPieces = pieces.mapIndexed { idx, p ->
            if (idx == pieceIndex) p.copy(isUsed = true, canFit = false) else p
        }

        val isHandCompleted = updatedPieces.all { it.isUsed }

        // 6. Check game over: update fit status for remaining pieces on the board AFTER clears
        val piecesWithFit = if (!isHandCompleted) {
            updatePiecesFitStatus(boardAfterClears, updatedPieces)
        } else {
            updatedPieces
        }

        val remainingUnused = piecesWithFit.filter { !it.isUsed }
        val isGameOver = !isHandCompleted && remainingUnused.isNotEmpty() && remainingUnused.none { it.canFit }

        return PlacementResult(
            success = true,
            newBoard = boardAfterClears,
            clearedRows = clearedRows,
            clearedCols = clearedCols,
            scoreResult = scoreResult,
            updatedPieces = piecesWithFit,
            isHandCompleted = isHandCompleted,
            isGameOver = isGameOver
        )
    }
}
