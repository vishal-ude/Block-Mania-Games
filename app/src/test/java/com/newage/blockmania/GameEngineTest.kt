package com.newage.blockmania

import com.newage.blockmania.engine.BlockGenerator
import com.newage.blockmania.engine.GameEngine
import com.newage.blockmania.model.BOARD_SIZE
import com.newage.blockmania.model.BlockColor
import com.newage.blockmania.model.BlockShape
import com.newage.blockmania.model.Board
import com.newage.blockmania.model.Piece
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameEngineTest {

    @Test
    fun testValidPlacementOnEmptyRegion() {
        val board = Board.EMPTY
        val piece = Piece(shape = BlockShape.SQUARE_2X2, color = BlockColor.GOLD)

        assertTrue(GameEngine.canPlacePiece(board, piece, 0, 0))
        assertTrue(GameEngine.canPlacePiece(board, piece, 6, 6))

        val result = GameEngine.executePlacement(
            board = board,
            pieces = listOf(piece),
            pieceIndex = 0,
            startRow = 2,
            startCol = 3,
            currentStreak = 0
        )

        assertTrue(result.success)
        assertEquals(BlockColor.GOLD, result.newBoard[2, 3])
        assertEquals(BlockColor.GOLD, result.newBoard[2, 4])
        assertEquals(BlockColor.GOLD, result.newBoard[3, 3])
        assertEquals(BlockColor.GOLD, result.newBoard[3, 4])
        assertEquals(4, result.scoreResult.pointsEarned)
        assertTrue(result.updatedPieces[0].isUsed)
    }

    @Test
    fun testInvalidPlacementOutOfBoundsOrCollision() {
        val board = Board.EMPTY
        val piece = Piece(shape = BlockShape.LINE_3_H, color = BlockColor.CYAN)

        // Out of bounds horizontally (start at col 6 for 3-wide line: spans 6, 7, 8 -> 8 is out of 8x8)
        assertFalse(GameEngine.canPlacePiece(board, piece, 0, 6))
        // Out of bounds vertically
        assertFalse(GameEngine.canPlacePiece(board, piece, 8, 0))
        // Negative coordinates
        assertFalse(GameEngine.canPlacePiece(board, piece, -1, 0))

        // Pre-fill cell (2, 2)
        val occupiedBoard = board.withCell(2, 2, BlockColor.CORAL)
        // Trying to place a piece that overlaps cell (2, 2)
        assertFalse(GameEngine.canPlacePiece(occupiedBoard, piece, 2, 1))

        // Executing invalid placement returns failure without mutating board
        val result = GameEngine.executePlacement(
            board = occupiedBoard,
            pieces = listOf(piece),
            pieceIndex = 0,
            startRow = 2,
            startCol = 1,
            currentStreak = 0
        )
        assertFalse(result.success)
        assertEquals(occupiedBoard, result.newBoard)
        assertEquals(0, result.scoreResult.pointsEarned)
    }

    @Test
    fun testCompletedRowClearsCorrectly() {
        var board = Board.EMPTY
        // Fill 7 cells in row 4
        for (c in 0 until 7) {
            board = board.withCell(4, c, BlockColor.BLUE)
        }

        // Place 1-dot in row 4, col 7 to complete the 8-cell row
        val dotPiece = Piece(shape = BlockShape.DOT, color = BlockColor.ORANGE)
        val result = GameEngine.executePlacement(
            board = board,
            pieces = listOf(dotPiece),
            pieceIndex = 0,
            startRow = 4,
            startCol = 7,
            currentStreak = 0
        )

        assertTrue(result.success)
        assertEquals(setOf(4), result.clearedRows)
        assertTrue(result.clearedCols.isEmpty())

        // Row 4 must be completely cleared (all empty)
        for (c in 0 until BOARD_SIZE) {
            assertTrue("Cell (4, $c) should be empty after clear", result.newBoard.isEmpty(4, c))
        }

        // Score: 1 cell placed (1) + 1 line bonus (10) = 11
        assertEquals(11, result.scoreResult.pointsEarned)
        assertEquals(1, result.scoreResult.newStreak)
    }

    @Test
    fun testCompletedColumnClearsCorrectly() {
        var board = Board.EMPTY
        // Fill 7 cells in col 7
        for (r in 0 until 7) {
            board = board.withCell(r, 7, BlockColor.PURPLE)
        }

        val dotPiece = Piece(shape = BlockShape.DOT, color = BlockColor.EMERALD)
        val result = GameEngine.executePlacement(
            board = board,
            pieces = listOf(dotPiece),
            pieceIndex = 0,
            startRow = 7,
            startCol = 7,
            currentStreak = 0
        )

        assertTrue(result.success)
        assertTrue(result.clearedRows.isEmpty())
        assertEquals(setOf(7), result.clearedCols)

        // Col 7 must be empty
        for (r in 0 until BOARD_SIZE) {
            assertTrue("Cell ($r, 7) should be empty after clear", result.newBoard.isEmpty(r, 7))
        }

        assertEquals(11, result.scoreResult.pointsEarned)
    }

    @Test
    fun testSimultaneousMultipleLinesClearInOneMove() {
        var board = Board.EMPTY
        // Set up row 0 almost full (except col 0)
        for (c in 1 until BOARD_SIZE) {
            board = board.withCell(0, c, BlockColor.CORAL)
        }
        // Set up col 0 almost full (except row 0)
        for (r in 1 until BOARD_SIZE) {
            board = board.withCell(r, 0, BlockColor.MAGENTA)
        }

        // Placing DOT at (0, 0) completes BOTH row 0 and column 0 in the exact same move!
        val dotPiece = Piece(shape = BlockShape.DOT, color = BlockColor.GOLD)
        val result = GameEngine.executePlacement(
            board = board,
            pieces = listOf(dotPiece),
            pieceIndex = 0,
            startRow = 0,
            startCol = 0,
            currentStreak = 0
        )

        assertTrue(result.success)
        assertEquals(setOf(0), result.clearedRows)
        assertEquals(setOf(0), result.clearedCols)
        assertEquals(2, result.scoreResult.linesCleared)

        // Both row 0 and col 0 should be empty
        for (i in 0 until BOARD_SIZE) {
            assertTrue(result.newBoard.isEmpty(0, i))
            assertTrue(result.newBoard.isEmpty(i, 0))
        }

        // 1 placed cell + 30 line bonus = 31 pts
        assertEquals(31, result.scoreResult.pointsEarned)
    }

    @Test
    fun testComboStreakScoring() {
        // First move clears 1 line: streak goes from 0 to 1 (no combo multiplier on first streak)
        val res1 = GameEngine.calculateScore(placedCells = 1, linesCleared = 1, currentStreak = 0)
        assertEquals(11, res1.pointsEarned)
        assertEquals(1, res1.newStreak)
        assertEquals(0, res1.comboBonus)

        // Second consecutive move clears 1 line: streak goes from 1 to 2 -> combo bonus awarded!
        val res2 = GameEngine.calculateScore(placedCells = 1, linesCleared = 1, currentStreak = 1)
        assertEquals(2, res2.newStreak)
        assertTrue(res2.comboBonus > 0)
        assertEquals(1 + 10 + res2.comboBonus, res2.pointsEarned)

        // Move with 0 lines cleared resets streak
        val res3 = GameEngine.calculateScore(placedCells = 4, linesCleared = 0, currentStreak = 2)
        assertEquals(0, res3.newStreak)
        assertEquals(4, res3.pointsEarned)
    }

    @Test
    fun testCanAnyPieceFitAndGameOver() {
        // Completely fill the board with blocks except one isolated 1x1 cell at (0, 0)
        var board = Board.EMPTY
        for (r in 0 until BOARD_SIZE) {
            for (c in 0 until BOARD_SIZE) {
                if (r != 0 || c != 0) {
                    board = board.withCell(r, c, BlockColor.BLUE)
                }
            }
        }

        // A 2x2 square cannot fit on this board
        val squarePiece = Piece(shape = BlockShape.SQUARE_2X2, color = BlockColor.CORAL)
        assertFalse(GameEngine.canPieceFitAnywhere(board, squarePiece))

        // A 1x1 dot CAN fit at (0, 0)
        val dotPiece = Piece(shape = BlockShape.DOT, color = BlockColor.GOLD)
        assertTrue(GameEngine.canPieceFitAnywhere(board, dotPiece))

        // With only the square piece available, isGameOver should be true
        val updatedPieces = GameEngine.updatePiecesFitStatus(board, listOf(squarePiece))
        assertFalse(updatedPieces[0].canFit)

        val result = GameEngine.executePlacement(
            board = board,
            pieces = listOf(dotPiece, squarePiece),
            pieceIndex = 0, // place dot
            startRow = 0,
            startCol = 0,
            currentStreak = 0
        )

        // Note: placing dot at (0, 0) completes row 0 and col 0, opening up space!
        assertTrue(result.clearedRows.contains(0))
        assertTrue(result.clearedCols.contains(0))
        // So square can fit after the lines clear!
        assertFalse(result.isGameOver)
    }

    @Test
    fun testBlockGeneratorHandSizeAndVariety() {
        val generator = BlockGenerator(Random(12345))
        for (i in 0 until 20) {
            val hand = generator.generateHand()
            assertEquals(3, hand.size)
            assertTrue(hand.all { !it.isUsed })
            assertTrue(hand.all { it.shape.cellCount > 0 })
            // Ensure not all 3 pieces are large 3x3s
            val largeCount = hand.count { it.shape.category == com.newage.blockmania.model.ShapeCategory.LARGE }
            assertTrue("Hand should have at most 1 large shape", largeCount <= 1)
        }
    }
}
