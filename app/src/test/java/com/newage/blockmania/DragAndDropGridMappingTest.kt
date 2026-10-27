package com.newage.blockmania

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.newage.blockmania.engine.GameEngine
import com.newage.blockmania.model.BOARD_SIZE
import com.newage.blockmania.model.BlockColor
import com.newage.blockmania.model.BlockShape
import com.newage.blockmania.model.Board
import com.newage.blockmania.model.DragState
import com.newage.blockmania.model.Piece
import com.newage.blockmania.ui.components.PieceLayoutHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.roundToInt

class DragAndDropGridMappingTest {

    private val testBoardBounds = Rect(left = 50f, top = 200f, right = 850f, bottom = 1000f)
    private val testCellSize = 100f // 800 / 8

    /**
     * Helper delegating to DragSensitivityHelper.calculatePlacement
     */
    private fun calculateDragState(
        board: Board,
        boardBounds: Rect,
        boardCellSize: Float,
        pieceIndex: Int,
        piece: Piece,
        touchX: Float,
        touchY: Float,
        touchOffsetX: Float,
        touchOffsetY: Float
    ): DragState {
        val result = com.newage.blockmania.engine.DragSensitivityHelper.calculatePlacement(
            board = board,
            boardBounds = boardBounds,
            boardCellSize = boardCellSize,
            piece = piece,
            touchX = touchX,
            touchY = touchY,
            touchOffsetX = touchOffsetX,
            touchOffsetY = touchOffsetY
        )

        return DragState(
            pieceIndex = pieceIndex,
            piece = piece,
            touchX = touchX,
            touchY = touchY,
            touchOffsetX = touchOffsetX,
            touchOffsetY = touchOffsetY,
            targetRow = result.targetRow,
            targetCol = result.targetCol,
            isValid = result.isValid,
            potentialClearedRows = result.potentialClearedRows,
            potentialClearedCols = result.potentialClearedCols,
            isOverBoard = result.isOverBoard
        )
    }

    @Test
    fun testDragFromCenterOfPieceAccuratelyMapsToGrid() {
        val piece = Piece(shape = BlockShape.SQUARE_2X2, color = BlockColor.GOLD)
        // Center of a 2x2 piece at boardCellSize (200x200): offset = (100f, 100f)
        val touchOffsetX = 100f
        val touchOffsetY = 100f

        // Player drags the center of the piece to the center of board cell (row 3, col 4)
        // Center of cell (3, 4) in root coordinates:
        // X: boardBounds.left + 4 * cellSize + 50f (center of cell 4)
        // Y: boardBounds.top + 3 * cellSize + 50f (center of cell 3)
        // But for a 2x2 piece with center touch (100f, 100f), if the center of the 2x2 piece is over the intersection
        // between row 3 & 4 and col 4 & 5:
        val touchX = testBoardBounds.left + 4 * testCellSize + 100f // 50 + 400 + 100 = 550f
        val touchY = testBoardBounds.top + 3 * testCellSize + 100f // 200 + 300 + 100 = 600f

        val dragState = calculateDragState(
            board = Board.EMPTY,
            boardBounds = testBoardBounds,
            boardCellSize = testCellSize,
            pieceIndex = 0,
            piece = piece,
            touchX = touchX,
            touchY = touchY,
            touchOffsetX = touchOffsetX,
            touchOffsetY = touchOffsetY
        )

        assertTrue("Drag state should be valid", dragState.isValid)
        assertEquals("Target row should be 3", 3, dragState.targetRow)
        assertEquals("Target col should be 4", 4, dragState.targetCol)

        // When dropped and executed, blocks land on (3,4), (3,5), (4,4), (4,5)
        val result = GameEngine.executePlacement(
            board = Board.EMPTY,
            pieces = listOf(piece),
            pieceIndex = 0,
            startRow = dragState.targetRow!!,
            startCol = dragState.targetCol!!,
            currentStreak = 0
        )
        assertTrue(result.success)
        assertEquals(BlockColor.GOLD, result.newBoard[3, 4])
        assertEquals(BlockColor.GOLD, result.newBoard[3, 5])
        assertEquals(BlockColor.GOLD, result.newBoard[4, 4])
        assertEquals(BlockColor.GOLD, result.newBoard[4, 5])
    }

