package fr.uha.hassenforder.easy.minesweeper.ui.puzzle

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.minesweeper.database.dao.PuzzleDao
import fr.uha.hassenforder.easy.minesweeper.dto.FullPuzzle
import fr.uha.hassenforder.easy.minesweeper.model.Cell
import fr.uha.hassenforder.easy.minesweeper.model.PuzzleState
import fr.uha.hassenforder.easy.minesweeper.repository.PuzzleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Clock

enum class GameMode {
    REVEAL,
    SHOULD_MINED,
    SHOULD_FREE,
}

@HiltViewModel
class PlayPuzzleViewModel @Inject constructor (
    private val repository: PuzzleRepository,
    private val navigator: Navigator
): ViewModel() {

    private val _id: MutableStateFlow<Long> = MutableStateFlow(0)

    private val _mode: MutableStateFlow<GameMode> = MutableStateFlow(GameMode.REVEAL)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _puzzle : StateFlow<Result<FullPuzzle>> = _id
        .flatMapLatest { id -> repository.getById(id) }
        .map { puzzle ->
            if (puzzle != null) {
                Result.Success(content = puzzle)
            } else {
                Result.Error()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    @Immutable
    data class UIState(
        val puzzle : FullPuzzle,
        val mode : GameMode,
    ) {
        companion object {
            fun create (puzzle : FullPuzzle, mode : GameMode) : UIState {
                return UIState(puzzle, mode)
            }
        }

    }

    val uiState : StateFlow<Result<UIState>> = combine(_puzzle, _mode)
        { p, m ->
            when (p) {
                is Result.Success -> Result.Success(content = UIState.create(p.content, m))
                is Result.Error -> Result.Error()
                is Result.Loading -> Result.Loading()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    sealed class UIEvent {
        data object OnBack: UIEvent()
        data class CellChange(val cell: Cell, val mode : GameMode): UIEvent()
        data class ModeChange(val newValue: GameMode): UIEvent()
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        if (uiState.value !is Result.Success) return@launch
        val uiState = uiState.value as Result.Success
        when (uiEvent) {
            UIEvent.OnBack -> {
                if (uiState.content.puzzle.puzzle.state == PuzzleState.CREATED) {
                    val elapsedFromStart =
                        Clock.System.now().minus(uiState.content.puzzle.puzzle.start)
                    repository.update(
                        PuzzleDao.PuzzleUpdate.State(
                            uiState.content.puzzle.puzzle.pid,
                            PuzzleState.CREATED,
                            uiState.content.puzzle.puzzle.offset + elapsedFromStart.inWholeMilliseconds,
                        )
                    )
                }
                navigator.navigateUp()
            }
            is UIEvent.CellChange -> repository.updateCell(uiState.content.puzzle, uiEvent.cell, uiEvent.mode)
            is UIEvent.ModeChange -> _mode.value = uiEvent.newValue
        }
    }

    fun load (pid : Long) = viewModelScope.launch {
        val pid : Long = repository.load(pid)
        _id.value = pid
    }

}
