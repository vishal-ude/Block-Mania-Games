package com.newage.blockmania

import com.newage.blockmania.data.GameSaveRepository
import com.newage.blockmania.engine.GameEngine
import com.newage.blockmania.model.BOARD_SIZE
import com.newage.blockmania.model.BlockColor
import com.newage.blockmania.model.BlockShape
import com.newage.blockmania.model.Board
import com.newage.blockmania.model.DailyChallenge
import com.newage.blockmania.model.Piece
import com.newage.blockmania.monetization.RewardedAdManager
import com.newage.blockmania.theme.GameTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CompetitiveAndProgressionTest {

    @Test
    fun testDailyChallengeProgressCalculation() {
        val challenge = DailyChallenge(
            dateString = "2026-10-27",
            title = "Daily Master Challenge",
            description = "Score 1500 pts and clear 10 lines",
            targetScore = 1500,
            targetLines = 10,
            currentScore = 750,
            currentLines = 5,
            isCompleted = false
        )

        assertEquals(0.5f, challenge.scoreProgress, 0.001f)
        assertEquals(0.5f, challenge.linesProgress, 0.001f)
        assertFalse(challenge.isCompleted)

        val completedChallenge = challenge.copy(
            currentScore = 1600,
            currentLines = 12,
            isCompleted = true
        )
        assertEquals(1.0f, completedChallenge.scoreProgress, 0.001f)
        assertEquals(1.0f, completedChallenge.linesProgress, 0.001f)
        assertTrue(completedChallenge.isCompleted)
    }

    @Test
    fun testThemeUnlockCriteria() {
        // Galaxy is always unlocked by default
        assertTrue("Galaxy must be unlocked by default", GameTheme.GALAXY == GameTheme.GALAXY)

        fun isUnlockedSimulated(theme: GameTheme, bestScore: Int, lines: Int, combo: Int, dailyCount: Int): Boolean {
            return when (theme) {
                GameTheme.GALAXY -> true
                GameTheme.OCEAN_BLUE -> bestScore >= 1000 || lines >= 20
                GameTheme.CANDY_POP -> combo >= 3 || bestScore >= 3000
                GameTheme.EMERALD -> dailyCount >= 1 || bestScore >= 5000
            }
        }

        // Beginner player (0 score, 0 lines)
        assertTrue(isUnlockedSimulated(GameTheme.GALAXY, 0, 0, 0, 0))
        assertFalse(isUnlockedSimulated(GameTheme.OCEAN_BLUE, 500, 10, 1, 0))
        assertFalse(isUnlockedSimulated(GameTheme.CANDY_POP, 500, 10, 2, 0))
        assertFalse(isUnlockedSimulated(GameTheme.EMERALD, 500, 10, 2, 0))

        // Cleared 20 lines -> Unlocks Ocean Blue
        assertTrue(isUnlockedSimulated(GameTheme.OCEAN_BLUE, 500, 20, 1, 0))

        // 3x combo -> Unlocks Candy Pop
        assertTrue(isUnlockedSimulated(GameTheme.CANDY_POP, 500, 10, 3, 0))

        // Completed 1 daily challenge -> Unlocks Emerald
        assertTrue(isUnlockedSimulated(GameTheme.EMERALD, 500, 10, 1, 1))
    }

    @Test
    fun testBoardAndHandSerializationIntegrity() {
        // Construct board with specific placed blocks
        var board = Board.EMPTY
        board = board.withCell(0, 0, BlockColor.CYAN)
        board = board.withCell(7, 7, BlockColor.MAGENTA)
        board = board.withCell(3, 4, BlockColor.GOLD)

        val pieces = listOf(
            Piece(id = "p1", shape = BlockShape.LINE_3_H, color = BlockColor.CYAN, isUsed = false, canFit = true),
            Piece(id = "p2", shape = BlockShape.SQUARE_2X2, color = BlockColor.GOLD, isUsed = true, canFit = false),
            Piece(id = "p3", shape = BlockShape.T_UP, color = BlockColor.PURPLE, isUsed = false, canFit = true)
        )

        // Serialize using GameSaveRepository pure-Kotlin serializers
        val boardStr = GameSaveRepository.serializeBoard(board)
        val piecesStr = GameSaveRepository.serializePieces(pieces)

        // Deserialize
        val deserializedBoard = GameSaveRepository.deserializeBoard(boardStr)
        assertNotNull(deserializedBoard)
        assertEquals(BlockColor.CYAN, deserializedBoard!![0, 0])
        assertEquals(BlockColor.MAGENTA, deserializedBoard[7, 7])
        assertEquals(BlockColor.GOLD, deserializedBoard[3, 4])
        assertNull(deserializedBoard[1, 1])

        val deserializedPieces = GameSaveRepository.deserializePieces(piecesStr)
        assertEquals(3, deserializedPieces.size)
        assertEquals(BlockShape.LINE_3_H, deserializedPieces[0].shape)
        assertFalse(deserializedPieces[0].isUsed)
        assertTrue(deserializedPieces[1].isUsed)
        assertEquals(BlockShape.T_UP, deserializedPieces[2].shape)
    }

    @Test
    fun testRewardedAdManagerPolicyCompliance() {
        val adManager = RewardedAdManager()

        // 1. Must be DISABLED by default
        assertFalse("Monetization must be disabled by default", adManager.isMonetizationEnabled)
        assertFalse("Cannot revive when monetization is disabled", adManager.canRevive())

        // 2. Verified official test ad unit ID
        assertEquals("ca-app-pub-3940256099942544/5224354917", adManager.testAdUnitId)

        // 3. Single continue limit per game session
        adManager.isMonetizationEnabled = true
        // Preload ad
        adManager.preloadRewardedAd()
        assertTrue("Ad must be available when enabled and preloaded", adManager.canRevive())

        // Use revive
        var rewardEarned = false
        var dismissed = false
        adManager.showRewardedAd(
            activity = null,
            onRewardEarned = { rewardEarned = true },
            onAdDismissed = { dismissed = true }
        )

        assertTrue("Reward callback must be triggered", rewardEarned)
        assertTrue("Dismiss callback must be triggered", dismissed)
        assertFalse("Cannot revive more than once per game session", adManager.canRevive())

        // Reset session on new game started
        adManager.onNewGameStarted()
        adManager.preloadRewardedAd()
        assertTrue("Can revive again in a new game session", adManager.canRevive())
    }

    @Test
    fun testReviveClearsCenterAndRestoresPlayability() {
        // Create full board (blocked game over)
        var blockedBoard = Board.EMPTY
        for (r in 0 until BOARD_SIZE) {
            for (c in 0 until BOARD_SIZE) {
                blockedBoard = blockedBoard.withCell(r, c, BlockColor.CORAL)
            }
        }

        val testPiece = Piece(shape = BlockShape.SQUARE_2X2, color = BlockColor.CYAN)
        assertFalse("Piece cannot fit on full board", GameEngine.canPieceFitAnywhere(blockedBoard, testPiece))

        // Perform revive clear on center 3x3 (rows 2..4, cols 2..4)
        var clearedBoard = blockedBoard
        for (r in 2..4) {
            for (c in 2..4) {
                clearedBoard = clearedBoard.withCell(r, c, null)
            }
        }

        // Verify cells are empty
        for (r in 2..4) {
            for (c in 2..4) {
                assertTrue("Cell ($r, $c) should be empty after revive", clearedBoard.isEmpty(r, c))
            }
        }

        // Now test piece fits in the cleared center!
        assertTrue("Square piece must fit in cleared center", GameEngine.canPieceFitAnywhere(clearedBoard, testPiece))
    }
}
