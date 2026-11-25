package fr.uha.hassenforder.easy.minesweeper.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "cells")
@Immutable
data class Cell (
    @PrimaryKey(autoGenerate = true)
    val cid: Long = 0,
    val pid: Long = 0,
    val x : Int = 0,
    val y : Int = 0,
    val state: CellState = CellState.Hidden(false),
) {
    @Ignore
    val key : Int = buildKey(this.x, this.y)

    companion object {
        fun buildKey (x : Int, y : Int) : Int {
            return 1000 * y + x
        }
    }
}
