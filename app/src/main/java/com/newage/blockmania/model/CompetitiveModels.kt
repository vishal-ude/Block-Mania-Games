package com.newage.blockmania.model

/**
 * Data structures for Competitive Mode, Player Progression, Daily Challenges, and Achievements.
 */

data class PlayerStats(
    val gamesPlayed: Int = 0,
    val totalLinesCleared: Int = 0,
    val highestCombo: Int = 0,
    val totalScore: Long = 0L,
    val dailyChallengesCompleted: Int = 0
)

data class DailyChallenge(
    val dateString: String,
    val title: String,
    val description: String,
    val targetScore: Int,
    val targetLines: Int,
    val currentScore: Int = 0,
    val currentLines: Int = 0,
    val isCompleted: Boolean = false,
    val rewardDescription: String = "Unlock Emerald Theme"
) {
    val scoreProgress: Float
        get() = if (targetScore > 0) (currentScore.toFloat() / targetScore).coerceIn(0f, 1f) else 1f

    val linesProgress: Float
        get() = if (targetLines > 0) (currentLines.toFloat() / targetLines).coerceIn(0f, 1f) else 1f
}

data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val targetProgress: Int,
    val currentProgress: Int = 0,
    val isCompleted: Boolean = false,
    val rewardDescription: String
) {
    val progressPercent: Float
        get() = if (targetProgress > 0) (currentProgress.toFloat() / targetProgress).coerceIn(0f, 1f) else 1f
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val targetValue: Int,
    val currentValue: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedDate: Long? = null,
    val iconType: String = "trophy"
) {
    val progressPercent: Float
        get() = if (targetValue > 0) (currentValue.toFloat() / targetValue).coerceIn(0f, 1f) else 1f
}

data class SessionMatchStats(
    val finalScore: Int = 0,
    val bestScore: Int = 0,
    val linesCleared: Int = 0,
    val maxCombo: Int = 0,
    val isNewBest: Boolean = false,
    val unlockedAchievements: List<Achievement> = emptyList()
)
