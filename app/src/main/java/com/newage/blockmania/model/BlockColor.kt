package com.newage.blockmania.model

import androidx.compose.ui.graphics.Color

/**
 * Child-friendly vibrant color palette for Block Mania blocks.
 * Each color includes highlight, primary, and shadow shades for a rich 3D beveled appearance.
 */
enum class BlockColor(
    val primary: Color,
    val highlight: Color,
    val shadow: Color,
    val glow: Color
) {
    CORAL(
        primary = Color(0xFFFF4B6E),
        highlight = Color(0xFFFF8DA3),
        shadow = Color(0xFFC71F42),
        glow = Color(0x66FF4B6E)
    ),
    ORANGE(
        primary = Color(0xFFFF8B1F),
        highlight = Color(0xFFFFB86B),
        shadow = Color(0xFFD66000),
        glow = Color(0x66FF8B1F)
    ),
    GOLD(
        primary = Color(0xFFFFC700),
        highlight = Color(0xFFFFDF6B),
        shadow = Color(0xFFD49E00),
        glow = Color(0x66FFC700)
    ),
    EMERALD(
        primary = Color(0xFF10B981),
        highlight = Color(0xFF6EE7B7),
        shadow = Color(0xFF047857),
        glow = Color(0x6610B981)
    ),
    CYAN(
        primary = Color(0xFF06B6D4),
        highlight = Color(0xFF67E8F9),
        shadow = Color(0xFF0E7490),
        glow = Color(0x6606B6D4)
    ),
    BLUE(
        primary = Color(0xFF3B82F6),
        highlight = Color(0xFF93C5FD),
        shadow = Color(0xFF1D4ED8),
        glow = Color(0x663B82F6)
    ),
    PURPLE(
        primary = Color(0xFF8B5CF6),
        highlight = Color(0xFFC4B5FD),
        shadow = Color(0xFF5B21B6),
        glow = Color(0x668B5CF6)
    ),
    MAGENTA(
        primary = Color(0xFFEC4899),
        highlight = Color(0xFFF472B6),
        shadow = Color(0xFFBE185D),
        glow = Color(0x66EC4899)
    );

    companion object {
        fun random(exclude: BlockColor? = null): BlockColor {
            val pool = if (exclude != null) entries.filter { it != exclude } else entries
            return pool.random()
        }
    }
}
