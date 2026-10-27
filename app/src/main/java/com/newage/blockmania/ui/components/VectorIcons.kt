package com.newage.blockmania.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lightweight, high-performance custom vector icons for Block Mania.
 * Replaces system emojis with crisp, resolution-independent vector icons
 * with consistent stroke weights, proportions, and tactile feel.
 */
object VectorIcons {

    @Composable
    fun SoundIcon(
        isSoundEnabled: Boolean,
        tint: Color = Color.White,
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val strokeW = w * 0.10f

            // Speaker body
            val path = Path().apply {
                moveTo(w * 0.18f, h * 0.38f)
                lineTo(w * 0.36f, h * 0.38f)
                lineTo(w * 0.58f, h * 0.20f)
                lineTo(w * 0.58f, h * 0.80f)
                lineTo(w * 0.36f, h * 0.62f)
                lineTo(w * 0.18f, h * 0.62f)
                close()
            }
            drawPath(path, color = tint, style = Fill)

            if (isSoundEnabled) {
                // Sound wave 1
                val wave1 = Path().apply {
                    moveTo(w * 0.70f, h * 0.35f)
                    cubicTo(w * 0.78f, h * 0.42f, w * 0.78f, h * 0.58f, w * 0.70f, h * 0.65f)
                }
                drawPath(wave1, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

                // Sound wave 2
                val wave2 = Path().apply {
                    moveTo(w * 0.84f, h * 0.24f)
                    cubicTo(w * 0.98f, h * 0.38f, w * 0.98f, h * 0.62f, w * 0.84f, h * 0.76f)
                }
                drawPath(wave2, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))
            } else {
                // Mute diagonal slash
                drawLine(
                    color = tint,
                    start = Offset(w * 0.20f, h * 0.82f),
                    end = Offset(w * 0.85f, h * 0.18f),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
            }
        }
    }

