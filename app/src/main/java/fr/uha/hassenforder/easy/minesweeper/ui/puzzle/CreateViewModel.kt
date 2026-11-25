package fr.uha.hassenforder.easy.minesweeper.ui.puzzle

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.minesweeper.model.Puzzle
import fr.uha.hassenforder.easy.minesweeper.model.PuzzleSize
import fr.uha.hassenforder.easy.minesweeper.navigation.PuzzlesDestination
import fr.uha.hassenforder.easy.minesweeper.repository.PuzzleRepository
import fr.uha.hassenforder.easy.minesweeper.validation.PuzzleValidationErrors
import fr.uha.hassenforder.easy.minesweeper.validation.PuzzleValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor (
    private val repository: PuzzleRepository,
    private val navigator: Navigator
): ViewModel() {

    private val _puzzle: MutableStateFlow<Puzzle?> = MutableStateFlow(null)

    @Immutable
    data class UIState(
        val puzzle: Puzzle,
        val validation : PuzzleValidationErrors,
    ) {
        companion object {
            fun create (puzzle : Puzzle) : UIState {
                val validation = PuzzleValidator(puzzle).validateAll()
                return UIState(puzzle, validation)
            }
        }

    }

    val uiState : StateFlow<Result<UIState>> = _puzzle
        .map { p ->
            if (p == null) Result.Error()
            else Result.Success(content = UIState.create(p))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    sealed class UIEvent {
        data object OnBack: UIEvent()
        data class SizeChanged(val puzzle: Puzzle, val newValue: PuzzleSize): UIEvent()
        data class WidthChanged(val puzzle: Puzzle, val newValue: Int): UIEvent()
        data class HeightChanged(val puzzle: Puzzle, val newValue: Int): UIEvent()
        data class MinesChanged(val puzzle: Puzzle, val newValue: Int): UIEvent()
        data class Create(val puzzle: Puzzle): UIEvent()
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        when (uiEvent) {
            is UIEvent.OnBack -> navigator.navigateUp()
            is UIEvent.SizeChanged -> _puzzle.value = repository.getPuzzles(uiEvent.newValue)
            is UIEvent.WidthChanged -> _puzzle.value = uiEvent.puzzle.copy(size=PuzzleSize.CUSTOM, width = uiEvent.newValue)
            is UIEvent.HeightChanged -> _puzzle.value = uiEvent.puzzle.copy(size=PuzzleSize.CUSTOM, height = uiEvent.newValue)
            is UIEvent.MinesChanged ->  _puzzle.value = uiEvent.puzzle.copy(size=PuzzleSize.CUSTOM, mines = uiEvent.newValue)
            is UIEvent.Create -> {
                val id : Long = repository.create(uiEvent.puzzle)
                navigator.navigateUp()
                navigator.navigate(
                    destination = PuzzlesDestination.EditScreen(id),
                )
            }
        }
    }

    fun generate () {
        _puzzle.value = repository.getPuzzles(PuzzleSize.SMALL)
    }

}
