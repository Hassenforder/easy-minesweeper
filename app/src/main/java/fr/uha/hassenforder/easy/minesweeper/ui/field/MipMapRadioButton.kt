package fr.uha.hassenforder.easy.minesweeper.ui.field

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

data class MipMapEntries<E : Enum<E>> (
    val value : E,
    val checkedMipmap : Int,
    val uncheckedMipmap : Int,
) {
    fun getFor (value : E) : Int {
        return if (this.value == value) checkedMipmap else uncheckedMipmap
    }
}

@Composable
fun <E : Enum<E>> MipmapRadioButton(
    value : E,
    entries : Array<MipMapEntries<E>>,
    onChange : (E) -> Unit,
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        entries.forEach { entry ->
            IconToggleButton(
                checked = value == entry.value,
                onCheckedChange =  { onChange(entry.value) }
            ) {
                Image(
                    painter = painterResource(entry.getFor(value)),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
