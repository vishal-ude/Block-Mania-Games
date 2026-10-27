package com.newage.blockmania.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newage.blockmania.model.ComboPopup
import com.newage.blockmania.theme.GameTheme
import kotlinx.coroutines.delay

@Composable
fun ComboBanner(
    comboPopup: ComboPopup?,
    theme: GameTheme = GameTheme.GALAXY,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(comboPopup?.timestamp) {
        if (comboPopup != null) {
            visible = true
            delay(1200)
            visible = false
        }
    }

    AnimatedVisibility(
        visible = visible && comboPopup != null,
        enter = scaleIn(initialScale = 0.5f, animationSpec = tween(200, easing = FastOutSlowInEasing)) + fadeIn(),
        exit = scaleOut(targetScale = 1.2f, animationSpec = tween(300, easing = LinearEasing)) + fadeOut(),
        modifier = modifier
    ) {
        if (comboPopup != null) {
            val title = when (comboPopup.comboCount) {
                1 -> "GREAT!"
                2 -> "SUPER COMBO!"
                3 -> "FANTASTIC!"
                4 -> "INCREDIBLE!"
                else -> "MANIA BOOM!"
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                theme.scoreAccent,
                                theme.bestScoreAccent,
                                Color(0xFFFFD700)
                            )
                        )
                    )
                    .border(2.5.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "+${comboPopup.bonusPoints} PTS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E2542)
                    )
                }
            }
        }
    }
}
