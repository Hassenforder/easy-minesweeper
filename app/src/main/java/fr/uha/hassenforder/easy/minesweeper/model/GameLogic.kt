package fr.uha.hassenforder.easy.minesweeper.model

import android.util.Log
import fr.uha.hassenforder.easy.minesweeper.model.CellState.*
import fr.uha.hassenforder.easy.minesweeper.ui.puzzle.GameMode
import kotlin.random.Random

class GameLogic (
    private val puzzle : Puzzle,
    private val cells : List<Cell>
) {
    private val grid : MutableMap<Int, Cell> by lazy {
        cells.associateBy { it.key }.toMutableMap()
    }

    private val changeList = mutableMapOf<Int, Cell>()

    private var gameState = PuzzleState.CREATED

    private fun replaceGrid (cell : Cell) {
        grid.put(cell.key, cell)
        changeList.put(cell.key, cell)
    }

    fun create (pid : Long) : List<Cell> {
        for (i in 0..<puzzle.width) {
            for (j in 0..<puzzle.height) {
                val cell = Cell(0, pid, i, j, Hidden(false))
                replaceGrid(cell)
            }
        }
        var count = puzzle.mines
        while (count > 0) {
            val x: Int = Random.nextInt(puzzle.width)
            val y: Int = Random.nextInt(puzzle.height)
            if (isMined(x, y)) continue
            val cell = Cell(0, pid, x, y, Hidden(true))
            replaceGrid(cell)
            --count
        }
        return changeList.values.toList()
    }

    private fun revealAll () {
        for (i in 0..<puzzle.width) {
            for (j in 0..<puzzle.height) {
                val original = grid[Cell.buildKey(i, j)]
                if (original == null) continue
                when (original.state) {
                    is Hidden -> {
                        if (original.state.mined) {
                            replaceGrid(original.copy(state = Revealed(exploded = false)))
                        } else {
                            replaceGrid(original.copy(state = Neighbour(count = getMinedNeighbours(cell = original))))
                        }
                    }
                    is Neighbour -> { }
                    is Revealed -> { }
                    is ShouldEmpty -> {
                        if (original.state.reality) {
                            replaceGrid(original.copy(state = Revealed(exploded = false)))
                        } else {
                            replaceGrid(original.copy(state = Neighbour(count = getMinedNeighbours(cell = original))))
                        }
                    }
                    is ShouldMined -> {
                        if (original.state.reality) {
                            replaceGrid(original.copy(state = Revealed(exploded = false)))
                        } else {
                            replaceGrid(original.copy(state = Neighbour(count = getMinedNeighbours(cell = original))))
                        }
                    }
                }
            }
        }
    }

    private fun checkWin(): Boolean {
        val guessed = grid.values.count { it.state is ShouldMined && it.state.reality }
        return guessed == puzzle.mines
    }

    private fun isMined(x : Int, y : Int) : Boolean {
        val cell = grid[Cell.buildKey(x, y)]
        if (cell == null) {
            Log.d("isMined", "cell $x $y is null")
            return false
        }
        return cell.state.isMined()
    }

    private fun getMinedNeighbours(cell: Cell): Int {
        var count = 0
        if (isMined(cell.x - 1, cell.y - 1)) ++count
        if (isMined(cell.x - 1, cell.y)) ++count
        if (isMined(cell.x - 1, cell.y + 1)) ++count
        if (isMined(cell.x, cell.y - 1)) ++count
        if (isMined(cell.x, cell.y + 1)) ++count
        if (isMined(cell.x + 1, cell.y - 1)) ++count
        if (isMined(cell.x + 1, cell.y)) ++count
        if (isMined(cell.x + 1, cell.y + 1)) ++count
        return count
    }

    private fun revealNeighbours(x : Int, y : Int) {
        val cell = grid[Cell.buildKey(x, y)]
        if (cell == null) return
        if (cell.state is Hidden && !cell.state.mined) {
            val count = getMinedNeighbours(cell = cell)
            replaceGrid(cell.copy(state = Neighbour(count = count)))
            if (count != 0) return
            revealNeighbours (cell.x - 1, cell.y - 1)
            revealNeighbours (cell.x - 1, cell.y)
            revealNeighbours (cell.x - 1, cell.y + 1)
            revealNeighbours (cell.x, cell.y - 1)
            revealNeighbours (cell.x, cell.y + 1)
            revealNeighbours (cell.x + 1, cell.y - 1)
            revealNeighbours (cell.x + 1, cell.y)
            revealNeighbours (cell.x + 1, cell.y + 1)
        }
    }

    private fun reveal(cell: Cell) {
        if (cell.state.isMined()) {
            revealAll ()
            replaceGrid(cell.copy(state = Revealed(exploded = true)))
            gameState = PuzzleState.LOST
        } else {
            revealNeighbours (cell.x, cell.y)
            if (checkWin()) {
                revealAll ()
                gameState = PuzzleState.WON
            }
        }
    }

    private fun changeToRevealed (cell : Cell) {
        when (cell.state) {
            is Hidden -> {
                reveal (cell)
            }
            is Neighbour -> { }
            is Revealed -> { }
            is ShouldEmpty -> {
                reveal (cell)
            }
            is ShouldMined -> {
                reveal (cell)
            }
        }
    }

    private fun changeToShouldMined (cell : Cell) {
        when (cell.state) {
            is Hidden -> {
                replaceGrid(cell.copy(state = ShouldMined(reality = cell.state.mined)))
                if (checkWin()) {
                    revealAll ()
                    gameState = PuzzleState.WON
                }
            }
            is Neighbour -> { }
            is Revealed -> { }
            is ShouldEmpty -> {
                replaceGrid(cell.copy(state = ShouldMined(reality = cell.state.reality)))
                if (checkWin()) {
                    revealAll ()
                    gameState = PuzzleState.WON
                }
            }
            is ShouldMined -> {
                replaceGrid(cell.copy(state = Hidden(mined = cell.state.reality)))
            }
        }
    }

    private fun changeToShouldFree (cell : Cell) {
        when (cell.state) {
            is Hidden -> {
                replaceGrid(cell.copy(state = ShouldEmpty(reality = cell.state.mined)))
            }
            is Neighbour -> { }
            is Revealed -> { }
            is ShouldEmpty -> {
                replaceGrid(cell.copy(state = Hidden(mined = cell.state.reality)))
            }
            is ShouldMined -> {
                replaceGrid(cell.copy(state = ShouldEmpty(reality = cell.state.reality)))
            }
        }
    }

    fun computeChanges (cell : Cell, gameMode : GameMode) : Pair<PuzzleState, Collection<Cell>> {
        when (gameMode) {
            GameMode.REVEAL -> changeToRevealed(cell)
            GameMode.SHOULD_MINED -> changeToShouldMined(cell)
            GameMode.SHOULD_FREE -> changeToShouldFree(cell)
        }
        return gameState to changeList.values
    }

    companion object {

        val defaultPuzzle = Puzzle(0, PuzzleSize.TINY, 3, 3, 1)

        val puzzles : Map<PuzzleSize, Puzzle> by lazy {
            mapOf<PuzzleSize, Puzzle>(
                PuzzleSize.TINY to Puzzle(0, PuzzleSize.TINY, 3, 3, 1),
                PuzzleSize.SMALL to Puzzle(0, PuzzleSize.SMALL, 9, 9, 10),
                PuzzleSize.MEDIUM to Puzzle(0, PuzzleSize.MEDIUM, 16, 16, 40),
                PuzzleSize.LARGE to Puzzle(0, PuzzleSize.LARGE, 16, 30, 99),
                PuzzleSize.CUSTOM to Puzzle(0, PuzzleSize.CUSTOM, 16, 16, 40),
            )
        }

    }

}