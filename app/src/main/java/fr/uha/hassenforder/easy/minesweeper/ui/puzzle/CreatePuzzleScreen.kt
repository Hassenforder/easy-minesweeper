package fr.uha.hassenforder.easy.minesweeper.ui.puzzle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.DecoratorProperties
import fr.uha.hassenforder.easy.android.ui.app.AppUp
import fr.uha.hassenforder.easy.android.ui.field.spinner.OutlinedEnumSpinnerField
import fr.uha.hassenforder.easy.android.ui.field.spinner.SpinnerProperties
import fr.uha.hassenforder.easy.android.ui.field.text.ActionOneUnitLess
import fr.uha.hassenforder.easy.android.ui.field.text.ActionOneUnitMore
import fr.uha.hassenforder.easy.android.ui.field.text.ActionTenUnitsLess
import fr.uha.hassenforder.easy.android.ui.field.text.ActionTenUnitsMore
import fr.uha.hassenforder.easy.android.ui.field.text.OutlinedIntField
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.minesweeper.R
import fr.uha.hassenforder.easy.minesweeper.model.PuzzleSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePuzzleScreen (
    vm: CreateViewModel = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.generate()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.title_create_puzzle))
                },
                navigationIcon = { AppUp(onClick = { vm.send(CreateViewModel.UIEvent.OnBack) }) },
                windowInsets = WindowInsets(top = 0.dp, bottom = 0.dp),
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            StateScreen(uiState) {
                InternalCreatePuzzleScreen(it, vm::send )
            }
        }
    }
}

@Composable
private fun InternalCreatePuzzleScreen (
    uiState: CreateViewModel.UIState,
    send : (CreateViewModel.UIEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedEnumSpinnerField(
            value = uiState.puzzle.size,
            onValueChange = { send(CreateViewModel.UIEvent.SizeChanged(uiState.puzzle, it)) },
            properties = SpinnerProperties(
                converter = { PuzzleSize.valueOf(it) },
                optionValues = PuzzleSize.entries.toTypedArray()
            ),
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.size,
                errorId = null,
            ),
        )
        OutlinedIntField(
            value = uiState.puzzle.width,
            onValueChange = { send(CreateViewModel.UIEvent.WidthChanged(uiState.puzzle, it)) },
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.width,
                errorId = uiState.validation.width,
            ),
            leading = {
                ActionTenUnitsLess()
                ActionOneUnitLess()
            },
            trailing = {
                ActionOneUnitMore()
                ActionTenUnitsMore()
            },
        )
        OutlinedIntField(
            value = uiState.puzzle.height,
            onValueChange = { send(CreateViewModel.UIEvent.HeightChanged(uiState.puzzle, it)) },
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.height,
                errorId = uiState.validation.height,
            ),
            leading = {
                ActionTenUnitsLess()
                ActionOneUnitLess()
            },
            trailing = {
                ActionOneUnitMore()
                ActionTenUnitsMore()
            },
        )
        OutlinedIntField(
            value = uiState.puzzle.mines,
            onValueChange = { send(CreateViewModel.UIEvent.MinesChanged(uiState.puzzle, it)) },
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.mines,
                errorId = uiState.validation.mines,
            ),
            leading = {
                ActionTenUnitsLess()
                ActionOneUnitLess()
            },
            trailing = {
                ActionOneUnitMore()
                ActionTenUnitsMore()
            },
        )
        Button(
            onClick = { send(CreateViewModel.UIEvent.Create(uiState.puzzle)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.validation.hasError().not()
        ) {
            Text(stringResource(R.string.create))
        }
    }
}