package fr.uha.hassenforder.easy.minesweeper.database

class FeedDatabase (
    private val db : MineSweeperDatabase
) {

    @Suppress("unused")
    suspend fun populate(mode : Int) {
    }

    fun clear() {
        db.clearAllTables()
    }

}