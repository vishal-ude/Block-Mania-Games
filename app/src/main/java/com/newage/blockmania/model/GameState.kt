package com.newage.blockmania.model

import com.newage.blockmania.theme.GameTheme

enum class ScreenState {
    START,
    PLAYING,
    PAUSED,
    GAME_OVER
}

data class LineClearEffect(
    val rows: Set<Int>,
    val cols: Set<Int>,
    val timestamp: Long = System.currentTimeMillis()
)

data class ComboPopup(
    val comboCount: Int,
    val bonusPoints: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class PlacementAnimation(
    val cells: List<Pair<Int, Int>>,
    val timestamp: Long = System.currentTimeMillis()
)

data class DragState(
    val pieceIndex: Int,
    val piece: Piece,
    val touchX: Float = 0f,
    val touchY: Float = 0f,
    val touchOffsetX: Float = 0f,
    val touchOffsetY: Float = 0f,
    val targetRow: Int? = null,
    val targetCol: Int? = null,
    val isValid: Boolean = false,
    val potentialClearedRows: Set<Int> = emptySet(),
    val potentialClearedCols: Set<Int> = emptySet(),
    val isOverBoard: Boolean = false
)

/**
 * Coarse-grained grid placement preview state.
 * Emits only when targetRow, targetCol, or isValid changes,
 * preventing continuous full-screen recomposition during dragging.
 */
data class DragPreviewState(
    val piece: Piece,
    val targetRow: Int? = null,
    val targetCol: Int? = null,
    val isValid: Boolean = false,
    val potentialClearedRows: Set<Int> = emptySet(),
    val potentialClearedCols: Set<Int> = emptySet(),
    val isOverBoard: Boolean = false
)

data class GameState(
    val screenState: ScreenState = ScreenState.START,
    val board: Board = Board.EMPTY,
    val currentPieces: List<Piece> = emptyList(),
    val score: Int = 0,
    val bestScore: Int = 0,
    val comboStreak: Int = 0,
    val isNewHighScore: Boolean = false,
    val activeLineClear: LineClearEffect? = null,
    val activeComboPopup: ComboPopup? = null,
    val isSoundEnabled: Boolean = true,
    val selectedTheme: GameTheme = GameTheme.GALAXY,
    val canUndo: Boolean = false,
    val lastPlacement: PlacementAnimation? = null,
    val showThemeDialog: Boolean = false,
    val showCompetitiveHub: Boolean = false,
    val sessionLinesCleared: Int = 0,
    val sessionMaxCombo: Int = 0,
    val newlyUnlockedAchievements: List<Achievement> = emptyList(),
    val canRevive: Boolean = false,
    val hasSavedGame: Boolean = false
) {
    val isGameOver: Boolean get() = screenState == ScreenState.GAME_OVER
}