    @Composable
    fun UndoIcon(
        tint: Color = Color.White,
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val strokeW = w * 0.11f

            // Counter-clockwise return arc
            val arcPath = Path().apply {
                moveTo(w * 0.32f, h * 0.44f)
                cubicTo(w * 0.40f, h * 0.26f, w * 0.76f, h * 0.26f, w * 0.82f, h * 0.50f)
                cubicTo(w * 0.88f, h * 0.74f, w * 0.66f, h * 0.86f, w * 0.46f, h * 0.86f)
            }
            drawPath(
                arcPath,
                color = tint,
                style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Arrow head pointing down-left
            val arrowHead = Path().apply {
                moveTo(w * 0.42f, h * 0.24f)
                lineTo(w * 0.20f, h * 0.42f)
                lineTo(w * 0.42f, h * 0.58f)
            }
            drawPath(
                arrowHead,
                color = tint,
                style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }

    @Composable
    fun ThemeIcon(
        tint: Color = Color.White,
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val strokeW = w * 0.10f

            // Artist palette silhouette
            val palette = Path().apply {
                moveTo(w * 0.50f, h * 0.14f)
                cubicTo(w * 0.82f, h * 0.14f, w * 0.90f, h * 0.40f, w * 0.88f, h * 0.60f)
                cubicTo(w * 0.86f, h * 0.80f, w * 0.68f, h * 0.88f, w * 0.50f, h * 0.88f)
                cubicTo(w * 0.38f, h * 0.88f, w * 0.28f, h * 0.82f, w * 0.24f, h * 0.72f)
                cubicTo(w * 0.20f, h * 0.64f, w * 0.24f, h * 0.56f, w * 0.32f, h * 0.56f)
                cubicTo(w * 0.40f, h * 0.56f, w * 0.44f, h * 0.62f, w * 0.44f, h * 0.68f)
                cubicTo(w * 0.44f, h * 0.74f, w * 0.34f, h * 0.74f, w * 0.34f, h * 0.74f)
                cubicTo(w * 0.22f, h * 0.74f, w * 0.14f, h * 0.56f, w * 0.14f, h * 0.44f)
                cubicTo(w * 0.14f, h * 0.24f, w * 0.30f, h * 0.14f, w * 0.50f, h * 0.14f)
                close()
            }
            drawPath(
                palette,
                color = tint,
                style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Paint dots
            drawCircle(color = tint, radius = w * 0.065f, center = Offset(w * 0.38f, h * 0.32f))
            drawCircle(color = tint, radius = w * 0.065f, center = Offset(w * 0.58f, h * 0.28f))
            drawCircle(color = tint, radius = w * 0.065f, center = Offset(w * 0.72f, h * 0.44f))
        }
    }

    @Composable
    fun RestartIcon(
        tint: Color = Color.White,
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val strokeW = w * 0.11f

            // Circular refresh arc (approx 290 degrees)
            val arcPath = Path().apply {
                moveTo(w * 0.50f, h * 0.18f)
                cubicTo(w * 0.74f, h * 0.18f, w * 0.84f, h * 0.36f, w * 0.84f, h * 0.52f)
                cubicTo(w * 0.84f, h * 0.72f, w * 0.70f, h * 0.84f, w * 0.50f, h * 0.84f)
                cubicTo(w * 0.30f, h * 0.84f, w * 0.18f, h * 0.70f, w * 0.18f, h * 0.52f)
                cubicTo(w * 0.18f, h * 0.40f, w * 0.24f, h * 0.30f, w * 0.32f, h * 0.24f)
            }
            drawPath(
                arcPath,
                color = tint,
                style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Arrowhead at top
            val arrow = Path().apply {
                moveTo(w * 0.54f, h * 0.08f)
                lineTo(w * 0.54f, h * 0.28f)
                lineTo(w * 0.36f, h * 0.20f)
                close()
            }
            drawPath(arrow, color = tint, style = Fill)
        }
    }

    @Composable
    fun PauseIcon(
        tint: Color = Color.White,
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val barW = w * 0.18f
            val barH = h * 0.56f
            val corner = CornerRadius(barW * 0.4f, barW * 0.4f)
            val top = (h - barH) / 2f

            // Left bar
            drawRoundRect(
                color = tint,
                topLeft = Offset(w * 0.26f, top),
                size = Size(barW, barH),
                cornerRadius = corner
            )
            // Right bar
            drawRoundRect(
                color = tint,
                topLeft = Offset(w * 0.56f, top),
                size = Size(barW, barH),
                cornerRadius = corner
            )
        }
    }

    @Composable
    fun StarIcon(
        tint: Color = Color(0xFFFFC700),
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f
            val outerR = w * 0.44f
            val innerR = outerR * 0.44f

            val path = Path()
            for (i in 0 until 10) {
                val r = if (i % 2 == 0) outerR else innerR
                val angle = (i * 36 - 90) * (Math.PI / 180.0)
                val x = (cx + r * cos(angle)).toFloat()
                val y = (cy + r * sin(angle)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path, color = tint, style = Fill)
        }
    }

    @Composable
    fun TrophyIcon(
        tint: Color = Color(0xFFFFD700),
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val strokeW = w * 0.08f

            // Trophy Cup Body
            val cupPath = Path().apply {
                moveTo(w * 0.28f, h * 0.18f)
                lineTo(w * 0.72f, h * 0.18f)
                lineTo(w * 0.68f, h * 0.52f)
                cubicTo(w * 0.64f, h * 0.66f, w * 0.36f, h * 0.66f, w * 0.32f, h * 0.52f)
                close()
            }
            drawPath(cupPath, color = tint, style = Fill)

            // Left Handle
            val leftHandle = Path().apply {
                moveTo(w * 0.28f, h * 0.26f)
                cubicTo(w * 0.12f, h * 0.26f, w * 0.12f, h * 0.48f, w * 0.32f, h * 0.48f)
            }
            drawPath(leftHandle, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

            // Right Handle
            val rightHandle = Path().apply {
                moveTo(w * 0.72f, h * 0.26f)
                cubicTo(w * 0.88f, h * 0.26f, w * 0.88f, h * 0.48f, w * 0.68f, h * 0.48f)
            }
            drawPath(rightHandle, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

            // Stem & Pedestal Base
            val stemPath = Path().apply {
                moveTo(w * 0.44f, h * 0.64f)
                lineTo(w * 0.56f, h * 0.64f)
                lineTo(w * 0.54f, h * 0.76f)
                lineTo(w * 0.46f, h * 0.76f)
                close()
            }
            drawPath(stemPath, color = tint, style = Fill)

            // Base plate
            drawRoundRect(
                color = tint,
                topLeft = Offset(w * 0.26f, h * 0.76f),
                size = Size(w * 0.48f, h * 0.10f),
                cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
            )
        }
    }

    @Composable
    fun HomeIcon(
        tint: Color = Color.White,
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Roof
            val roofPath = Path().apply {
                moveTo(w * 0.12f, h * 0.46f)
                lineTo(w * 0.50f, h * 0.14f)
                lineTo(w * 0.88f, h * 0.46f)
            }
            drawPath(roofPath, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))

            // Body
            val bodyPath = Path().apply {
                moveTo(w * 0.24f, h * 0.44f)
                lineTo(w * 0.24f, h * 0.84f)
                lineTo(w * 0.76f, h * 0.84f)
                lineTo(w * 0.76f, h * 0.44f)
            }
            drawPath(bodyPath, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))

            // Door
            val doorPath = Path().apply {
                moveTo(w * 0.42f, h * 0.84f)
                lineTo(w * 0.42f, h * 0.60f)
                lineTo(w * 0.58f, h * 0.60f)
                lineTo(w * 0.58f, h * 0.84f)
            }
            drawPath(doorPath, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    @Composable
    fun LockIcon(
        tint: Color = Color(0xFF94A3B8),
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Shackle
            val shacklePath = Path().apply {
                moveTo(w * 0.32f, h * 0.48f)
                lineTo(w * 0.32f, h * 0.32f)
                cubicTo(w * 0.32f, h * 0.14f, w * 0.68f, h * 0.14f, w * 0.68f, h * 0.32f)
                lineTo(w * 0.68f, h * 0.48f)
            }
            drawPath(shacklePath, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round))

            // Body
            drawRoundRect(
                color = tint,
                topLeft = Offset(w * 0.20f, h * 0.46f),
                size = Size(w * 0.60f, h * 0.44f),
                cornerRadius = CornerRadius(w * 0.10f, w * 0.10f)
            )

            // Keyhole
            drawCircle(
                color = Color.Black.copy(alpha = 0.5f),
                radius = w * 0.07f,
                center = Offset(w * 0.50f, h * 0.65f)
            )
        }
    }

