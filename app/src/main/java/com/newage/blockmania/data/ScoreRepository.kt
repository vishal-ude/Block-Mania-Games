package com.newage.blockmania.data

import android.content.Context
import android.content.SharedPreferences
import com.newage.blockmania.theme.GameTheme

class ScoreRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getBestScore(): Int {
        return prefs.getInt(KEY_BEST_SCORE, 0)
    }

    fun saveBestScore(score: Int) {
        val currentBest = getBestScore()
        if (score > currentBest) {
            prefs.edit().putInt(KEY_BEST_SCORE, score).apply()
        }
    }

    fun isSoundEnabled(): Boolean {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    fun getSelectedTheme(): GameTheme {
        val savedId = prefs.getString(KEY_SELECTED_THEME, GameTheme.GALAXY.id)
        return GameTheme.fromId(savedId)
    }

    fun saveSelectedTheme(theme: GameTheme) {
        prefs.edit().putString(KEY_SELECTED_THEME, theme.id).apply()
    }

    companion object {
        private const val PREFS_NAME = "block_mania_prefs"
        private const val KEY_BEST_SCORE = "key_best_score"
        private const val KEY_SOUND_ENABLED = "key_sound_enabled"
        private const val KEY_SELECTED_THEME = "key_selected_theme"
    }
}
