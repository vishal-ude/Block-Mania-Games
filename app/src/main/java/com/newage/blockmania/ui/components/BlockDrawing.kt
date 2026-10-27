package com.newage.blockmania.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.newage.blockmania.model.BlockColor
import com.newage.blockmania.theme.GameTheme
import com.newage.blockmania.theme.themed

/**
 * High-performance drawing utilities for rendering Block Mania 3D blocks and grid cells.
 * Supports dynamic themes with rich gradients, highlights, gloss reflections, and floating shadows.
 */
object BlockDrawing {

    /**
     * Draws an empty recessed board cell with the active theme colors.
     */
    fun drawEmptyCell(
        drawScope: DrawScope,
        topLeft: Offset,
        size: Size,
        theme: GameTheme = GameTheme.GALAXY,
        isFlashing: Boolean = false
    ) {
        val corner = CornerRadius(size.width * 0.20f, size.height * 0.20f)
        val pad = size.width * 0.055f
        val innerSize = Size(size.width - pad * 2, size.height - pad * 2)
        val innerTopLeft = Offset(topLeft.x + pad, topLeft.y + pad)

        val bg = if (isFlashing) theme.lineClearGlow.copy(alpha = 0.70f) else theme.emptyCellColor
        val border = if (isFlashing) Color.White else theme.emptyCellBorder

        drawScope.drawRoundRect(
            color = bg,
            topLeft = innerTopLeft,
            size = innerSize,
            cornerRadius = corner,
            style = Fill
        )
        drawScope.drawRoundRect(
            color = border,
            topLeft = innerTopLeft,
            size = innerSize,
            cornerRadius = corner,
            style = Stroke(width = 1.5f)
        )
    }

    /**
     * Draws a vibrant, playful 3D block with top highlights, lower edge shadow, and gloss reflection.
     */
    fun drawOccupiedBlock(
        drawScope: DrawScope,
        topLeft: Offset,
        size: Size,
        color: BlockColor,
        theme: GameTheme = GameTheme.GALAXY,
        alpha: Float = 1.0f,
        scale: Float = 1.0f,
        isDragging: Boolean = false
    ) {
        val themedColors = color.themed(theme)

        val actualWidth = size.width * scale
        val actualHeight = size.height * scale
        val offsetX = topLeft.x + (size.width - actualWidth) / 2f
        val offsetY = topLeft.y + (size.height - actualHeight) / 2f

        val pad = actualWidth * 0.055f
        val blockWidth = actualWidth - pad * 2
        val blockHeight = actualHeight - pad * 2
        val blockTopLeft = Offset(offsetX + pad, offsetY + pad)
        val cornerRadius = CornerRadius(blockWidth * 0.24f, blockHeight * 0.24f)

        // 1. Drop shadow / bottom bevel
        if (isDragging) {
            // Elevated soft shadow when floating above the board
            drawScope.drawRoundRect(
                color = Color.Black.copy(alpha = 0.40f * alpha),
                topLeft = Offset(blockTopLeft.x + 3f, blockTopLeft.y + actualHeight * 0.14f),
                size = Size(blockWidth, blockHeight),
                cornerRadius = cornerRadius,
                style = Fill
            )
        } else {
            // Base 3D bevel shadow under placed block
            drawScope.drawRoundRect(
                color = themedColors.shadow.copy(alpha = 0.60f * alpha),
                topLeft = Offset(blockTopLeft.x, blockTopLeft.y + actualHeight * 0.07f),
                size = Size(blockWidth, blockHeight),
                cornerRadius = cornerRadius,
                style = Fill
            )
        }

        // 2. Main block body with subtle gradient
        val bodyBrush = Brush.verticalGradient(
            colors = listOf(
                themedColors.highlight.copy(alpha = alpha),
                themedColors.primary.copy(alpha = alpha),
                themedColors.shadow.copy(alpha = alpha)
            ),
            startY = blockTopLeft.y,
            endY = blockTopLeft.y + blockHeight
        )

        drawScope.drawRoundRect(
            brush = bodyBrush,
            topLeft = blockTopLeft,
            size = Size(blockWidth, blockHeight),
            cornerRadius = cornerRadius,
            style = Fill
        )

        // 3. Subtle top bevel outline
        val bevelStrokeWidth = blockWidth * 0.06f
        drawScope.drawRoundRect(
            color = themedColors.highlight.copy(alpha = 0.75f * alpha),
            topLeft = blockTopLeft,
            size = Size(blockWidth, blockHeight),
            cornerRadius = cornerRadius,
            style = Stroke(width = bevelStrokeWidth)
        )

        // 4. Gloss shine reflection on upper third of the block
        val shineWidth = blockWidth * 0.68f
        val shineHeight = blockHeight * 0.30f
        val shineTopLeft = Offset(
            blockTopLeft.x + (blockWidth - shineWidth) / 2f,
            blockTopLeft.y + blockHeight * 0.08f
        )
        val shineCorner = CornerRadius(shineHeight * 0.5f, shineHeight * 0.5f)

        drawScope.drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.55f * alpha),
                    Color.White.copy(alpha = 0.08f * alpha)
                )
            ),
            topLeft = shineTopLeft,
            size = Size(shineWidth, shineHeight),
            cornerRadius = shineCorner,
            style = Fill
        )
    }

    /**
     * Draws a glowing ghost preview block when hovering over valid drop coordinates.
     */
    fun drawGhostBlock(
        drawScope: DrawScope,
        topLeft: Offset,
        size: Size,
        color: BlockColor,
        theme: GameTheme = GameTheme.GALAXY
    ) {
        val themedColors = color.themed(theme)
        val pad = size.width * 0.06f
        val blockWidth = size.width - pad * 2
        val blockHeight = size.height - pad * 2
        val blockTopLeft = Offset(topLeft.x + pad, topLeft.y + pad)
        val cornerRadius = CornerRadius(blockWidth * 0.22f, blockHeight * 0.22f)

        // Soft glowing fill
        drawScope.drawRoundRect(
            color = themedColors.primary.copy(alpha = 0.35f),
            topLeft = blockTopLeft,
            size = Size(blockWidth, blockHeight),
            cornerRadius = cornerRadius,
            style = Fill
        )

        // Bright outline
        drawScope.drawRoundRect(
            color = themedColors.highlight.copy(alpha = 0.90f),
            topLeft = blockTopLeft,
            size = Size(blockWidth, blockHeight),
            cornerRadius = cornerRadius,
            style = Stroke(width = 3.0f)
        )
    }

    /**
     * Draws a subtle warning indicator when dragging over an invalid drop coordinate.
     */
    fun drawInvalidHoverBlock(
        drawScope: DrawScope,
        topLeft: Offset,
        size: Size
    ) {
        val pad = size.width * 0.06f
        val blockWidth = size.width - pad * 2
        val blockHeight = size.height - pad * 2
        val blockTopLeft = Offset(topLeft.x + pad, topLeft.y + pad)
        val cornerRadius = CornerRadius(blockWidth * 0.22f, blockHeight * 0.22f)

        drawScope.drawRoundRect(
            color = Color(0x33FF4444),
            topLeft = blockTopLeft,
            size = Size(blockWidth, blockHeight),
            cornerRadius = cornerRadius,
            style = Fill
        )
        drawScope.drawRoundRect(
            color = Color(0x66FF4444),
            topLeft = blockTopLeft,
            size = Size(blockWidth, blockHeight),
            cornerRadius = cornerRadius,
            style = Stroke(width = 2.0f)
        )
    }
}