    @Test
    fun testDragFromEdgesAndCornersOfPiece() {
        // Test L-Shape (3 rows x 2 cols):
        // [1, 0]
        // [1, 0]
        // [1, 1]
        val piece = Piece(shape = BlockShape.L_3X2_BL, color = BlockColor.CYAN)

        // Scenario A: Player touched the TOP-LEFT block (r=0, c=0):
        // Cell fraction is (0.5, 0.5) inside block (0, 0)
        val touchOffsetTopLeft = Offset(0.5f * testCellSize, 0.5f * testCellSize) // (50f, 50f)

        // Player moves finger to center of cell (2, 5)
        val fingerAtCell2_5 = Offset(
            testBoardBounds.left + 5 * testCellSize + 50f,
            testBoardBounds.top + 2 * testCellSize + 50f
        )

        val stateA = calculateDragState(
            board = Board.EMPTY,
            boardBounds = testBoardBounds,
            boardCellSize = testCellSize,
            pieceIndex = 0,
            piece = piece,
            touchX = fingerAtCell2_5.x,
            touchY = fingerAtCell2_5.y,
            touchOffsetX = touchOffsetTopLeft.x,
            touchOffsetY = touchOffsetTopLeft.y
        )
        assertEquals(2, stateA.targetRow)
        assertEquals(5, stateA.targetCol)
        // Top-left block lands on cell (2, 5) directly under the finger!

        // Scenario B: Player touched the BOTTOM-RIGHT block (r=2, c=1):
        // Cell fraction is (1.5, 2.5) -> offset = (150f, 250f)
        val touchOffsetBottomRight = Offset(1.5f * testCellSize, 2.5f * testCellSize)

        // Player moves finger to cell (7, 6)
        val fingerAtCell7_6 = Offset(
            testBoardBounds.left + 6 * testCellSize + 50f,
            testBoardBounds.top + 7 * testCellSize + 50f
        )

        val stateB = calculateDragState(
            board = Board.EMPTY,
            boardBounds = testBoardBounds,
            boardCellSize = testCellSize,
            pieceIndex = 0,
            piece = piece,
            touchX = fingerAtCell7_6.x,
            touchY = fingerAtCell7_6.y,
            touchOffsetX = touchOffsetBottomRight.x,
            touchOffsetY = touchOffsetBottomRight.y
        )
        // Target col should be 6 - 1 = 5, target row should be 7 - 2 = 5
        assertEquals(5, stateB.targetRow)
        assertEquals(5, stateB.targetCol)
        // And block (2, 1) of the L lands on (5 + 2 = 7, 5 + 1 = 6) -> cell (7, 6)!
        // Exactly where the finger is!
    }

