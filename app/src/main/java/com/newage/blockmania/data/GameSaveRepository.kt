package com.newage.blockmania.data

import android.content.Context
import android.content.SharedPreferences
import com.newage.blockmania.model.BOARD_SIZE
import com.newage.blockmania.model.BlockColor
import com.newage.blockmania.model.BlockShape
import com.newage.blockmania.model.Board
import com.newage.blockmania.model.Piece

data class SavedGameSession(
    val board: Board,
    val pieces: List<Piece>,
    val score: Int,
    val comboStreak: Int,
    val canUndo: Boolean
)

/**
 * Pure Kotlin high-performance serialization repository.
 * Stores board layout and active piece tray safely across app restarts
 * without reflection or external JSON library dependencies.
 */
class GameSaveRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun hasSavedGame(): Boolean {
        if (!prefs.getBoolean(KEY_HAS_SAVED_GAME, false)) return false
        val boardStr = prefs.getString(KEY_SAVED_BOARD, null)
        val piecesStr = prefs.getString(KEY_SAVED_PIECES, null)
        if (boardStr.isNullOrEmpty() || piecesStr.isNullOrEmpty()) {
            clearSavedGame()
            return false
        }
        return true
    }

    fun saveGame(
        board: Board,
        pieces: List<Piece>,
        score: Int,
        comboStreak: Int,
        canUndo: Boolean
    ) {
        val unused = pieces.filter { !it.isUsed }
        // Never save if pieces are empty, exhausted, or none can fit anywhere on the board (game over)
        if (unused.isEmpty() || unused.none { com.newage.blockmania.engine.GameEngine.canPieceFitAnywhere(board, it) }) {
            clearSavedGame()
            return
        }

        try {
            val boardStr = serializeBoard(board)
            val piecesStr = serializePieces(pieces)

            prefs.edit()
                .putBoolean(KEY_HAS_SAVED_GAME, true)
                .putString(KEY_SAVED_BOARD, boardStr)
                .putString(KEY_SAVED_PIECES, piecesStr)
                .putInt(KEY_SAVED_SCORE, score)
                .putInt(KEY_SAVED_STREAK, comboStreak)
                .putBoolean(KEY_SAVED_CAN_UNDO, canUndo)
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadGame(): SavedGameSession? {
        if (!hasSavedGame()) return null

        try {
            val boardStr = prefs.getString(KEY_SAVED_BOARD, null) ?: return null
            val piecesStr = prefs.getString(KEY_SAVED_PIECES, null) ?: return null
            val score = prefs.getInt(KEY_SAVED_SCORE, 0)
            val comboStreak = prefs.getInt(KEY_SAVED_STREAK, 0)
            val canUndo = prefs.getBoolean(KEY_SAVED_CAN_UNDO, false)

            val board = deserializeBoard(boardStr) ?: return null
            val pieces = deserializePieces(piecesStr)

            // Validate that loaded pieces actually contain at least one legal move
            val unused = pieces.filter { !it.isUsed }
            if (unused.isEmpty() || unused.none { com.newage.blockmania.engine.GameEngine.canPieceFitAnywhere(board, it) }) {
                clearSavedGame()
                return null
            }

            return SavedGameSession(
                board = board,
                pieces = pieces,
                score = score,
                comboStreak = comboStreak,
                canUndo = canUndo
            )
        } catch (e: Exception) {
            e.printStackTrace()
            clearSavedGame()
            return null
        }
    }

    fun clearSavedGame() {
        prefs.edit()
            .remove(KEY_HAS_SAVED_GAME)
            .remove(KEY_SAVED_BOARD)
            .remove(KEY_SAVED_PIECES)
            .remove(KEY_SAVED_SCORE)
            .remove(KEY_SAVED_STREAK)
            .remove(KEY_SAVED_CAN_UNDO)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "block_mania_saved_session"
        private const val KEY_HAS_SAVED_GAME = "key_has_saved_game"
        private const val KEY_SAVED_BOARD = "key_saved_board"
        private const val KEY_SAVED_PIECES = "key_saved_pieces"
        private const val KEY_SAVED_SCORE = "key_saved_score"
        private const val KEY_SAVED_STREAK = "key_saved_streak"
        private const val KEY_SAVED_CAN_UNDO = "key_saved_can_undo"

        fun serializeBoard(board: Board): String {
            val tokens = mutableListOf<String>()
            for (r in 0 until BOARD_SIZE) {
                for (c in 0 until BOARD_SIZE) {
                    val color = board[r, c]
                    tokens.add(color?.name ?: "_")
                }
            }
            return tokens.joinToString(",")
        }

        fun deserializeBoard(raw: String): Board? {
            val tokens = raw.split(",")
            if (tokens.size != BOARD_SIZE * BOARD_SIZE) return null

            val cells = MutableList(BOARD_SIZE) { MutableList<BlockColor?>(BOARD_SIZE) { null } }
            for (r in 0 until BOARD_SIZE) {
                for (c in 0 until BOARD_SIZE) {
                    val tok = tokens[r * BOARD_SIZE + c]
                    cells[r][c] = if (tok == "_" || tok.isEmpty()) null else {
                        try {
                            BlockColor.valueOf(tok)
                        } catch (_: Exception) {
                            null
                        }
                    }
                }
            }
            return Board(cells)
        }

        fun serializePieces(pieces: List<Piece>): String {
            return pieces.joinToString(";") { p ->
                "${p.id}|${p.shape.name}|${p.color.name}|${p.isUsed}|${p.canFit}"
            }
        }

        fun deserializePieces(raw: String): List<Piece> {
            if (raw.isEmpty()) return emptyList()
            return raw.split(";").mapNotNull { pieceTok ->
                val parts = pieceTok.split("|")
                if (parts.size >= 5) {
                    val id = parts[0]
                    val shapeName = parts[1]
                    val colorName = parts[2]
                    val isUsed = parts[3].toBoolean()
                    val canFit = parts[4].toBoolean()

                    val shape = BlockShape.ALL_SHAPES.firstOrNull { it.name == shapeName } ?: BlockShape.DOT
                    val color = try {
                        BlockColor.valueOf(colorName)
                    } catch (_: Exception) {
                        BlockColor.CYAN
                    }
                    Piece(id = id, shape = shape, color = color, isUsed = isUsed, canFit = canFit)
                } else null
            }
        }
    }
}
