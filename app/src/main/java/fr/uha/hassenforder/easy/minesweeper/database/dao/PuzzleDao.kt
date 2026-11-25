package fr.uha.hassenforder.easy.minesweeper.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import fr.uha.hassenforder.easy.minesweeper.dto.FullPuzzle
import fr.uha.hassenforder.easy.minesweeper.model.Cell
import fr.uha.hassenforder.easy.minesweeper.model.Puzzle
import fr.uha.hassenforder.easy.minesweeper.model.PuzzleState
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.Instant

@Dao
abstract class PuzzleDao {

    sealed class PuzzleUpdate {
        data class State(val pid : Long, val state: PuzzleState, val offset: Long, val start: Instant = Clock.System.now()) : PuzzleUpdate()
    }

    @Transaction
    @Query("SELECT P.* " +
            "FROM puzzles AS P " +
            "ORDER BY P.created DESC "
    )
    abstract fun getAll(): Flow<List<Puzzle>>

    @Transaction
    @Query("SELECT P.* " +
            "FROM puzzles AS P " +
            "WHERE P.state != 'CREATED' " +
            "ORDER BY P.created DESC "
    )
    abstract fun getFinishedOnly(): Flow<List<Puzzle>>

    @Transaction
    @Query("SELECT P.* " +
            "FROM puzzles AS P " +
            "WHERE P.state == 'CREATED' " +
            "ORDER BY P.created DESC "
    )
    abstract fun getNotFinishedOnly(): Flow<List<Puzzle>>

    @Transaction
    @Query("SELECT P.* " +
            "FROM puzzles AS P " +
            "WHERE P.pid = :id "
    )
    abstract fun getById(id : Long): Flow<FullPuzzle?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun create(puzzle: Puzzle): Long

    @Delete
    abstract suspend fun delete(puzzle: Puzzle)

    @Update(entity = Puzzle::class)
    abstract suspend fun update(content : PuzzleUpdate.State)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun createCells(cells : List<Cell>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun updateCells(cells : List<Cell>)

    @Delete
    abstract suspend fun deleteCells(cells : List<Cell>)

}