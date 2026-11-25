package fr.uha.hassenforder.easy.minesweeper.ui.puzzle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.app.AppMenu
import fr.uha.hassenforder.easy.android.ui.app.AppMenuEntry
import fr.uha.hassenforder.easy.android.ui.app.AppUp
import fr.uha.hassenforder.easy.android.ui.app.appMenuEntriesBuilder
import fr.uha.hassenforder.easy.android.ui.lazy.SwipeableItem
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.minesweeper.R
import fr.uha.hassenforder.easy.minesweeper.model.Puzzle
import fr.uha.hassenforder.easy.minesweeper.repository.ListMode
import fr.uha.hassenforder.easy.minesweeper.ui.field.DurationField
import fr.uha.hassenforder.easy.minesweeper.ui.field.InstantColumnField
import fr.uha.hassenforder.easy.minesweeper.ui.field.SizeField
import fr.uha.hassenforder.easy.minesweeper.ui.field.SmileyField

@Composable
fun ListPuzzlesScreen (
    vm: ListPuzzlesViewModel = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    StateScreen(uiState) {
        SuccessListScreen(it, vm::send )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessListScreen(
    uiState: ListPuzzlesViewModel.UIState,
    send : (ListPuzzlesViewModel.UIEvent) -> Unit,
) {
    val listModeAsStrings = stringArrayResource(R.array.listMode)
    val menuEntries = remember (uiState.mode) {
        appMenuEntriesBuilder(
            selected = uiState.mode,
            itemValues = ListMode.entries.toTypedArray(),
            itemLabels = listModeAsStrings,
            onChange = { send(ListPuzzlesViewModel.UIEvent.OnMode(it)) }
        )
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.title_list_puzzles))
                },
                navigationIcon = { AppUp(onClick = { send(ListPuzzlesViewModel.UIEvent.OnBack) }) },
                actions = { AppMenu(menuEntries) },
                windowInsets = WindowInsets(top = 0.dp, bottom = 0.dp),
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { send(ListPuzzlesViewModel.UIEvent.OnCreate) } ) {
                Icon(Icons.Filled.Add, contentDescription = "create")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(
                items = uiState.puzzles,
                key = { puzzle -> puzzle.pid }
            ) { item ->
                SwipeableItem(
                    onEdit = { send(ListPuzzlesViewModel.UIEvent.OnResume(item)) },
                    onDelete = { send(ListPuzzlesViewModel.UIEvent.OnDelete(item)) }
                ) {
                    PuzzleItem(item)
                }
            }
        }
    }
}

@Composable
private fun PuzzleItem (
    puzzle : Puzzle,
) {
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        leadingContent = {
            SizeField(puzzle.size)
        },
        trailingContent = {
            SmileyField (puzzle.state)
        },
        supportingContent = {
            Row (
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.GridOn,
                    contentDescription = "grid"
                )
                Text(puzzle.width.toString())
                Text("x")
                Text(puzzle.height.toString())
                Icon(
                    imageVector = Icons.Outlined.Flag,
                    contentDescription = "mines"
                )
                Text(puzzle.mines.toString())
                Icon(
                    imageVector = Icons.Outlined.HourglassEmpty,
                    contentDescription = "duration"
                )
                DurationField(puzzle.start, puzzle.offset)
            }
        },
        content = {
            InstantColumnField (puzzle.created)
        },
    )
}