    @Composable
    fun CheckmarkIcon(
        tint: Color = Color(0xFF10B981),
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            val path = Path().apply {
                moveTo(w * 0.20f, h * 0.52f)
                lineTo(w * 0.42f, h * 0.74f)
                lineTo(w * 0.82f, h * 0.26f)
            }
            drawPath(path, color = tint, style = Stroke(width = w * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    @Composable
    fun PlayIcon(
        tint: Color = Color.White,
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            val path = Path().apply {
                moveTo(w * 0.28f, h * 0.20f)
                lineTo(w * 0.80f, h * 0.50f)
                lineTo(w * 0.28f, h * 0.80f)
                close()
            }
            drawPath(path, color = tint, style = Fill)
        }
    }

    @Composable
    fun MedalIcon(
        tint: Color = Color(0xFFFFD700),
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Ribbon left & right
            val ribbon = Path().apply {
                moveTo(w * 0.32f, h * 0.12f)
                lineTo(w * 0.45f, h * 0.46f)
                lineTo(w * 0.55f, h * 0.46f)
                lineTo(w * 0.68f, h * 0.12f)
            }
            drawPath(ribbon, color = tint.copy(alpha = 0.75f), style = Stroke(width = w * 0.08f, cap = StrokeCap.Round))

            // Medal circle
            drawCircle(
                color = tint,
                radius = w * 0.30f,
                center = Offset(w * 0.50f, h * 0.65f)
            )

            // Inner star
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = w * 0.18f,
                center = Offset(w * 0.50f, h * 0.65f),
                style = Stroke(width = w * 0.04f)
            )
        }
    }

    @Composable
    fun AdRewardIcon(
        tint: Color = Color(0xFFFFC700),
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Video frame outline
            drawRoundRect(
                color = tint,
                topLeft = Offset(w * 0.12f, h * 0.20f),
                size = Size(w * 0.76f, h * 0.60f),
                cornerRadius = CornerRadius(w * 0.10f, w * 0.10f),
                style = Stroke(width = w * 0.09f)
            )

            // Inner play triangle
            val playPath = Path().apply {
                moveTo(w * 0.44f, h * 0.38f)
                lineTo(w * 0.64f, h * 0.50f)
                lineTo(w * 0.44f, h * 0.62f)
                close()
            }
            drawPath(playPath, color = tint, style = Fill)
        }
    }

    @Composable
    fun CalendarIcon(
        tint: Color = Color.White,
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Outer box
            drawRoundRect(
                color = tint,
                topLeft = Offset(w * 0.15f, h * 0.22f),
                size = Size(w * 0.70f, h * 0.66f),
                cornerRadius = CornerRadius(w * 0.10f, w * 0.10f),
                style = Stroke(width = w * 0.09f)
            )

            // Top header bar
            drawLine(
                color = tint,
                start = Offset(w * 0.15f, h * 0.42f),
                end = Offset(w * 0.85f, h * 0.42f),
                strokeWidth = w * 0.07f
            )

            // 2 Top binder pins
            drawLine(
                color = tint,
                start = Offset(w * 0.35f, h * 0.12f),
                end = Offset(w * 0.35f, h * 0.26f),
                strokeWidth = w * 0.09f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = tint,
                start = Offset(w * 0.65f, h * 0.12f),
                end = Offset(w * 0.65f, h * 0.26f),
                strokeWidth = w * 0.09f,
                cap = StrokeCap.Round
            )

            // Star or check in calendar
            drawCircle(
                color = tint,
                radius = w * 0.07f,
                center = Offset(w * 0.50f, h * 0.62f)
            )
        }
    }

    @Composable
    fun CloseIcon(
        tint: Color = Color.White,
        size: Dp = 18.dp,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val strokeW = w * 0.12f

            drawLine(
                color = tint,
                start = Offset(w * 0.25f, h * 0.25f),
                end = Offset(w * 0.75f, h * 0.75f),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
            drawLine(
                color = tint,
                start = Offset(w * 0.75f, h * 0.25f),
                end = Offset(w * 0.25f, h * 0.75f),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
        }
    }
}
