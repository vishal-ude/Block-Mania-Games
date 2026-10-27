package com.newage.blockmania.engine

import com.newage.blockmania.model.BlockColor
import com.newage.blockmania.model.BlockShape
import com.newage.blockmania.model.Piece
import com.newage.blockmania.model.ShapeCategory
import kotlin.random.Random

/**
 * Generates fair, varied sets of 3 playable block pieces.
 */
class BlockGenerator(private val random: Random = Random.Default) {

    private val smallShapes = BlockShape.ALL_SHAPES.filter { it.category == ShapeCategory.SMALL }
    private val mediumShapes = BlockShape.ALL_SHAPES.filter { it.category == ShapeCategory.MEDIUM }
    private val lineShapes = BlockShape.ALL_SHAPES.filter { it.category == ShapeCategory.LINE }
    private val largeShapes = BlockShape.ALL_SHAPES.filter { it.category == ShapeCategory.LARGE }

    /**
     * Generates a balanced set of 3 pieces with varied shapes and colors.
     * Guarantees fair gameplay by preventing multiple huge shapes simultaneously.
     */
    fun generateHand(): List<Piece> {
        val selectedShapes = mutableListOf<BlockShape>()

        // 1. First piece: flexible/small or medium
        val firstPool = if (random.nextBoolean()) smallShapes else mediumShapes
        selectedShapes.add(firstPool.random(random))

        // 2. Second piece: medium or line
        val secondPool = if (random.nextBoolean()) mediumShapes else lineShapes
        val secondCandidates = secondPool.filter { it.name != selectedShapes.first().name }
        selectedShapes.add(
            if (secondCandidates.isNotEmpty()) secondCandidates.random(random) else secondPool.random(random)
        )

        // 3. Third piece: can be large, line, or medium (at most 1 large in the hand)
        val thirdPool = when (random.nextInt(3)) {
            0 -> largeShapes
            1 -> smallShapes
            else -> mediumShapes
        }
        val thirdCandidates = thirdPool.filter { shape -> selectedShapes.none { it.name == shape.name } }
        selectedShapes.add(
            if (thirdCandidates.isNotEmpty()) thirdCandidates.random(random) else thirdPool.random(random)
        )

        // Assign distinct vibrant colors to each piece in the hand
        val colorPool = BlockColor.entries.shuffled(random).toMutableList()

        return selectedShapes.map { shape ->
            val color = if (colorPool.isNotEmpty()) colorPool.removeAt(0) else BlockColor.random()
            Piece(
                shape = shape,
                color = color
            )
        }
    }
}
