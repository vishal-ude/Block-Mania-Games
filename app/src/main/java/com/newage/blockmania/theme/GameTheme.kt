package com.newage.blockmania.theme

import androidx.compose.ui.graphics.Color
import com.newage.blockmania.model.BlockColor

/**
 * 3D shading color set for a puzzle block in a specific theme.
 */
data class BlockThemeColors(
    val primary: Color,
    val highlight: Color,
    val shadow: Color,
    val glow: Color
)

/**
 * The four distinct visual themes for Block Mania:
 * 1. Candy Pop - pink, purple, coral, and cheerful pastel colors
 * 2. Ocean Blue - blue, cyan, and teal colors
 * 3. Galaxy - deep navy, violet, and glowing purple accents
 * 4. Emerald - green, mint, and soft turquoise colors
 */
enum class GameTheme(
    val id: String,
    val displayName: String,
    val description: String,
    val previewColors: List<Color>,
    val backgroundGradient: List<Color>,
    val ambientGlowColors: List<Color>,
    val boardBackground: Color,
    val boardBorder: Color,
    val emptyCellColor: Color,
    val emptyCellBorder: Color,
    val cardBackground: List<Color>,
    val cardBorder: Color,
    val scoreAccent: Color,
    val bestScoreAccent: Color,
    val primaryButton: Color,
    val secondaryButton: Color,
    val secondaryButtonBorder: Color,
    val trayBackground: List<Color>,
    val trayBorder: Color,
    val slotBackground: Color,
    val slotBorder: Color,
    val lineClearGlow: Color,
    val dialogBackground: Color,
    val dialogBorder: Color
) {
    CANDY_POP(
        id = "candy_pop",
        displayName = "Candy Pop",
        description = "Sweet pink, purple, coral & pastels",
        previewColors = listOf(Color(0xFFFF6584), Color(0xFFA855F7), Color(0xFFFF6B6B), Color(0xFF38BDF8)),
        backgroundGradient = listOf(Color(0xFF26122C), Color(0xFF35173B), Color(0xFF1B0B20)),
        ambientGlowColors = listOf(Color(0xFFFF6584), Color(0xFFA855F7), Color(0xFFFF6B6B), Color(0xFF34D399)),
        boardBackground = Color(0xFF2B1432),
        boardBorder = Color(0xFF752C7C),
        emptyCellColor = Color(0xFF220E28),
        emptyCellBorder = Color(0xFF421E4B),
        cardBackground = listOf(Color(0xFF3B1A43), Color(0xFF28112E)),
        cardBorder = Color(0xFF7B2F83),
        scoreAccent = Color(0xFFFF6584),
        bestScoreAccent = Color(0xFFFDE047),
        primaryButton = Color(0xFFEC4899),
        secondaryButton = Color(0xFF3B1A45),
        secondaryButtonBorder = Color(0xFF6B2B78),
        trayBackground = listOf(Color(0xFF2E1535), Color(0xFF200D26)),
        trayBorder = Color(0xFF6E2875),
        slotBackground = Color(0xFF1F0D24),
        slotBorder = Color(0xFF3E1A47),
        lineClearGlow = Color(0xFFFF6584),
        dialogBackground = Color(0xFF2A1331),
        dialogBorder = Color(0xFF6B2B78)
    ),

    OCEAN_BLUE(
        id = "ocean_blue",
        displayName = "Ocean Blue",
        description = "Deep blue, cyan & sea teal",
        previewColors = listOf(Color(0xFF00E5FF), Color(0xFF2563EB), Color(0xFF0D9488), Color(0xFF38BDF8)),
        backgroundGradient = listOf(Color(0xFF0A182F), Color(0xFF0E2448), Color(0xFF061021)),
        ambientGlowColors = listOf(Color(0xFF00E5FF), Color(0xFF2563EB), Color(0xFF0D9488), Color(0xFF14B8A6)),
        boardBackground = Color(0xFF0B1F3C),
        boardBorder = Color(0xFF1C4B78),
        emptyCellColor = Color(0xFF08172F),
        emptyCellBorder = Color(0xFF16375E),
        cardBackground = listOf(Color(0xFF102B52), Color(0xFF0B1D38)),
        cardBorder = Color(0xFF225C94),
        scoreAccent = Color(0xFF00E5FF),
        bestScoreAccent = Color(0xFF10B981),
        primaryButton = Color(0xFF0284C7),
        secondaryButton = Color(0xFF10284A),
        secondaryButtonBorder = Color(0xFF1F487E),
        trayBackground = listOf(Color(0xFF0C2140), Color(0xFF08162D)),
        trayBorder = Color(0xFF1C4D7E),
        slotBackground = Color(0xFF07142A),
        slotBorder = Color(0xFF133256),
        lineClearGlow = Color(0xFF00E5FF),
        dialogBackground = Color(0xFF0C2140),
        dialogBorder = Color(0xFF1F4D7E)
    ),

    GALAXY(
        id = "galaxy",
        displayName = "Galaxy",
        description = "Deep navy, violet & glowing purple",
        previewColors = listOf(Color(0xFFA55EEA), Color(0xFFFF4B6E), Color(0xFF00D2D3), Color(0xFFFFC700)),
        backgroundGradient = listOf(Color(0xFF16152B), Color(0xFF1E1B44), Color(0xFF111024)),
        ambientGlowColors = listOf(Color(0xFF00D2D3), Color(0xFFFF4B6E), Color(0xFFA55EEA), Color(0xFFFFC700)),
        boardBackground = Color(0xFF171B36),
        boardBorder = Color(0xFF3F3B7A),
        emptyCellColor = Color(0xFF12142B),
        emptyCellBorder = Color(0xFF282852),
        cardBackground = listOf(Color(0xFF26204C), Color(0xFF1B1738)),
        cardBorder = Color(0xFF4C3E8A),
        scoreAccent = Color(0xFFFFC72C),
        bestScoreAccent = Color(0xFF10B981),
        primaryButton = Color(0xFF10B981),
        secondaryButton = Color(0xFF28234D),
        secondaryButtonBorder = Color(0xFF433979),
        trayBackground = listOf(Color(0xFF1D1A3B), Color(0xFF15132C)),
        trayBorder = Color(0xFF3F3770),
        slotBackground = Color(0xFF141227),
        slotBorder = Color(0xFF2C2652),
        lineClearGlow = Color(0xFFFFD700),
        dialogBackground = Color(0xFF1E1A3C),
        dialogBorder = Color(0xFF3F3572)
    ),

    EMERALD(
        id = "emerald",
        displayName = "Emerald",
        description = "Lush green, mint & soft turquoise",
        previewColors = listOf(Color(0xFF10B981), Color(0xFF34D399), Color(0xFF06B6D4), Color(0xFF84CC16)),
        backgroundGradient = listOf(Color(0xFF081C16), Color(0xFF0E2C22), Color(0xFF051410)),
        ambientGlowColors = listOf(Color(0xFF10B981), Color(0xFF34D399), Color(0xFF06B6D4), Color(0xFF84CC16)),
        boardBackground = Color(0xFF0B241C),
        boardBorder = Color(0xFF1E5842),
        emptyCellColor = Color(0xFF081C15),
        emptyCellBorder = Color(0xFF173E2F),
        cardBackground = listOf(Color(0xFF123629), Color(0xFF0A2219)),
        cardBorder = Color(0xFF24684E),
        scoreAccent = Color(0xFF34D399),
        bestScoreAccent = Color(0xFFFDE047),
        primaryButton = Color(0xFF059669),
        secondaryButton = Color(0xFF12362A),
        secondaryButtonBorder = Color(0xFF235C47),
        trayBackground = listOf(Color(0xFF0D281E), Color(0xFF081C15)),
        trayBorder = Color(0xFF1E5641),
        slotBackground = Color(0xFF071912),
        slotBorder = Color(0xFF143B2C),
        lineClearGlow = Color(0xFF34D399),
        dialogBackground = Color(0xFF0D281E),
        dialogBorder = Color(0xFF1E5641)
    );

    companion object {
        fun fromId(id: String?): GameTheme {
            return entries.find { it.id == id } ?: GALAXY
        }
    }
}

