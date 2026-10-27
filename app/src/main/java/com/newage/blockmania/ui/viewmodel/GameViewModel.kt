package com.newage.blockmania.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.newage.blockmania.data.CompetitiveRepository
import com.newage.blockmania.data.GameSaveRepository
import com.newage.blockmania.data.ScoreRepository
import com.newage.blockmania.engine.BlockGenerator
import com.newage.blockmania.engine.DragSensitivityHelper
import com.newage.blockmania.engine.GameEngine
import com.newage.blockmania.engine.SoundManager
import com.newage.blockmania.model.Achievement
import com.newage.blockmania.model.Board
import com.newage.blockmania.model.ComboPopup
import com.newage.blockmania.model.DailyChallenge
import com.newage.blockmania.model.DragPreviewState
import com.newage.blockmania.model.DragState
import com.newage.blockmania.model.GameState
import com.newage.blockmania.model.LineClearEffect
import com.newage.blockmania.model.Mission
import com.newage.blockmania.model.Piece
import com.newage.blockmania.model.PlacementAnimation
import com.newage.blockmania.model.PlayerStats
import com.newage.blockmania.model.ScreenState
import com.newage.blockmania.monetization.RewardedAdManager
import com.newage.blockmania.theme.GameTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class MoveHistory(
    val board: Board,
    val currentPieces: List<Piece>,
    val score: Int,
    val comboStreak: Int
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val scoreRepository = ScoreRepository(application)
    private val competitiveRepository = CompetitiveRepository(application)
    private val gameSaveRepository = GameSaveRepository(application)
    val rewardedAdManager = RewardedAdManager()
    private val soundManager = SoundManager(application)
    private val blockGenerator = BlockGenerator()

    private var previousMove: MoveHistory? = null
    private var isPlacementInProgress = false

    private val _gameState = MutableStateFlow(
        GameState(
            bestScore = scoreRepository.getBestScore(),
            isSoundEnabled = scoreRepository.isSoundEnabled(),
            selectedTheme = scoreRepository.getSelectedTheme(),
            hasSavedGame = gameSaveRepository.hasSavedGame()
        )
    )
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _dragState = MutableStateFlow<DragState?>(null)
    val dragState: StateFlow<DragState?> = _dragState.asStateFlow()

    // Decoupled preview state: emits ONLY when target grid cell or validity changes
    private val _dragPreviewState = MutableStateFlow<DragPreviewState?>(null)
    val dragPreviewState: StateFlow<DragPreviewState?> = _dragPreviewState.asStateFlow()

    // Active drag index: emits ONLY on drag start and drag end
    private val _activeDragPieceIndex = MutableStateFlow<Int?>(null)
    val activeDragPieceIndex: StateFlow<Int?> = _activeDragPieceIndex.asStateFlow()

    // Continuous touch coordinates read in Draw Phase to avoid recomposing the entire screen
    var currentTouchPosition by mutableStateOf(Offset.Zero)
        private set
    var currentTouchOffset by mutableStateOf(Offset.Zero)
        private set
    var currentDraggedPiece by mutableStateOf<Piece?>(null)
        private set

    var boardBounds by mutableStateOf(Rect.Zero)
        private set
    var boardCellSize by mutableStateOf(0f)
        private set

    init {
        soundManager.isEnabled = _gameState.value.isSoundEnabled
    }

    fun startGame(resumeIfActive: Boolean = true) {
        val current = _gameState.value
        val hasActiveInMemory = current.screenState != ScreenState.GAME_OVER &&
                current.currentPieces.isNotEmpty() &&
                current.currentPieces.any { !it.isUsed && it.canFit }

        if (resumeIfActive && hasActiveInMemory) {
            _gameState.update { it.copy(screenState = ScreenState.PLAYING) }
            return
        }

        // Try restoring from persistent local storage if requested
        if (resumeIfActive && gameSaveRepository.hasSavedGame()) {
            val savedSession = gameSaveRepository.loadGame()
            if (savedSession != null) {
                val fittedPieces = GameEngine.updatePiecesFitStatus(savedSession.board, savedSession.pieces)
                val hasPlayablePiece = fittedPieces.any { !it.isUsed && it.canFit }
                if (hasPlayablePiece) {
                    previousMove = null
                    _gameState.update {
                        it.copy(
                            screenState = ScreenState.PLAYING,
                            board = savedSession.board,
                            currentPieces = fittedPieces,
                            score = savedSession.score,
                            comboStreak = savedSession.comboStreak,
                            canUndo = savedSession.canUndo,
                            isNewHighScore = false,
                            activeLineClear = null,
                            activeComboPopup = null,
                            lastPlacement = null,
                            hasSavedGame = true
                        )
                    }
                    return
                } else {
                    gameSaveRepository.clearSavedGame()
                }
            }
        }

        // Start fresh match
        previousMove = null
        rewardedAdManager.onNewGameStarted()
        gameSaveRepository.clearSavedGame()

        val initialHand = blockGenerator.generateHand()
        val fittedPieces = GameEngine.updatePiecesFitStatus(Board.EMPTY, initialHand)
        _gameState.update {
            it.copy(
                screenState = ScreenState.PLAYING,
                board = Board.EMPTY,
                currentPieces = fittedPieces,
                score = 0,
                comboStreak = 0,
                sessionLinesCleared = 0,
                sessionMaxCombo = 0,
                newlyUnlockedAchievements = emptyList(),
                isNewHighScore = false,
                activeLineClear = null,
                activeComboPopup = null,
                lastPlacement = null,
                canUndo = false,
                canRevive = false,
                hasSavedGame = false
            )
        }
    }

    fun restartGame() {
        previousMove = null
        rewardedAdManager.onNewGameStarted()
        gameSaveRepository.clearSavedGame()

        val newHand = blockGenerator.generateHand()
        val fittedPieces = GameEngine.updatePiecesFitStatus(Board.EMPTY, newHand)
        _gameState.update {
            it.copy(
                screenState = ScreenState.PLAYING,
                board = Board.EMPTY,
                currentPieces = fittedPieces,
                score = 0,
                comboStreak = 0,
                sessionLinesCleared = 0,
                sessionMaxCombo = 0,
                newlyUnlockedAchievements = emptyList(),
                isNewHighScore = false,
                activeLineClear = null,
                activeComboPopup = null,
                lastPlacement = null,
                canUndo = false,
                canRevive = false,
                hasSavedGame = false
            )
        }
    }

    fun undo() {
        val prev = previousMove ?: return
        previousMove = null
        val fittedPieces = GameEngine.updatePiecesFitStatus(prev.board, prev.currentPieces)
        soundManager.playPickUp()

        // Persist undo state
        gameSaveRepository.saveGame(
            board = prev.board,
            pieces = fittedPieces,
            score = prev.score,
            comboStreak = prev.comboStreak,
            canUndo = false
        )

        _gameState.update {
            it.copy(
                board = prev.board,
                currentPieces = fittedPieces,
                score = prev.score,
                comboStreak = prev.comboStreak,
                canUndo = false,
                activeLineClear = null,
                activeComboPopup = null,
                lastPlacement = null,
                screenState = ScreenState.PLAYING,
                hasSavedGame = true
            )
        }
    }

    /**
     * Optional Rewarded Continue (Second Chance).
     * Clears the central 4x4 board section (rows 2..5, cols 2..5) and deals 3 fresh playable pieces.
     */
    fun reviveGame() {
        rewardedAdManager.consumeRevive()
        val currentBoard = _gameState.value.board
        var clearedBoard = currentBoard
        for (r in 2..5) {
            for (c in 2..5) {
                clearedBoard = clearedBoard.withCell(r, c, null)
            }
        }

        val freshHand = blockGenerator.generateHand()
        val fittedPieces = GameEngine.updatePiecesFitStatus(clearedBoard, freshHand)

        soundManager.playPickUp()
        gameSaveRepository.saveGame(
            board = clearedBoard,
            pieces = fittedPieces,
            score = _gameState.value.score,
            comboStreak = 0,
            canUndo = false
        )

        _gameState.update {
            it.copy(
                screenState = ScreenState.PLAYING,
                board = clearedBoard,
                currentPieces = fittedPieces,
                comboStreak = 0,
                canUndo = false,
                canRevive = false,
                hasSavedGame = true
            )
        }
    }

    fun setTheme(theme: GameTheme) {
        if (!isThemeUnlocked(theme)) return
        scoreRepository.saveSelectedTheme(theme)
        _gameState.update { it.copy(selectedTheme = theme, showThemeDialog = false) }
    }

    fun openThemeDialog() {
        soundManager.playButtonClick()
        _gameState.update { it.copy(showThemeDialog = true) }
    }

    fun closeThemeDialog() {
        soundManager.playButtonClick()
        _gameState.update { it.copy(showThemeDialog = false) }
    }

    fun openCompetitiveHub() {
        soundManager.playButtonClick()
        _gameState.update { it.copy(showCompetitiveHub = true) }
    }

    fun closeCompetitiveHub() {
        soundManager.playButtonClick()
        _gameState.update { it.copy(showCompetitiveHub = false) }
    }

    fun getDailyChallenge(): DailyChallenge = competitiveRepository.getDailyChallenge()
    fun getMissions(): List<Mission> = competitiveRepository.getMissions()
    fun getAchievements(): List<Achievement> = competitiveRepository.getAchievements()
    fun getPlayerStats(): PlayerStats = competitiveRepository.getPlayerStats()
    fun isThemeUnlocked(theme: GameTheme): Boolean = competitiveRepository.isThemeUnlocked(theme)
    fun getThemeUnlockRequirement(theme: GameTheme): String = competitiveRepository.getThemeUnlockRequirement(theme)

    fun pauseGame() {
        if (_gameState.value.screenState == ScreenState.PLAYING) {
            soundManager.playButtonClick()
            val current = _gameState.value
            gameSaveRepository.saveGame(
                board = current.board,
                pieces = current.currentPieces,
                score = current.score,
                comboStreak = current.comboStreak,
                canUndo = current.canUndo
            )
            _gameState.update { it.copy(screenState = ScreenState.PAUSED, hasSavedGame = true) }
        }
    }

    fun resumeGame() {
        if (_gameState.value.screenState == ScreenState.PAUSED) {
            soundManager.playButtonClick()
            _gameState.update { it.copy(screenState = ScreenState.PLAYING) }
        }
    }

    fun goToHome() {
        soundManager.playButtonClick()
        val current = _gameState.value
        val isGameOverOrDead = current.screenState == ScreenState.GAME_OVER ||
                current.currentPieces.isEmpty() ||
                current.currentPieces.none { !it.isUsed && it.canFit }

        if (isGameOverOrDead) {
            // Finished match returning to home: reset finished board and tray state
            previousMove = null
            gameSaveRepository.clearSavedGame()
            _gameState.update {
                it.copy(
                    screenState = ScreenState.START,
                    board = Board.EMPTY,
                    currentPieces = emptyList(),
                    score = 0,
                    comboStreak = 0,
                    canUndo = false,
                    canRevive = false,
                    hasSavedGame = false,
                    activeLineClear = null,
                    activeComboPopup = null,
                    lastPlacement = null
                )
            }
        } else {
            // Live active puzzle returning to home: persist it
            gameSaveRepository.saveGame(
                board = current.board,
                pieces = current.currentPieces,
                score = current.score,
                comboStreak = current.comboStreak,
                canUndo = current.canUndo
            )
            _gameState.update {
                it.copy(
                    screenState = ScreenState.START,
                    hasSavedGame = true
                )
            }
        }
    }

    fun toggleSound() {
        val newSoundState = !_gameState.value.isSoundEnabled
        scoreRepository.setSoundEnabled(newSoundState)
        soundManager.isEnabled = newSoundState
        _gameState.update { it.copy(isSoundEnabled = newSoundState) }
        if (newSoundState) soundManager.playButtonClick()
    }

    fun onBoardBoundsCalculated(bounds: Rect, cellSize: Float) {
        boardBounds = bounds
        boardCellSize = cellSize
    }

    fun onDragStart(
        pieceIndex: Int,
        piece: Piece,
        pointerInRoot: Offset,
        touchOffsetInPiece: Offset
    ) {
        if (_gameState.value.screenState != ScreenState.PLAYING) return
        soundManager.playPickUp()

        currentTouchPosition = pointerInRoot
        currentTouchOffset = touchOffsetInPiece
        currentDraggedPiece = piece
        _activeDragPieceIndex.value = pieceIndex

        val result = DragSensitivityHelper.calculatePlacement(
            board = _gameState.value.board,
            boardBounds = boardBounds,
            boardCellSize = boardCellSize,
            piece = piece,
            touchX = pointerInRoot.x,
            touchY = pointerInRoot.y,
            touchOffsetX = touchOffsetInPiece.x,
            touchOffsetY = touchOffsetInPiece.y
        )

        _dragPreviewState.value = DragPreviewState(
            piece = piece,
            targetRow = result.targetRow,
            targetCol = result.targetCol,
            isValid = result.isValid,
            potentialClearedRows = result.potentialClearedRows,
            potentialClearedCols = result.potentialClearedCols,
            isOverBoard = result.isOverBoard
        )

        _dragState.value = DragState(
            pieceIndex = pieceIndex,
            piece = piece,
            touchX = pointerInRoot.x,
            touchY = pointerInRoot.y,
            touchOffsetX = touchOffsetInPiece.x,
            touchOffsetY = touchOffsetInPiece.y,
            targetRow = result.targetRow,
            targetCol = result.targetCol,
            isValid = result.isValid,
            potentialClearedRows = result.potentialClearedRows,
            potentialClearedCols = result.potentialClearedCols,
            isOverBoard = result.isOverBoard
        )
    }

    fun onDrag(pointerInRoot: Offset) {
        val current = _dragState.value ?: return

        // 1. Update continuous touch position (consumed in Draw Phase)
        currentTouchPosition = pointerInRoot

        // 2. Authoritative placement calculation
        val result = DragSensitivityHelper.calculatePlacement(
            board = _gameState.value.board,
            boardBounds = boardBounds,
            boardCellSize = boardCellSize,
            piece = current.piece,
            touchX = pointerInRoot.x,
            touchY = pointerInRoot.y,
            touchOffsetX = current.touchOffsetX,
            touchOffsetY = current.touchOffsetY
        )

        _dragState.value = current.copy(
            touchX = pointerInRoot.x,
            touchY = pointerInRoot.y,
            targetRow = result.targetRow,
            targetCol = result.targetCol,
            isValid = result.isValid,
            potentialClearedRows = result.potentialClearedRows,
            potentialClearedCols = result.potentialClearedCols,
            isOverBoard = result.isOverBoard
        )

        // 3. Update preview state ONLY when target grid cell, validity, or cleared lines change
        val prevPreview = _dragPreviewState.value
        if (prevPreview == null ||
            prevPreview.targetRow != result.targetRow ||
            prevPreview.targetCol != result.targetCol ||
            prevPreview.isValid != result.isValid ||
            prevPreview.isOverBoard != result.isOverBoard ||
            prevPreview.potentialClearedRows != result.potentialClearedRows ||
            prevPreview.potentialClearedCols != result.potentialClearedCols
        ) {
            _dragPreviewState.value = DragPreviewState(
                piece = current.piece,
                targetRow = result.targetRow,
                targetCol = result.targetCol,
                isValid = result.isValid,
                potentialClearedRows = result.potentialClearedRows,
                potentialClearedCols = result.potentialClearedCols,
                isOverBoard = result.isOverBoard
            )
        }
    }

    fun onDragEnd() {
        if (isPlacementInProgress) return
        isPlacementInProgress = true

        try {
            val current = _dragState.value ?: return
            _dragState.value = null
            _activeDragPieceIndex.value = null
            _dragPreviewState.value = null
            currentDraggedPiece = null

            if (current.isValid && current.targetRow != null && current.targetCol != null) {
                val state = _gameState.value
                val result = GameEngine.executePlacement(
                    board = state.board,
                    pieces = state.currentPieces,
                    pieceIndex = current.pieceIndex,
                    startRow = current.targetRow,
                    startCol = current.targetCol,
                    currentStreak = state.comboStreak
                )

                if (result.success) {
                    // Save state for undo
                    previousMove = MoveHistory(
                        board = state.board,
                        currentPieces = state.currentPieces,
                        score = state.score,
                        comboStreak = state.comboStreak
                    )

                    // Track placed cells for pop animation
                    val placedCells = mutableListOf<Pair<Int, Int>>()
                    val p = current.piece
                    for (r in 0 until p.rowCount) {
                        for (c in 0 until p.colCount) {
                            if (p[r, c]) {
                                placedCells.add(Pair(current.targetRow + r, current.targetCol + c))
                            }
                        }
                    }

                    val newScore = state.score + result.scoreResult.pointsEarned
                    val currentBest = state.bestScore
                    val isNewBest = newScore > currentBest
                    val hadAlreadyBeatenBest = state.isNewHighScore

                    if (isNewBest) {
                        scoreRepository.saveBestScore(newScore)
                        if (!hadAlreadyBeatenBest) {
                            soundManager.playNewBest()
                        }
                    } else if (newScore / 1000 > state.score / 1000 && newScore > 0) {
                        soundManager.playScoreMilestone()
                    }

                    // Audio and combo feedback
                    val linesCleared = result.clearedRows.size + result.clearedCols.size
                    if (linesCleared > 0) {
                        if (result.scoreResult.newStreak > 1 || linesCleared > 1) {
                            soundManager.playCombo(result.scoreResult.newStreak)
                        } else {
                            soundManager.playLineClear()
                        }
                    } else if (!isNewBest || hadAlreadyBeatenBest) {
                        soundManager.playPlace()
                    }

                    // Update daily challenge progress
                    val completedDaily = competitiveRepository.recordDailyProgress(
                        result.scoreResult.pointsEarned,
                        linesCleared
                    )
                    if (completedDaily) {
                        soundManager.playDailyChallengeComplete()
                    }

                    // Update session metrics
                    val updatedSessionLines = state.sessionLinesCleared + linesCleared
                    val updatedSessionCombo = maxOf(state.sessionMaxCombo, result.scoreResult.newStreak)

                    // If hand is exhausted, deal 3 new pieces
                    var nextPieces = result.updatedPieces
                    if (result.isHandCompleted) {
                        val freshHand = blockGenerator.generateHand()
                        nextPieces = GameEngine.updatePiecesFitStatus(result.newBoard, freshHand)
                    }

                    val hasUnused = nextPieces.filter { !it.isUsed }
                    val isGameOverFinal = result.isGameOver || (hasUnused.isNotEmpty() && hasUnused.none { it.canFit })

                    var newlyUnlockedList = emptyList<Achievement>()
                    if (isGameOverFinal) {
                        soundManager.playGameOver()
                        newlyUnlockedList = competitiveRepository.recordMatchEnd(
                            finalScore = newScore,
                            linesInMatch = updatedSessionLines,
                            maxComboInMatch = updatedSessionCombo
                        )
                        gameSaveRepository.clearSavedGame()
                    } else {
                        // Persist ongoing valid game state
                        gameSaveRepository.saveGame(
                            board = result.newBoard,
                            pieces = nextPieces,
                            score = newScore,
                            comboStreak = result.scoreResult.newStreak,
                            canUndo = true
                        )
                    }

                    _gameState.update {
                        it.copy(
                            board = result.newBoard,
                            currentPieces = nextPieces,
                            score = newScore,
                            bestScore = maxOf(newScore, currentBest),
                            comboStreak = result.scoreResult.newStreak,
                            isNewHighScore = isNewBest || it.isNewHighScore,
                            screenState = if (isGameOverFinal) ScreenState.GAME_OVER else ScreenState.PLAYING,
                            activeLineClear = if (linesCleared > 0) LineClearEffect(result.clearedRows, result.clearedCols) else null,
                            activeComboPopup = if (linesCleared > 0 && (linesCleared > 1 || result.scoreResult.newStreak > 1)) {
                                ComboPopup(result.scoreResult.newStreak, result.scoreResult.comboBonus + result.scoreResult.pointsEarned)
                            } else null,
                            lastPlacement = PlacementAnimation(placedCells),
                            canUndo = !isGameOverFinal,
                            sessionLinesCleared = updatedSessionLines,
                            sessionMaxCombo = updatedSessionCombo,
                            newlyUnlockedAchievements = newlyUnlockedList,
                            canRevive = isGameOverFinal && rewardedAdManager.canRevive(),
                            hasSavedGame = !isGameOverFinal
                        )
                    }

                    // Auto-clear line burst and placement animation after short delay
                    if (linesCleared > 0) {
                        viewModelScope.launch {
                            delay(450)
                            _gameState.update { it.copy(activeLineClear = null) }
                        }
                    }
                } else {
                    soundManager.playInvalid()
                }
            } else {
                soundManager.playInvalid()
            }
        } finally {
            isPlacementInProgress = false
        }
    }

    fun onDragCancel() {
        _dragState.value = null
        _activeDragPieceIndex.value = null
        _dragPreviewState.value = null
        currentDraggedPiece = null
    }

    fun calculateDragState(
        pieceIndex: Int,
        piece: Piece,
        touchX: Float,
        touchY: Float,
        touchOffsetX: Float,
        touchOffsetY: Float
    ): DragState {
        val result = DragSensitivityHelper.calculatePlacement(
            board = _gameState.value.board,
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
}
