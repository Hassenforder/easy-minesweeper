package fr.uha.hassenforder.easy.minesweeper.dto

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation
import fr.uha.hassenforder.easy.minesweeper.model.Cell
import fr.uha.hassenforder.easy.minesweeper.model.Puzzle

@Immutable
data class FullPuzzle (
    @Embedded
    val puzzle: Puzzle,

    @Relation(parentColumn = "pid", entityColumn = "pid")
    val cells: List<Cell>,

)