/**
 * Returns the theme-specific colors for this BlockColor.
 */
fun BlockColor.themed(theme: GameTheme): BlockThemeColors {
    return when (theme) {
        GameTheme.CANDY_POP -> when (this) {
            BlockColor.CORAL -> BlockThemeColors(Color(0xFFFF6584), Color(0xFFFFA3B6), Color(0xFFCC3D5C), Color(0x66FF6584))
            BlockColor.ORANGE -> BlockThemeColors(Color(0xFFFB923C), Color(0xFFFDBA74), Color(0xFFEA580C), Color(0x66FB923C))
            BlockColor.GOLD -> BlockThemeColors(Color(0xFFFDE047), Color(0xFFFEF08A), Color(0xFFCA8A04), Color(0x66FDE047))
            BlockColor.EMERALD -> BlockThemeColors(Color(0xFF34D399), Color(0xFF6EE7B7), Color(0xFF059669), Color(0x6634D399))
            BlockColor.CYAN -> BlockThemeColors(Color(0xFF38BDF8), Color(0xFF7DD3FC), Color(0xFF0284C7), Color(0x6638BDF8))
            BlockColor.BLUE -> BlockThemeColors(Color(0xFF818CF8), Color(0xFFA5B4FC), Color(0xFF4F46E5), Color(0x66818CF8))
            BlockColor.PURPLE -> BlockThemeColors(Color(0xFFA855F7), Color(0xFFD8B4FE), Color(0xFF7E22CE), Color(0x66A855F7))
            BlockColor.MAGENTA -> BlockThemeColors(Color(0xFFF43F5E), Color(0xFFFB7185), Color(0xFFBE123C), Color(0x66F43F5E))
        }

        GameTheme.OCEAN_BLUE -> when (this) {
            BlockColor.CORAL -> BlockThemeColors(Color(0xFF00E5FF), Color(0xFF80F2FF), Color(0xFF00A3B5), Color(0x6600E5FF))
            BlockColor.ORANGE -> BlockThemeColors(Color(0xFF38BDF8), Color(0xFFBAE6FD), Color(0xFF0284C7), Color(0x6638BDF8))
            BlockColor.GOLD -> BlockThemeColors(Color(0xFF60A5FA), Color(0xFFBFDBFE), Color(0xFF2563EB), Color(0x6660A5FA))
            BlockColor.EMERALD -> BlockThemeColors(Color(0xFF0D9488), Color(0xFF2DD4BF), Color(0xFF0F766E), Color(0x660D9488))
            BlockColor.CYAN -> BlockThemeColors(Color(0xFF06B6D4), Color(0xFF67E8F9), Color(0xFF0891B2), Color(0x6606B6D4))
            BlockColor.BLUE -> BlockThemeColors(Color(0xFF2563EB), Color(0xFF93C5FD), Color(0xFF1D4ED8), Color(0x662563EB))
            BlockColor.PURPLE -> BlockThemeColors(Color(0xFF4F46E5), Color(0xFF818CF8), Color(0xFF3730A3), Color(0x664F46E5))
            BlockColor.MAGENTA -> BlockThemeColors(Color(0xFF14B8A6), Color(0xFF5EEAD4), Color(0xFF0D9488), Color(0x6614B8A6))
        }

        GameTheme.GALAXY -> when (this) {
            BlockColor.CORAL -> BlockThemeColors(Color(0xFFFF4B6E), Color(0xFFFF8DA3), Color(0xFFC71F42), Color(0x66FF4B6E))
            BlockColor.ORANGE -> BlockThemeColors(Color(0xFFFF8B1F), Color(0xFFFFB86B), Color(0xFFD66000), Color(0x66FF8B1F))
            BlockColor.GOLD -> BlockThemeColors(Color(0xFFFFC700), Color(0xFFFFDF6B), Color(0xFFD49E00), Color(0x66FFC700))
            BlockColor.EMERALD -> BlockThemeColors(Color(0xFF10B981), Color(0xFF6EE7B7), Color(0xFF047857), Color(0x6610B981))
            BlockColor.CYAN -> BlockThemeColors(Color(0xFF00D2D3), Color(0xFF55EFC4), Color(0xFF009899), Color(0x6600D2D3))
            BlockColor.BLUE -> BlockThemeColors(Color(0xFF3B82F6), Color(0xFF93C5FD), Color(0xFF1D4ED8), Color(0x663B82F6))
            BlockColor.PURPLE -> BlockThemeColors(Color(0xFFA55EEA), Color(0xFFD29EFC), Color(0xFF7B2CBF), Color(0x66A55EEA))
            BlockColor.MAGENTA -> BlockThemeColors(Color(0xFFEC4899), Color(0xFFF472B6), Color(0xFFBE185D), Color(0x66EC4899))
        }

        GameTheme.EMERALD -> when (this) {
            BlockColor.CORAL -> BlockThemeColors(Color(0xFF10B981), Color(0xFF6EE7B7), Color(0xFF047857), Color(0x6610B981))
            BlockColor.ORANGE -> BlockThemeColors(Color(0xFF84CC16), Color(0xFFBEF264), Color(0xFF4D7C0F), Color(0x6684CC16))
            BlockColor.GOLD -> BlockThemeColors(Color(0xFFEAB308), Color(0xFFFDE047), Color(0xFFA16207), Color(0x66EAB308))
            BlockColor.EMERALD -> BlockThemeColors(Color(0xFF059669), Color(0xFF34D399), Color(0xFF065F46), Color(0x66059669))
            BlockColor.CYAN -> BlockThemeColors(Color(0xFF06B6D4), Color(0xFF67E8F9), Color(0xFF0E7490), Color(0x6606B6D4))
            BlockColor.BLUE -> BlockThemeColors(Color(0xFF0D9488), Color(0xFF5EEAD4), Color(0xFF115E59), Color(0x660D9488))
            BlockColor.PURPLE -> BlockThemeColors(Color(0xFF34D399), Color(0xFFA7F3D0), Color(0xFF059669), Color(0x6634D399))
            BlockColor.MAGENTA -> BlockThemeColors(Color(0xFF2DD4BF), Color(0xFF99F6E4), Color(0xFF0F766E), Color(0x662DD4BF))
        }
    }
}
