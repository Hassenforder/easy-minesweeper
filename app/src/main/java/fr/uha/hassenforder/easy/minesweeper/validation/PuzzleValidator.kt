package fr.uha.hassenforder.easy.minesweeper.validation

import androidx.compose.runtime.Immutable
import fr.uha.hassenforder.easy.minesweeper.R
import fr.uha.hassenforder.easy.minesweeper.model.Puzzle
import fr.uha.hassenforder.easy.minesweeper.model.PuzzleSize

@Immutable
data class PuzzleValidationErrors (
    val size: Int?,
    val width: Int?,
    val height: Int?,
    val mines: Int?,
) {
    fun hasError () : Boolean {
        if (size != null) return true
        if (width != null) return true
        if (height != null) return true
        if (mines != null) return true
        return false
    }
}

class PuzzleValidator (
    val puzzle: Puzzle,
) {

    fun validateSize(newValue: PuzzleSize) : Int? {
        return null
    }

    private fun validateWidth(newValue: Int) : Int? {
        return when {
            newValue <  1 ->  R.string.value_too_short
            newValue > 99 ->  R.string.value_too_long
            else -> null
        }
    }

    private fun validateHeight(newValue: Int) : Int? {
        return when {
            newValue <  1 ->  R.string.value_too_short
            newValue > 99 ->  R.string.value_too_long
            else -> null
        }
    }

    private fun validateMines(newValue: Int) : Int? {
        val cellCount = puzzle.width * puzzle.height
        return when {
            newValue > cellCount /  5 ->  R.string.value_between
            newValue < cellCount / 20 ->  R.string.value_between
            else -> null
        }
    }

    fun validateAll () : PuzzleValidationErrors {
        return PuzzleValidationErrors (
            validateSize(puzzle.size),
            validateWidth(puzzle.width),
            validateHeight(puzzle.height),
            validateMines(puzzle.mines),
        )
    }
}