    @Test
    fun testFourCornersPlacement() {
        val board = Board.EMPTY
        val piece2x2 = Piece(shape = BlockShape.SQUARE_2X2, color = BlockColor.EMERALD)
        val touchOffset = Offset(50f, 50f) // touching top-left block

        // 1. Top-Left Corner: cell (0, 0)
        val fingerTopLeft = Offset(testBoardBounds.left + 50f, testBoardBounds.top + 50f)
        val stateTL = calculateDragState(
            board, testBoardBounds, testCellSize, 0, piece2x2,
            fingerTopLeft.x, fingerTopLeft.y, touchOffset.x, touchOffset.y
        )
        assertTrue("Top-left placement should be valid", stateTL.isValid)
        assertEquals(0, stateTL.targetRow)
        assertEquals(0, stateTL.targetCol)

        // 2. Top-Right Corner: targetCol should be 6 for 2-wide piece (occupies 6, 7 on 8x8)
        val fingerTopRight = Offset(testBoardBounds.left + 6 * testCellSize + 50f, testBoardBounds.top + 50f)
        val stateTR = calculateDragState(
            board, testBoardBounds, testCellSize, 0, piece2x2,
            fingerTopRight.x, fingerTopRight.y, touchOffset.x, touchOffset.y
        )
        assertTrue("Top-right placement should be valid", stateTR.isValid)
        assertEquals(0, stateTR.targetRow)
        assertEquals(6, stateTR.targetCol)

        // 3. Bottom-Left Corner: targetRow should be 6 for 2-high piece (occupies 6, 7 on 8x8)
        val fingerBottomLeft = Offset(testBoardBounds.left + 50f, testBoardBounds.top + 6 * testCellSize + 50f)
        val stateBL = calculateDragState(
            board, testBoardBounds, testCellSize, 0, piece2x2,
            fingerBottomLeft.x, fingerBottomLeft.y, touchOffset.x, touchOffset.y
        )
        assertTrue("Bottom-left placement should be valid", stateBL.isValid)
        assertEquals(6, stateBL.targetRow)
        assertEquals(0, stateBL.targetCol)

        // 4. Bottom-Right Corner: targetRow 6, targetCol 6
        val fingerBottomRight = Offset(testBoardBounds.left + 6 * testCellSize + 50f, testBoardBounds.top + 6 * testCellSize + 50f)
        val stateBR = calculateDragState(
            board, testBoardBounds, testCellSize, 0, piece2x2,
            fingerBottomRight.x, fingerBottomRight.y, touchOffset.x, touchOffset.y
        )
        assertTrue("Bottom-right placement should be valid", stateBR.isValid)
        assertEquals(6, stateBR.targetRow)
        assertEquals(6, stateBR.targetCol)
    }

    @Test
    fun testSmartSnapToNearestValidNeighborWhenBlocked() {
        // Block cell (2, 2)
        val board = Board.EMPTY.withCell(2, 2, BlockColor.PURPLE)
        val piece1x1 = Piece(shape = BlockShape.DOT, color = BlockColor.GOLD)
        val touchOffset = Offset(50f, 50f)

        // Player hovers at (row 2.1, col 2.1) - would round to blocked (2, 2)
        val finger = Offset(
            testBoardBounds.left + 2.1f * testCellSize + touchOffset.x,
            testBoardBounds.top + 2.1f * testCellSize + touchOffset.y
        )

        val state = calculateDragState(
            board, testBoardBounds, testCellSize, 0, piece1x1,
            finger.x, finger.y, touchOffset.x, touchOffset.y
        )

        // Should magnetically snap to closest valid neighbor within tolerance, e.g. (2, 3) or (2, 1)
        assertTrue("Smart snap should find a valid neighbor", state.isValid)
        assertTrue("Target row should be near 2", state.targetRow in 1..3)
        assertTrue("Target col should be near 2", state.targetCol in 1..3)
        // Ensure it did NOT place on the occupied cell (2, 2)
        assertFalse(state.targetRow == 2 && state.targetCol == 2)
    }

    @Test
    fun testEdgeClampingNeverOverflowsBoard() {
        val piece3x3 = Piece(shape = BlockShape.SQUARE_3X3, color = BlockColor.PURPLE)
        val touchOffset = Offset(50f, 50f)

        // 1. Drag past right edge on empty 8x8 board: should clamp to maxCol = 5 (occupies cols 5, 6, 7)
        val fingerPastRight = Offset(testBoardBounds.left + 5.6f * testCellSize + touchOffset.x, testBoardBounds.top + touchOffset.y)
        val stateRight = calculateDragState(
            Board.EMPTY, testBoardBounds, testCellSize, 0, piece3x3,
            fingerPastRight.x, fingerPastRight.y, touchOffset.x, touchOffset.y
        )
        assertTrue("Right edge should clamp and be valid on empty board", stateRight.isValid)
        assertEquals(5, stateRight.targetCol)
        assertEquals(0, stateRight.targetRow)

        // 2. Drag past bottom edge: should clamp to maxRow = 5 (occupies rows 5, 6, 7)
        val fingerPastBottom = Offset(testBoardBounds.left + touchOffset.x, testBoardBounds.top + 5.6f * testCellSize + touchOffset.y)
        val stateBottom = calculateDragState(
            Board.EMPTY, testBoardBounds, testCellSize, 0, piece3x3,
            fingerPastBottom.x, fingerPastBottom.y, touchOffset.x, touchOffset.y
        )
        assertTrue("Bottom edge should clamp and be valid on empty board", stateBottom.isValid)
        assertEquals(5, stateBottom.targetRow)
        assertEquals(0, stateBottom.targetCol)
    }

