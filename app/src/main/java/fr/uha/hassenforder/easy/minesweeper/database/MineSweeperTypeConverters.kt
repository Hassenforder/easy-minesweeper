package fr.uha.hassenforder.easy.minesweeper.database

import androidx.room.TypeConverter
import fr.uha.hassenforder.easy.minesweeper.model.CellState
import kotlin.time.Instant

object MineSweeperTypeConverters {

    @TypeConverter
    fun int2CellState(state:Int): CellState {
        return when (state and MAJOR_MASK) {
            MAJOR_HIDDEN ->
                CellState.Hidden(state and MINOR_MINED != 0)
            MAJOR_REVEALED ->
                CellState.Revealed(state and MINOR_MINED != 0)
            MAJOR_NEIGHBOUR ->
                CellState.Neighbour(state and 0x0F)
            MAJOR_SHOULD_EMPTY ->
                CellState.ShouldEmpty(((state and MINOR_MINED) != 0))
            MAJOR_SHOULD_MINED ->
                CellState.ShouldMined(((state and MINOR_MINED) != 0))
            else -> throw IllegalArgumentException("Unknown state: $state")
        }
    }

    @TypeConverter
    fun cellState2int(state: CellState): Int {
        return when (state) {
            is CellState.Hidden -> {
                MAJOR_HIDDEN + if (state.mined) MINOR_MINED else 0
            }
            is CellState.Revealed -> {
                MAJOR_REVEALED + if (state.exploded) MINOR_MINED else 0
            }
            is CellState.Neighbour -> {
                MAJOR_NEIGHBOUR + state.count
            }
            is CellState.ShouldEmpty -> {
                MAJOR_SHOULD_EMPTY + (if (state.reality) MINOR_MINED else 0)
            }
            is CellState.ShouldMined -> {
                MAJOR_SHOULD_MINED + (if (state.reality) MINOR_MINED else 0)
            }
        }
    }

    private const val MAJOR_MASK : Int = 0xf0
    private const val MAJOR_HIDDEN : Int = 0x10
    private const val MAJOR_REVEALED : Int = 0x20
    private const val MAJOR_NEIGHBOUR : Int = 0x30
    private const val MAJOR_SHOULD_EMPTY : Int = 0x40
    private const val MAJOR_SHOULD_MINED : Int = 0x80

    private const val MINOR_MINED : Int = 0x01

}