package fr.uha.hassenforder.easy.minesweeper.ui.field

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.uha.hassenforder.easy.minesweeper.R
import fr.uha.hassenforder.easy.minesweeper.model.PuzzleSize
import fr.uha.hassenforder.easy.minesweeper.model.PuzzleState

@Composable
fun SizeField (
    size : PuzzleSize
) {
    val level : Color =
        when (size) {
            PuzzleSize.TINY -> Color.LightGray
            PuzzleSize.SMALL -> Color.Green
            PuzzleSize.MEDIUM -> Color.Yellow
            PuzzleSize.LARGE -> Color.Red
            PuzzleSize.CUSTOM -> Color.Black
        }
    Icon(imageVector = Icons.Filled.Star, contentDescription = "size", modifier = Modifier.size(48.dp), tint = level )
}

@Composable
fun SmileyField(
    state : PuzzleState,
) {
    val smiley = when (state) {
        PuzzleState.WON -> R.drawable.smiley_happy
        PuzzleState.LOST -> R.drawable.smiley_sad
        else -> R.drawable.smiley_neutral
    }
    Image(
        painterResource(smiley),
        contentDescription = null,
    )
}