    @Test
    fun testInvalidDropsAndOccupiedCells() {
        val piece3x3 = Piece(shape = BlockShape.SQUARE_3X3, color = BlockColor.PURPLE)
        val touchOffset = Offset(50f, 50f)

        // 1. Far off top/left (outside board interaction margin)
        val fingerPastTop = Offset(testBoardBounds.left - 200f, testBoardBounds.top - 200f)
        val stateTop = calculateDragState(
            Board.EMPTY, testBoardBounds, testCellSize, 0, piece3x3,
            fingerPastTop.x, fingerPastTop.y, touchOffset.x, touchOffset.y
        )
        assertFalse("Far off-board coordinate must be invalid", stateTop.isValid)
        assertNull(stateTop.targetRow)
        assertNull(stateTop.targetCol)
        assertFalse(stateTop.isOverBoard)

        // 2. Overlap with occupied cells where piece cannot fit
        var occupiedBoard = Board.EMPTY
        // Fill a 3x3 block from (0,0) to (2,2) with obstacles except (0,0)
        occupiedBoard = occupiedBoard.withCell(1, 1, BlockColor.CORAL)
        occupiedBoard = occupiedBoard.withCell(0, 1, BlockColor.CORAL)
        occupiedBoard = occupiedBoard.withCell(1, 0, BlockColor.CORAL)

        val fingerOnOccupied = Offset(testBoardBounds.left + 50f, testBoardBounds.top + 50f) // at (0, 0)
        val stateCollision = calculateDragState(
            occupiedBoard, testBoardBounds, testCellSize, 0, piece3x3,
            fingerOnOccupied.x, fingerOnOccupied.y, touchOffset.x, touchOffset.y
        )
        assertFalse("Collision with occupied cell must be invalid", stateCollision.isValid)
        // Invalid target row and col are preserved for UI warning preview
        assertEquals(0, stateCollision.targetRow)
        assertEquals(0, stateCollision.targetCol)

        // Dropping on collision should NOT place anything or modify board
        val result = GameEngine.executePlacement(
            board = occupiedBoard,
            pieces = listOf(piece3x3),
            pieceIndex = 0,
            startRow = stateCollision.targetRow!!,
            startCol = stateCollision.targetCol!!,
            currentStreak = 0
        )
        assertFalse(result.success)
        assertEquals(occupiedBoard, result.newBoard)
    }

    @Test
    fun testPieceLayoutHelperSlotMetrics() {
        // Test standard phone tray slot (width: 120px, height: 110px)
        val metrics1 = PieceLayoutHelper.calculateSlotMetrics(
            availableWidth = 120f,
            availableHeight = 110f,
            rowCount = 3,
            colCount = 3
        )
        assertTrue("miniCellSize should be positive", metrics1.miniCellSize > 0f)
        assertTrue("totalWidth must fit in availableWidth", metrics1.totalWidth <= 120f)
        assertTrue("totalHeight must fit in availableHeight", metrics1.totalHeight <= 110f)
        assertTrue("startX should be non-negative", metrics1.startX >= 0f)
        assertTrue("startY should be non-negative", metrics1.startY >= 0f)

        // Test 1x1 dot
        val metricsDot = PieceLayoutHelper.calculateSlotMetrics(
            availableWidth = 120f,
            availableHeight = 110f,
            rowCount = 1,
            colCount = 1
        )
        assertTrue("Dot totalWidth must fit in slot", metricsDot.totalWidth <= 120f)
        assertEquals(metricsDot.totalWidth, metricsDot.totalHeight, 0.001f)
    }

