package fr.uha.hassenforder.easy.minesweeper.ui.puzzle

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.minesweeper.database.dao.PuzzleDao
import fr.uha.hassenforder.easy.minesweeper.model.Puzzle
import fr.uha.hassenforder.easy.minesweeper.model.PuzzleState
import fr.uha.hassenforder.easy.minesweeper.navigation.PuzzlesDestination
import fr.uha.hassenforder.easy.minesweeper.repository.ListMode
import fr.uha.hassenforder.easy.minesweeper.repository.PuzzleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListPuzzlesViewModel @Inject constructor (
    private val repository: PuzzleRepository,
    private val navigator: Navigator
): ViewModel() {

    private val _mode: MutableStateFlow<ListMode> = MutableStateFlow(ListMode.ALL)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _puzzles: Flow<Result<List<Puzzle>>> = _mode.flatMapLatest { m -> repository.getAll(m) }
        .map { content -> Result.Success(content) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading()
        )

    @Immutable
    data class UIState (
        val mode : ListMode,
        val puzzles : List<Puzzle>,
    )

    val uiState : StateFlow<Result<UIState>> = combine (_mode, _puzzles) { m, l ->
            when (l) {
                is Result.Success -> Result.Success(content = UIState(m,l.content))
                is Result.Error -> Result.Error()
                is Result.Loading -> Result.Loading()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading()
        )

    sealed class UIEvent {
        data object OnBack: UIEvent()
        data class OnMode (val mode : ListMode) : UIEvent()
        data object OnCreate: UIEvent()
        data class OnResume(val newValue: Puzzle): UIEvent()
        data class OnDelete(val newValue: Puzzle): UIEvent()
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        when (uiEvent) {
            is UIEvent.OnBack -> navigator.navigateUp()
            is UIEvent.OnMode -> _mode.value = uiEvent.mode
            is UIEvent.OnCreate -> navigator.navigate(
                destination = PuzzlesDestination.CreateScreen,
            )
            is UIEvent.OnResume -> {
                if (uiEvent.newValue.state == PuzzleState.CREATED) {
                    repository.update(
                        PuzzleDao.PuzzleUpdate.State(
                            uiEvent.newValue.pid,
                            PuzzleState.CREATED,
                            uiEvent.newValue.offset
                        )
                    )
                }
                navigator.navigate(
                    destination = PuzzlesDestination.EditScreen(uiEvent.newValue.pid),
                )
            }
            is UIEvent.OnDelete -> repository.delete(uiEvent.newValue)
        }
    }

}