package fr.uha.hassenforder.easy.minesweeper.ui.puzzle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.app.AppMenu
import fr.uha.hassenforder.easy.android.ui.app.AppMenuEntry
import fr.uha.hassenforder.easy.android.ui.app.AppUp
import fr.uha.hassenforder.easy.android.ui.field.text.IntDigitAsLEDField
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.minesweeper.R
import fr.uha.hassenforder.easy.minesweeper.dto.FullPuzzle
import fr.uha.hassenforder.easy.minesweeper.model.Cell
import fr.uha.hassenforder.easy.minesweeper.model.CellState
import fr.uha.hassenforder.easy.minesweeper.ui.field.DurationDigitField
import fr.uha.hassenforder.easy.minesweeper.ui.field.MipMapEntries
import fr.uha.hassenforder.easy.minesweeper.ui.field.MipmapRadioButton
import fr.uha.hassenforder.easy.minesweeper.ui.field.SmileyField

private val backgroundColor = Color(0xFFC0C0C0)
private val topShadow = Color(0xFFF0F0F0)
private val bottomShadow = Color(0xFF808080)
private const val shadowThickness = 3.0f

@Composable
fun PlayPuzzleScreen (
    vm: PlayPuzzleViewModel = hiltViewModel(),
    pid : Long
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.load(pid)
    }

    StateScreen(state = uiState) {
        InternalPlayPuzzleScreen(it, { vm.send(it) })
    }
}

@Composable
private fun InternalPalette(
    mode : GameMode,
    onChange : (GameMode) -> Unit,
) {
    MipmapRadioButton(
        value = mode,
        entries = arrayOf(
            MipMapEntries(GameMode.REVEAL, R.mipmap.reveal, R.mipmap.reveal_bw),
            MipMapEntries(GameMode.SHOULD_MINED, R.mipmap.flag_mined, R.mipmap.flag_mined_bw),
            MipMapEntries(GameMode.SHOULD_FREE, R.mipmap.flag_empty, R.mipmap.flag_empty_bw),
        ),
        onChange = onChange
    )
}

private fun imageId (cell : Cell, cheater : Boolean) : Int {
    return when (cell.state) {
        is CellState.Hidden -> if (cheater && cell.state.mined) R.mipmap.bomb_bw else R.mipmap.empty
        is CellState.Neighbour -> when (cell.state.count) {
            0 -> R.mipmap.empty
            1 -> R.mipmap.mines_1
            2 -> R.mipmap.mines_2
            3 -> R.mipmap.mines_3
            4 -> R.mipmap.mines_4
            5 -> R.mipmap.mines_5
            6 -> R.mipmap.mines_6
            7 -> R.mipmap.mines_7
            8 -> R.mipmap.mines_8
            else -> R.mipmap.empty
        }
        is CellState.Revealed -> when (cell.state.exploded) {
            true -> R.mipmap.exploded
            false -> R.mipmap.bomb
        }
        is CellState.ShouldEmpty -> R.mipmap.flag_empty
        is CellState.ShouldMined -> R.mipmap.flag_mined
    }
}

private fun withShadow (cell : Cell) : Boolean {
    return when (cell.state) {
        is CellState.Hidden -> true
        is CellState.Neighbour -> false
        is CellState.Revealed -> true
        is CellState.ShouldEmpty -> false
        is CellState.ShouldMined -> false
    }
}

@Composable
private fun InternalCell(
    cell : Cell,
    onChange : (Cell) -> Unit,
    cellSize: Int,
    cheater : Boolean
) {
    Box(
        modifier = Modifier
            .size(cellSize.dp)
            .selectable(selected = false, onClick = { onChange(cell) } )
            .drawBehind {
                if (withShadow(cell)) {
                    drawLine(topShadow, Offset(0f, 0f), Offset(size.width - shadowThickness, 0f), shadowThickness)
                    drawLine(topShadow, Offset(0f, 0f), Offset(0f, size.height - shadowThickness), shadowThickness)
                    drawLine(bottomShadow, Offset(size.width - shadowThickness, size.height - shadowThickness), Offset(size.width - shadowThickness, 0f), shadowThickness)
                    drawLine(bottomShadow, Offset(size.width - shadowThickness, size.height - shadowThickness), Offset(0f, size.height - shadowThickness), shadowThickness)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painterResource(imageId(cell, cheater)),
            contentDescription = null,
            modifier = Modifier.requiredSize((cellSize-2).dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun InternalGrid(
    puzzle : FullPuzzle,
    onChange : (Cell) -> Unit,
    cheater : Boolean
) {
    var cellSize by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .padding(top = 4.dp)
            .fillMaxSize()
            .onGloballyPositioned {
                cellSize = with(density) {
                    minOf(
                        it.size.width / puzzle.puzzle.width,
                        it.size.height / puzzle.puzzle.height
                    ).toDp().value.toInt()
                }
            },
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .padding(top = 4.dp)
                .width((puzzle.puzzle.width * cellSize).dp)
                .height((puzzle.puzzle.height * cellSize).dp)
                .background(backgroundColor),
            columns = GridCells.Fixed(puzzle.puzzle.width),
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
            horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally)
        ) {
            items(
                items = puzzle.cells,
                key = { it.key }
            ) { item ->
                InternalCell(item, onChange, cellSize, cheater)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InternalPlayPuzzleScreen(
    uiState: PlayPuzzleViewModel.UIState,
    send : (PlayPuzzleViewModel.UIEvent) -> Unit,
) {
    val cheater = remember { mutableStateOf(false) }
    val menuEntries = remember (cheater) {
        listOf (
            AppMenuEntry.OverflowIdEntry(
                titleId = R.string.cheater_on,
                enabled = { ! cheater.value },
                listener = { cheater.value = true }
            ),
            AppMenuEntry.OverflowIdEntry(
                titleId = R.string.cheater_off,
                enabled = { cheater.value },
                listener = { cheater.value = false }
            ),
        )
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.title_play_puzzle))
                },
                navigationIcon = { AppUp(onClick = { send(PlayPuzzleViewModel.UIEvent.OnBack) }) },
                actions = { AppMenu(menuEntries) },
                windowInsets = WindowInsets(top = 0.dp, bottom = 0.dp),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val remaining = remember(uiState.puzzle) { uiState.puzzle.puzzle.mines - uiState.puzzle.cells.count { it.state is CellState.ShouldMined } }
                IntDigitAsLEDField (value = remaining)
                SmileyField(uiState.puzzle.puzzle.state)
                DurationDigitField (uiState.puzzle.puzzle.start, uiState.puzzle.puzzle.offset, 1000)
            }
            InternalPalette(
                mode = uiState.mode,
                onChange = { send(PlayPuzzleViewModel.UIEvent.ModeChange(it)) }
            )
            InternalGrid(
                puzzle = uiState.puzzle,
                onChange = { send(PlayPuzzleViewModel.UIEvent.CellChange(it, uiState.mode)) },
                cheater.value
            )
        }
    }
}
