package fr.uha.hassenforder.easy.minesweeper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.uha.hassenforder.easy.android.database.DatabaseTypeConverters
import fr.uha.hassenforder.easy.minesweeper.database.dao.PuzzleDao
import fr.uha.hassenforder.easy.minesweeper.model.Puzzle
import fr.uha.hassenforder.easy.minesweeper.model.Cell

@TypeConverters(DatabaseTypeConverters::class, MineSweeperTypeConverters::class)
@Database(
    entities = [
        Puzzle::class,
        Cell::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class MineSweeperDatabase : RoomDatabase() {

    abstract fun getPuzzleDao () : PuzzleDao

    companion object {
        private lateinit var instance : MineSweeperDatabase

        @Synchronized
        fun create (context : Context) : MineSweeperDatabase {
            instance = Room.databaseBuilder(context, MineSweeperDatabase::class.java, "team.db").build()
            return instance
        }

        @Synchronized
        fun get () : MineSweeperDatabase {
            return instance
        }
    }

}
