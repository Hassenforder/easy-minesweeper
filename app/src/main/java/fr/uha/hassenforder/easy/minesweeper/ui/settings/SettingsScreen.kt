package fr.uha.hassenforder.easy.minesweeper.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.screen.MetaDataScreen
import fr.uha.hassenforder.easy.minesweeper.R

@Composable
fun SettingsScreen(
    vm: SettingsViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = { AppTopBar(UITitle(screenNameId = R.string.title_settings)) },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp)) {
            Button(onClick = { vm.populateDatabase(0) }) {
                Text(stringResource(id = R.string.populate))
            }
            Button(onClick = { vm.clearDatabase() }) {
                Text(stringResource(id = R.string.clear))
            }
            MetaDataScreen(initiallyOpened = false, modifier = Modifier.fillMaxSize())
        }
    }

}