    @Test
    fun testCoordinateConversionAcrossScreenSizes() {
        val testScreens = listOf(
            Pair(Rect(20f, 100f, 340f, 420f), 32f),       // Small phone
            Pair(Rect(24f, 160f, 384f, 520f), 36f),       // Medium phone
            Pair(Rect(40f, 220f, 520f, 700f), 48f),       // Large phone
            Pair(Rect(150f, 150f, 650f, 650f), 50f)       // Tablet
        )

        val piece = Piece(shape = BlockShape.LINE_2_H, color = BlockColor.CYAN)

        for ((bounds, cellSize) in testScreens) {
            // Player drags to cell (5, 6)
            val touchOffset = Offset(cellSize * 0.5f, cellSize * 0.5f)
            val pointer = Offset(
                bounds.left + 6 * cellSize + cellSize * 0.5f,
                bounds.top + 5 * cellSize + cellSize * 0.5f
            )

            val state = calculateDragState(
                Board.EMPTY, bounds, cellSize, 0, piece,
                pointer.x, pointer.y, touchOffset.x, touchOffset.y
            )

            assertTrue("Should be valid on bounds $bounds with cellSize $cellSize", state.isValid)
            assertEquals("Row should be 5 on $bounds", 5, state.targetRow)
            assertEquals("Col should be 6 on $bounds", 6, state.targetCol)
        }
    }

    @Test
    fun testDeadCenterBlockedPieceDoesNotJumpUnpredictably() {
        // When hovering dead-center over an occupied block, the piece MUST NOT jump to a neighbor.
        // It must remain at the intended target with isValid = false so invalid feedback is displayed in-place.
        val board = Board.EMPTY.withCell(3, 3, BlockColor.PURPLE)
        val piece1x1 = Piece(shape = BlockShape.DOT, color = BlockColor.GOLD)
        val touchOffset = Offset(50f, 50f)

        // Exact center of cell (3, 3)
        val fingerExactCenter = Offset(
            testBoardBounds.left + 3.0f * testCellSize + touchOffset.x,
            testBoardBounds.top + 3.0f * testCellSize + touchOffset.y
        )

        val state = calculateDragState(
            board, testBoardBounds, testCellSize, 0, piece1x1,
            fingerExactCenter.x, fingerExactCenter.y, touchOffset.x, touchOffset.y
        )

        // Must retain target cell (3, 3) and be marked invalid!
        assertFalse("Dead center on blocked cell must be invalid", state.isValid)
        assertEquals("Target row must remain 3 without jumping away", 3, state.targetRow)
        assertEquals("Target col must remain 3 without jumping away", 3, state.targetCol)
        assertTrue("Must be identified as over board", state.isOverBoard)
    }

    @Test
    fun testAuthoritativePreviewMatchesDropResult100Percent() {
        // Ghost preview validation and final placement execution must share identical logic
        val piece = Piece(shape = BlockShape.T_DOWN, color = BlockColor.CORAL)
        val touchOffset = Offset(1.5f * testCellSize, 1.0f * testCellSize)

        for (r in 0..6) {
            for (c in 0..5) {
                val finger = Offset(
                    testBoardBounds.left + c * testCellSize + touchOffset.x,
                    testBoardBounds.top + r * testCellSize + touchOffset.y
                )

                val state = calculateDragState(
                    Board.EMPTY, testBoardBounds, testCellSize, 0, piece,
                    finger.x, finger.y, touchOffset.x, touchOffset.y
                )

                assertEquals(r, state.targetRow)
                assertEquals(c, state.targetCol)
                assertTrue(state.isValid)

                // Execute placement at exactly the previewed targetRow and targetCol
                val result = GameEngine.executePlacement(
                    board = Board.EMPTY,
                    pieces = listOf(piece),
                    pieceIndex = 0,
                    startRow = state.targetRow!!,
                    startCol = state.targetCol!!,
                    currentStreak = 0
                )

                assertTrue("Placement execution must succeed at valid preview coordinates", result.success)
            }
        }
    }
}
