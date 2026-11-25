package fr.uha.hassenforder.easy.minesweeper.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlin.time.Instant


/*
 * create is the creation day
 * start (of HourGlass) and offset (in Milliseconds) is for time taken
 * at creation :
 *    start is DISTANT_PAST, and 'initial' offset is 0
 *    the game is not played anyway
 * game is loaded
 *    if (start == DISTANT_PAST) updateWith (Instant.now)
 *    now the game is started
 *    elapsed time is : offset + (now - start)
 * game is paused or back :
 *    updateWith (Instant.Min, offset + current - start),
 *    offset is updated to the elapsed time from now
 *    start is DISTANT_PAST (irrelevant from now)
 * game is restarted :
 *    if (start == DISTANT_PAST) updateWith (Instant.now)
 *    now the game is restarted with the fresh start time
 *    elapsed time is : offset + (now - start)
 *
 * result => elapsed time can be cumulated until the puzzle is finished
 *
 */
@Entity(tableName = "puzzles")
@Immutable
data class Puzzle(
    @PrimaryKey(autoGenerate = true)
    val pid : Long,
    val size: PuzzleSize,
    val width : Int,
    val height : Int,
    val mines : Int,
    val state : PuzzleState = PuzzleState.CREATED,
    val created : Instant = Clock.System.now(),
    val start : Instant = Instant.DISTANT_PAST,
    val offset : Long = 0L
)
