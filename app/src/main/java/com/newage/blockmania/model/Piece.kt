package com.newage.blockmania.model

import java.util.UUID

/**
 * An immutable playable piece shown in the piece tray.
 */
data class Piece(
    val id: String = UUID.randomUUID().toString(),
    val shape: BlockShape,
    val color: BlockColor,
    val isUsed: Boolean = false,
    val canFit: Boolean = true
) {
    val rowCount: Int get() = shape.rowCount
    val colCount: Int get() = shape.colCount
    val cellCount: Int get() = shape.cellCount

    operator fun get(row: Int, col: Int): Boolean = shape[row, col]
}
