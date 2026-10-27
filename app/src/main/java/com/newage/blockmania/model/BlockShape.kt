package com.newage.blockmania.model

enum class ShapeCategory {
    SMALL,   // 1x1, 1x2, 2x1, 2x2 corner (1-3 cells)
    MEDIUM,  // 2x2 square, 1x3, 3x1, T-shapes, Z/S, L-shapes (4 cells)
    LINE,    // 1x4, 4x1, 1x5, 5x1
    LARGE    // 3x3 square, 3x3 big corner, 3x3 plus (5-9 cells)
}

data class BlockShape(
    val name: String,
    val category: ShapeCategory,
    val matrix: List<List<Boolean>>
) {
    val rowCount: Int = matrix.size
    val colCount: Int = if (matrix.isNotEmpty()) matrix[0].size else 0
    val cellCount: Int = matrix.sumOf { row -> row.count { it } }

    operator fun get(row: Int, col: Int): Boolean {
        if (row in 0 until rowCount && col in 0 until colCount) {
            return matrix[row][col]
        }
        return false
    }

    companion object {
        // --- 1-cell ---
        val DOT = BlockShape(
            name = "Dot",
            category = ShapeCategory.SMALL,
            matrix = listOf(listOf(true))
        )

        // --- 2-cell Lines ---
        val LINE_2_H = BlockShape(
            name = "Line 2H",
            category = ShapeCategory.SMALL,
            matrix = listOf(listOf(true, true))
        )
        val LINE_2_V = BlockShape(
            name = "Line 2V",
            category = ShapeCategory.SMALL,
            matrix = listOf(listOf(true), listOf(true))
        )

        // --- 3-cell Lines ---
        val LINE_3_H = BlockShape(
            name = "Line 3H",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(listOf(true, true, true))
        )
        val LINE_3_V = BlockShape(
            name = "Line 3V",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(listOf(true), listOf(true), listOf(true))
        )

        // --- 4-cell Lines ---
        val LINE_4_H = BlockShape(
            name = "Line 4H",
            category = ShapeCategory.LINE,
            matrix = listOf(listOf(true, true, true, true))
        )
        val LINE_4_V = BlockShape(
            name = "Line 4V",
            category = ShapeCategory.LINE,
            matrix = listOf(listOf(true), listOf(true), listOf(true), listOf(true))
        )

        // --- 5-cell Lines ---
        val LINE_5_H = BlockShape(
            name = "Line 5H",
            category = ShapeCategory.LINE,
            matrix = listOf(listOf(true, true, true, true, true))
        )
        val LINE_5_V = BlockShape(
            name = "Line 5V",
            category = ShapeCategory.LINE,
            matrix = listOf(listOf(true), listOf(true), listOf(true), listOf(true), listOf(true))
        )

        // --- 2x2 Square ---
        val SQUARE_2X2 = BlockShape(
            name = "Square 2x2",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(
                listOf(true, true),
                listOf(true, true)
            )
        )

        // --- 3x3 Square ---
        val SQUARE_3X3 = BlockShape(
            name = "Square 3x3",
            category = ShapeCategory.LARGE,
            matrix = listOf(
                listOf(true, true, true),
                listOf(true, true, true),
                listOf(true, true, true)
            )
        )

        // --- 2x2 Small Corners (3 cells) ---
        val CORNER_2X2_TL = BlockShape(
            name = "Corner 2x2 TL",
            category = ShapeCategory.SMALL,
            matrix = listOf(
                listOf(true, true),
                listOf(true, false)
            )
        )
        val CORNER_2X2_TR = BlockShape(
            name = "Corner 2x2 TR",
            category = ShapeCategory.SMALL,
            matrix = listOf(
                listOf(true, true),
                listOf(false, true)
            )
        )
        val CORNER_2X2_BL = BlockShape(
            name = "Corner 2x2 BL",
            category = ShapeCategory.SMALL,
            matrix = listOf(
                listOf(true, false),
                listOf(true, true)
            )
        )
        val CORNER_2X2_BR = BlockShape(
            name = "Corner 2x2 BR",
            category = ShapeCategory.SMALL,
            matrix = listOf(
                listOf(false, true),
                listOf(true, true)
            )
        )

        // --- 3x3 Big Corners (5 cells) ---
        val CORNER_3X3_TL = BlockShape(
            name = "Corner 3x3 TL",
            category = ShapeCategory.LARGE,
            matrix = listOf(
                listOf(true, true, true),
                listOf(true, false, false),
                listOf(true, false, false)
            )
        )
        val CORNER_3X3_TR = BlockShape(
            name = "Corner 3x3 TR",
            category = ShapeCategory.LARGE,
            matrix = listOf(
                listOf(true, true, true),
                listOf(false, false, true),
                listOf(false, false, true)
            )
        )
        val CORNER_3X3_BL = BlockShape(
            name = "Corner 3x3 BL",
            category = ShapeCategory.LARGE,
            matrix = listOf(
                listOf(true, false, false),
                listOf(true, false, false),
                listOf(true, true, true)
            )
        )
        val CORNER_3X3_BR = BlockShape(
            name = "Corner 3x3 BR",
            category = ShapeCategory.LARGE,
            matrix = listOf(
                listOf(false, false, true),
                listOf(false, false, true),
                listOf(true, true, true)
            )
        )

        // --- Classic L-Shapes (4 cells) ---
        val L_3X2_BL = BlockShape(
            name = "L 3x2 BL",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(
                listOf(true, false),
                listOf(true, false),
                listOf(true, true)
            )
        )
        val L_3X2_BR = BlockShape(
            name = "L 3x2 BR",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(
                listOf(false, true),
                listOf(false, true),
                listOf(true, true)
            )
        )
        val L_2X3_TL = BlockShape(
            name = "L 2x3 TL",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(
                listOf(true, true, true),
                listOf(true, false, false)
            )
        )
        val L_2X3_TR = BlockShape(
            name = "L 2x3 TR",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(
                listOf(true, true, true),
                listOf(false, false, true)
            )
        )

        // --- T-Shapes (4 cells) ---
        val T_UP = BlockShape(
            name = "T Up",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(
                listOf(false, true, false),
                listOf(true, true, true)
            )
        )
        val T_DOWN = BlockShape(
            name = "T Down",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(
                listOf(true, true, true),
                listOf(false, true, false)
            )
        )
        val T_LEFT = BlockShape(
            name = "T Left",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(
                listOf(false, true),
                listOf(true, true),
                listOf(false, true)
            )
        )
        val T_RIGHT = BlockShape(
            name = "T Right",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(
                listOf(true, false),
                listOf(true, true),
                listOf(true, false)
            )
        )

        // --- Z & S Shapes (4 cells) ---
        val Z_H = BlockShape(
            name = "Z Horizontal",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(
                listOf(true, true, false),
                listOf(false, true, true)
            )
        )
        val Z_V = BlockShape(
            name = "Z Vertical",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(
                listOf(false, true),
                listOf(true, true),
                listOf(true, false)
            )
        )
        val S_H = BlockShape(
            name = "S Horizontal",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(
                listOf(false, true, true),
                listOf(true, true, false)
            )
        )
        val S_V = BlockShape(
            name = "S Vertical",
            category = ShapeCategory.MEDIUM,
            matrix = listOf(
                listOf(true, false),
                listOf(true, true),
                listOf(false, true)
            )
        )

        // --- Plus Shape (5 cells) ---
        val PLUS = BlockShape(
            name = "Plus",
            category = ShapeCategory.LARGE,
            matrix = listOf(
                listOf(false, true, false),
                listOf(true, true, true),
                listOf(false, true, false)
            )
        )

        val ALL_SHAPES: List<BlockShape> = listOf(
            DOT,
            LINE_2_H, LINE_2_V,
            LINE_3_H, LINE_3_V,
            LINE_4_H, LINE_4_V,
            LINE_5_H, LINE_5_V,
            SQUARE_2X2, SQUARE_3X3,
            CORNER_2X2_TL, CORNER_2X2_TR, CORNER_2X2_BL, CORNER_2X2_BR,
            CORNER_3X3_TL, CORNER_3X3_TR, CORNER_3X3_BL, CORNER_3X3_BR,
            L_3X2_BL, L_3X2_BR, L_2X3_TL, L_2X3_TR,
            T_UP, T_DOWN, T_LEFT, T_RIGHT,
            Z_H, Z_V, S_H, S_V,
            PLUS
        )
    }
}
