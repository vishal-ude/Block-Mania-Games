package com.newage.blockmania

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.newage.blockmania.model.ScreenState
import com.newage.blockmania.ui.screens.GameScreen
import com.newage.blockmania.ui.screens.StartScreen
import com.newage.blockmania.ui.theme.BlockManiaTheme
import com.newage.blockmania.ui.viewmodel.GameViewModel

import androidx.activity.SystemBarStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.core.view.WindowCompat

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enforce pure black status bar with white/light icons across all Android versions
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.BLACK),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.BLACK)
        )

        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        insetsController.isAppearanceLightNavigationBars = false

        setContent {
            BlockManiaTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    // Pure black status bar spacer guaranteeing zero content overlap
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsTopHeight(WindowInsets.statusBars)
                            .background(Color.Black)
                    )

                    // Safe game content area handling horizontal display cutouts and navigation bars
                    // Top inset is already cleanly handled by the status bar spacer above
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .windowInsetsPadding(
                                WindowInsets.navigationBars.union(
                                    WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                                )
                            )
                    ) {
                        BlockManiaApp(viewModel = gameViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun BlockManiaApp(viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsState()
    val isGameActive = gameState.screenState != ScreenState.START

    AnimatedContent(
        targetState = isGameActive,
        transitionSpec = {
            (fadeIn(animationSpec = tween(350)) + scaleIn(initialScale = 0.95f, animationSpec = tween(350)))
                .togetherWith(fadeOut(animationSpec = tween(250)))
        },
        label = "screenTransition"
    ) { active ->
        if (!active) {
            val hasActiveGame = (gameState.currentPieces.isNotEmpty() &&
                    gameState.currentPieces.any { !it.isUsed }) || gameState.hasSavedGame

            com.newage.blockmania.ui.screens.StartScreen(
                bestScore = gameState.bestScore,
                isSoundEnabled = gameState.isSoundEnabled,
                theme = gameState.selectedTheme,
                hasActiveGame = hasActiveGame,
                onSoundToggle = { viewModel.toggleSound() },
                onThemeClick = { viewModel.openThemeDialog() },
                onCompetitiveHubClick = { viewModel.openCompetitiveHub() },
                onNewGameClick = { viewModel.startGame(resumeIfActive = false) },
                onPlayClick = { viewModel.startGame(resumeIfActive = true) }
            )
            if (gameState.showThemeDialog) {
                com.newage.blockmania.ui.screens.ThemeSelectionDialog(
                    currentTheme = gameState.selectedTheme,
                    onThemeSelect = { viewModel.setTheme(it) },
                    isThemeUnlocked = { viewModel.isThemeUnlocked(it) },
                    getUnlockRequirement = { viewModel.getThemeUnlockRequirement(it) },
                    onDismiss = { viewModel.closeThemeDialog() }
                )
            }
            if (gameState.showCompetitiveHub) {
                com.newage.blockmania.ui.components.CompetitiveHubDialog(
                    dailyChallenge = viewModel.getDailyChallenge(),
                    missions = viewModel.getMissions(),
                    achievements = viewModel.getAchievements(),
                    playerStats = viewModel.getPlayerStats(),
                    theme = gameState.selectedTheme,
                    onDismiss = { viewModel.closeCompetitiveHub() },
                    onPlayDaily = {
                        viewModel.closeCompetitiveHub()
                        viewModel.startGame(resumeIfActive = true)
                    }
                )
            }
        } else {
            GameScreen(viewModel = viewModel)
        }
    }
}