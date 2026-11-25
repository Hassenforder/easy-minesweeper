package fr.uha.hassenforder.easy.minesweeper.repository

import androidx.annotation.WorkerThread
import fr.uha.hassenforder.easy.minesweeper.database.dao.PuzzleDao
import fr.uha.hassenforder.easy.minesweeper.database.dao.PuzzleDao.PuzzleUpdate
import fr.uha.hassenforder.easy.minesweeper.dto.FullPuzzle
import fr.uha.hassenforder.easy.minesweeper.model.Cell
import fr.uha.hassenforder.easy.minesweeper.model.GameLogic
import fr.uha.hassenforder.easy.minesweeper.model.Puzzle
import fr.uha.hassenforder.easy.minesweeper.model.PuzzleSize
import fr.uha.hassenforder.easy.minesweeper.model.PuzzleState
import fr.uha.hassenforder.easy.minesweeper.ui.puzzle.GameMode
import fr.uha.hassenforder.easy.minesweeper.ui.puzzle.ListPuzzlesViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.time.Clock

enum class ListMode {
    ALL,
    FINISHED,
    IN_PROGRESS,
}

class PuzzleRepository (
    private val dispatcher: CoroutineDispatcher,
    private val puzzleDao: PuzzleDao,
) {
    fun getAll(mode : ListMode): Flow<List<Puzzle>> {
        return when(mode) {
            ListMode.ALL -> puzzleDao.getAll()
            ListMode.FINISHED -> puzzleDao.getFinishedOnly()
            ListMode.IN_PROGRESS -> puzzleDao.getNotFinishedOnly()
        }
    }

    fun getById(id: Long): Flow<FullPuzzle?> {
        return puzzleDao.getById(id)
    }

    @WorkerThread
    suspend fun update(content : PuzzleUpdate) = withContext(dispatcher) {
        when (content) {
            is PuzzleUpdate.State -> puzzleDao.update(content)
        }
    }

    @WorkerThread
    suspend fun create(puzzle: Puzzle): Long = withContext(dispatcher) {
        val updatedPuzzle = puzzle.copy(
            state = PuzzleState.CREATED,
            start = Clock.System.now(),
            offset = 0L
        )
        val pid = puzzleDao.create(updatedPuzzle)
        val gameLogic = GameLogic(puzzle, emptyList())
        val cells = gameLogic.create(pid)
        if (! cells.isEmpty()) {
            puzzleDao.createCells(cells)
        }
        pid
    }

    @WorkerThread
    suspend fun delete(puzzle: Puzzle) = withContext(dispatcher) {
//        puzzleDao.deleteCells(puzzle.cells)
        puzzleDao.delete(puzzle)
    }

    @WorkerThread
    suspend fun load(pid: Long): Long = pid

    @WorkerThread
    suspend fun updateCell(puzzle : FullPuzzle, cell: Cell, gameMode: GameMode) = withContext(dispatcher) {
        val gameLogic = GameLogic(puzzle.puzzle, puzzle.cells)
        val result = gameLogic.computeChanges(cell, gameMode)
        if (result.first != puzzle.puzzle.state) {
            val elapsedFromStart = Clock.System.now().minus(puzzle.puzzle.start)
            puzzleDao.update(PuzzleUpdate.State(
            puzzle.puzzle.pid,
            result.first,
            puzzle.puzzle.offset + elapsedFromStart.inWholeMilliseconds,
                )
            )
        }
        if (! result.second.isEmpty()) {
            puzzleDao.updateCells(result.second.toList())
        }
    }

    fun getPuzzles(size: PuzzleSize) : Puzzle {
        return GameLogic.puzzles[size] ?: GameLogic.defaultPuzzle
    }

}
