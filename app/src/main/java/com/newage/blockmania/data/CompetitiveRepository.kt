package com.newage.blockmania.data

import android.content.Context
import android.content.SharedPreferences
import com.newage.blockmania.model.Achievement
import com.newage.blockmania.model.DailyChallenge
import com.newage.blockmania.model.Mission
import com.newage.blockmania.model.PlayerStats
import com.newage.blockmania.theme.GameTheme
import java.time.LocalDate
import kotlin.math.abs

class CompetitiveRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getPlayerStats(): PlayerStats {
        return PlayerStats(
            gamesPlayed = prefs.getInt(KEY_GAMES_PLAYED, 0),
            totalLinesCleared = prefs.getInt(KEY_TOTAL_LINES, 0),
            highestCombo = prefs.getInt(KEY_HIGHEST_COMBO, 0),
            totalScore = prefs.getLong(KEY_TOTAL_SCORE, 0L),
            dailyChallengesCompleted = prefs.getInt(KEY_DAILY_COMPLETED_COUNT, 0)
        )
    }

    fun getDailyChallenge(): DailyChallenge {
        val today = LocalDate.now().toString()
        val savedDate = prefs.getString(KEY_DAILY_DATE, null)

        if (savedDate != today) {
            // New day: deterministically generate targets from date string
            val hash = abs(today.hashCode())
            val targetScore = 1200 + (hash % 9) * 150 // 1200 - 2400
            val targetLines = 8 + (hash % 6) * 2     // 8 - 18 lines

            prefs.edit()
                .putString(KEY_DAILY_DATE, today)
                .putInt(KEY_DAILY_TARGET_SCORE, targetScore)
                .putInt(KEY_DAILY_TARGET_LINES, targetLines)
                .putInt(KEY_DAILY_CURR_SCORE, 0)
                .putInt(KEY_DAILY_CURR_LINES, 0)
                .putBoolean(KEY_DAILY_COMPLETED, false)
                .apply()

            return DailyChallenge(
                dateString = today,
                title = "Daily Master Challenge",
                description = "Score $targetScore pts and clear $targetLines lines today!",
                targetScore = targetScore,
                targetLines = targetLines,
                currentScore = 0,
                currentLines = 0,
                isCompleted = false
            )
        }

        val targetScore = prefs.getInt(KEY_DAILY_TARGET_SCORE, 1500)
        val targetLines = prefs.getInt(KEY_DAILY_TARGET_LINES, 10)
        val currScore = prefs.getInt(KEY_DAILY_CURR_SCORE, 0)
        val currLines = prefs.getInt(KEY_DAILY_CURR_LINES, 0)
        val isCompleted = prefs.getBoolean(KEY_DAILY_COMPLETED, false)

        return DailyChallenge(
            dateString = today,
            title = "Daily Master Challenge",
            description = "Score $targetScore pts and clear $targetLines lines today!",
            targetScore = targetScore,
            targetLines = targetLines,
            currentScore = currScore,
            currentLines = currLines,
            isCompleted = isCompleted
        )
    }

    /**
     * Updates daily challenge progress during active gameplay.
     * Returns true if the challenge was just completed with this update.
     */
    fun recordDailyProgress(scoreGain: Int, linesGain: Int): Boolean {
        val challenge = getDailyChallenge()
        if (challenge.isCompleted) return false

        val newScore = challenge.currentScore + scoreGain
        val newLines = challenge.currentLines + linesGain
        val completed = newScore >= challenge.targetScore && newLines >= challenge.targetLines

        val editor = prefs.edit()
            .putInt(KEY_DAILY_CURR_SCORE, newScore)
            .putInt(KEY_DAILY_CURR_LINES, newLines)

        if (completed) {
            editor.putBoolean(KEY_DAILY_COMPLETED, true)
            val count = prefs.getInt(KEY_DAILY_COMPLETED_COUNT, 0) + 1
            editor.putInt(KEY_DAILY_COMPLETED_COUNT, count)
            // Complete unlock for Emerald theme
            unlockTheme(GameTheme.EMERALD)
        }
        editor.apply()

        return completed
    }

    fun getMissions(): List<Mission> {
        val stats = getPlayerStats()
        val daily = getDailyChallenge()

        return listOf(
            Mission(
                id = "mission_lines_25",
                title = "Line Sweeper",
                description = "Clear 25 total lines across games",
                targetProgress = 25,
                currentProgress = minOf(stats.totalLinesCleared, 25),
                isCompleted = stats.totalLinesCleared >= 25,
                rewardDescription = "Unlocks Ocean Blue Theme"
            ),
            Mission(
                id = "mission_combo_3",
                title = "Combo Specialist",
                description = "Reach a 3x combo streak in any game",
                targetProgress = 3,
                currentProgress = minOf(stats.highestCombo, 3),
                isCompleted = stats.highestCombo >= 3,
                rewardDescription = "Unlocks Candy Pop Theme"
            ),
            Mission(
                id = "mission_score_2000",
                title = "Score Maestro",
                description = "Accumulate 5,000 total score points",
                targetProgress = 5000,
                currentProgress = minOf(stats.totalScore.toInt(), 5000),
                isCompleted = stats.totalScore >= 5000L,
                rewardDescription = "Unlocks Emerald Theme"
            ),
            Mission(
                id = "mission_daily_1",
                title = "Daily Devotee",
                description = "Complete today's Daily Challenge",
                targetProgress = 1,
                currentProgress = if (daily.isCompleted) 1 else 0,
                isCompleted = daily.isCompleted,
                rewardDescription = "Master Challenger Badge"
            )
        )
    }

    fun getAchievements(): List<Achievement> {
        val stats = getPlayerStats()
        val bestScore = prefs.getInt("key_best_score_comp", 0)

        val list = mutableListOf<Achievement>()

        fun addAch(
            id: String,
            title: String,
            desc: String,
            target: Int,
            curr: Int,
            icon: String
        ) {
            val unlockedKey = "ach_unlocked_$id"
            val dateKey = "ach_date_$id"
            val isUnlocked = prefs.getBoolean(unlockedKey, curr >= target)
            val date = if (isUnlocked) prefs.getLong(dateKey, System.currentTimeMillis()) else null

            list.add(
                Achievement(
                    id = id,
                    title = title,
                    description = desc,
                    targetValue = target,
                    currentValue = minOf(curr, target),
                    isUnlocked = isUnlocked,
                    unlockedDate = date,
                    iconType = icon
                )
            )
        }

        addAch("first_clear", "First Clear", "Clear your first line", 1, stats.totalLinesCleared, "star")
        addAch("combo_master", "Combo Master", "Achieve a 3x combo streak", 3, stats.highestCombo, "medal")
        addAch("combo_legend", "Combo Legend", "Achieve a 5x combo streak", 5, stats.highestCombo, "trophy")
        addAch("lines_10", "Line Striker", "Clear 10 total lines", 10, stats.totalLinesCleared, "star")
        addAch("lines_50", "Grid Dominator", "Clear 50 total lines", 50, stats.totalLinesCleared, "trophy")
        addAch("score_1000", "Pioneer", "Score 1,000 points", 1000, bestScore, "star")
        addAch("score_3000", "Score Titan", "Score 3,000 points", 3000, bestScore, "medal")
        addAch("score_5000", "Puzzle Legend", "Score 5,000 points", 5000, bestScore, "trophy")
        addAch("daily_hero", "Daily Hero", "Complete 1 Daily Challenge", 1, stats.dailyChallengesCompleted, "calendar")

        return list
    }

    /**
     * Records match conclusion and checks for freshly unlocked achievements.
     * Returns the list of achievements newly unlocked in this session.
     */
    fun recordMatchEnd(
        finalScore: Int,
        linesInMatch: Int,
        maxComboInMatch: Int
    ): List<Achievement> {
        val prevStats = getPlayerStats()
        val currentBest = prefs.getInt("key_best_score_comp", 0)
        val newBest = maxOf(currentBest, finalScore)

        val newGamesPlayed = prevStats.gamesPlayed + 1
        val newTotalLines = prevStats.totalLinesCleared + linesInMatch
        val newHighestCombo = maxOf(prevStats.highestCombo, maxComboInMatch)
        val newTotalScore = prevStats.totalScore + finalScore

        prefs.edit()
            .putInt(KEY_GAMES_PLAYED, newGamesPlayed)
            .putInt(KEY_TOTAL_LINES, newTotalLines)
            .putInt(KEY_HIGHEST_COMBO, newHighestCombo)
            .putLong(KEY_TOTAL_SCORE, newTotalScore)
            .putInt("key_best_score_comp", newBest)
            .apply()

        // Check and persist newly unlocked achievements
        val newlyUnlocked = mutableListOf<Achievement>()
        val allAchievements = getAchievements()

        val editor = prefs.edit()
        for (ach in allAchievements) {
            val key = "ach_unlocked_${ach.id}"
            val alreadyUnlocked = prefs.getBoolean(key, false)
            if (!alreadyUnlocked && ach.currentValue >= ach.targetValue) {
                editor.putBoolean(key, true)
                editor.putLong("ach_date_${ach.id}", System.currentTimeMillis())
                newlyUnlocked.add(ach.copy(isUnlocked = true, unlockedDate = System.currentTimeMillis()))
            }
        }
        editor.apply()

        // Automatic theme unlocks
        if (newBest >= 1000 || newTotalLines >= 20) unlockTheme(GameTheme.OCEAN_BLUE)
        if (newHighestCombo >= 3 || newBest >= 3000) unlockTheme(GameTheme.CANDY_POP)
        if (prevStats.dailyChallengesCompleted >= 1 || newBest >= 5000) unlockTheme(GameTheme.EMERALD)

        return newlyUnlocked
    }

    fun isThemeUnlocked(theme: GameTheme): Boolean {
        if (theme == GameTheme.GALAXY) return true // Default theme is always unlocked
        if (prefs.getBoolean("theme_unlocked_${theme.id}", false)) return true

        val stats = getPlayerStats()
        val bestScore = prefs.getInt("key_best_score_comp", 0)

        return when (theme) {
            GameTheme.GALAXY -> true
            GameTheme.OCEAN_BLUE -> bestScore >= 1000 || stats.totalLinesCleared >= 20
            GameTheme.CANDY_POP -> stats.highestCombo >= 3 || bestScore >= 3000
            GameTheme.EMERALD -> stats.dailyChallengesCompleted >= 1 || bestScore >= 5000
        }
    }

    fun unlockTheme(theme: GameTheme) {
        prefs.edit().putBoolean("theme_unlocked_${theme.id}", true).apply()
    }

    fun getThemeUnlockRequirement(theme: GameTheme): String {
        return when (theme) {
            GameTheme.GALAXY -> "Unlocked by Default"
            GameTheme.OCEAN_BLUE -> "Reach 1,000 score or clear 20 lines"
            GameTheme.CANDY_POP -> "Reach 3x combo or 3,000 score"
            GameTheme.EMERALD -> "Complete 1 Daily Challenge or 5,000 score"
        }
    }

    companion object {
        private const val PREFS_NAME = "block_mania_competitive_prefs"
        private const val KEY_GAMES_PLAYED = "key_games_played"
        private const val KEY_TOTAL_LINES = "key_total_lines"
        private const val KEY_HIGHEST_COMBO = "key_highest_combo"
        private const val KEY_TOTAL_SCORE = "key_total_score"
        private const val KEY_DAILY_COMPLETED_COUNT = "key_daily_completed_count"

        private const val KEY_DAILY_DATE = "key_daily_date"
        private const val KEY_DAILY_TARGET_SCORE = "key_daily_target_score"
        private const val KEY_DAILY_TARGET_LINES = "key_daily_target_lines"
        private const val KEY_DAILY_CURR_SCORE = "key_daily_curr_score"
        private const val KEY_DAILY_CURR_LINES = "key_daily_curr_lines"
        private const val KEY_DAILY_COMPLETED = "key_daily_completed"
    }
}
