package fr.uha.hassenforder.easy.minesweeper.model

/*
 * Hidden is a cell hiding its content (can be a mine or not)
 * Revealed is a revealed mined cell (can be a exploded or just to show the bomb)
 * Neighbour is a revealed free cell (gives the count of mined neighbour )
 * Should is still a hidden cell : reality is about the mine and expected about user expectation
 */
sealed class CellState () {
    data class Hidden(val mined: Boolean) : CellState()
    data class Revealed(val exploded: Boolean) : CellState()
    data class Neighbour(val count: Int) : CellState()
    data class ShouldMined(val reality: Boolean) : CellState()
    data class ShouldEmpty(val reality: Boolean) : CellState()

    fun isMined(): Boolean {
        return when (this) {
            is Hidden -> mined
            is Neighbour -> false
            is Revealed -> true
            is ShouldEmpty -> reality
            is ShouldMined -> reality
        }
    }

}
