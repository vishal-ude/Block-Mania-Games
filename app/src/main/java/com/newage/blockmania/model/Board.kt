package com.newage.blockmania.model

const val BOARD_SIZE = 8

/**
 * Immutable 8x8 puzzle board.
 */
data class Board(
    val cells: List<List<BlockColor?>> = List(BOARD_SIZE) { List(BOARD_SIZE) { null } }
) {
    init {
        require(cells.size == BOARD_SIZE) { "Board must have $BOARD_SIZE rows" }
        require(cells.all { it.size == BOARD_SIZE }) { "Board must have $BOARD_SIZE columns" }
    }

    operator fun get(row: Int, col: Int): BlockColor? {
        if (row in 0 until BOARD_SIZE && col in 0 until BOARD_SIZE) {
            return cells[row][col]
        }
        return null
    }

    fun isOccupied(row: Int, col: Int): Boolean = get(row, col) != null

    fun isEmpty(row: Int, col: Int): Boolean = get(row, col) == null

    /**
     * Returns a new board with the specified cell updated.
     */
    fun withCell(row: Int, col: Int, color: BlockColor?): Board {
        if (row !in 0 until BOARD_SIZE || col !in 0 until BOARD_SIZE) return this
        val newCells = cells.mapIndexed { r, rowList ->
            if (r == row) {
                rowList.mapIndexed { c, currentColor ->
                    if (c == col) color else currentColor
                }
            } else {
                rowList
            }
        }
        return Board(newCells)
    }

    /**
     * Batch update multiple cells for high efficiency.
     */
    fun withPlacements(placements: List<Triple<Int, Int, BlockColor>>): Board {
        val placementMap = placements.associateBy({ Pair(it.first, it.second) }, { it.third })
        val newCells = cells.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, currentColor ->
                val newColor = placementMap[Pair(r, c)]
                if (newColor != null) newColor else currentColor
            }
        }
        return Board(newCells)
    }

    /**
     * Clears specific rows and columns simultaneously.
     */
    fun clearLines(clearedRows: Set<Int>, clearedCols: Set<Int>): Board {
        if (clearedRows.isEmpty() && clearedCols.isEmpty()) return this
        val newCells = cells.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, currentColor ->
                if (r in clearedRows || c in clearedCols) null else currentColor
            }
        }
        return Board(newCells)
    }

    companion object {
        val EMPTY = Board()
    }
}
