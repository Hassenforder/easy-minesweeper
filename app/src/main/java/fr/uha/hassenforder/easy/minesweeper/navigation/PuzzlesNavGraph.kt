package fr.uha.hassenforder.easy.minesweeper.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import fr.uha.hassenforder.easy.minesweeper.ui.puzzle.ListPuzzlesScreen
import fr.uha.hassenforder.easy.minesweeper.ui.puzzle.CreatePuzzleScreen
import fr.uha.hassenforder.easy.minesweeper.ui.puzzle.PlayPuzzleScreen
import kotlinx.serialization.Serializable

sealed interface PuzzlesDestination : NavKey {
    @Serializable
    data object ListScreen: NavKey

    @Serializable
    data object CreateScreen: NavKey

    @Serializable
    data class EditScreen(val pid: Long): NavKey
}

fun EntryProviderScope<NavKey>.puzzlesNavGraph () {
    entry<PuzzlesDestination.ListScreen> {
        ListPuzzlesScreen()
    }
    entry<PuzzlesDestination.CreateScreen> {
        CreatePuzzleScreen()
    }
    entry<PuzzlesDestination.EditScreen> {
        PlayPuzzleScreen(pid = it.pid)
    }
}