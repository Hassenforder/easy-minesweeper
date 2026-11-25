package fr.uha.hassenforder.easy.minesweeper.ui.field

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.uha.hassenforder.easy.android.ui.field.text.IntDigitAsLEDField
import kotlinx.coroutines.delay
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlin.time.Clock
import kotlin.time.DurationUnit
import kotlin.time.Instant
import kotlin.time.toDuration

private fun elapsedInMilliseconds (
    start : Instant,
    offset : Long,
) : Long {
    val elapsedFromStart = if (start != Instant.DISTANT_PAST) Clock.System.now().minus(start) else 0.toDuration(
        DurationUnit.MILLISECONDS)
    return offset + elapsedFromStart.inWholeMilliseconds
}

@Composable
fun DurationField (
    start : Instant,
    offset : Long,
) {
    val elapsedInMilliseconds = remember(start, offset) { mutableLongStateOf(elapsedInMilliseconds(start, offset)) }
    val elapsedSeconds = elapsedInMilliseconds.longValue / 1000
    Text(elapsedSeconds.toString() + "s")
}

@Composable
fun DurationDigitField (
    start : Instant,
    offset : Long,
    refresh : Long = 1000L
) {
    val elapsedInMilliseconds = remember(start, offset) { mutableLongStateOf(elapsedInMilliseconds(start, offset)) }
    if (refresh > 0) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(refresh)
                elapsedInMilliseconds.longValue = elapsedInMilliseconds(start, offset)
            }
        }
    }
    val elapsedSeconds = elapsedInMilliseconds.longValue / 1000
    IntDigitAsLEDField(elapsedSeconds.toInt())
}

@Composable
fun InstantDayField (
    value : Instant,
) {
    Text(value.format(instantDayOnly))
}

@Composable
fun InstantTimeField (
    value : Instant,
) {
    Text(value.format(instantTimeOnly))
}

@Composable
fun InstantField (
    value : Instant,
) {
    Text(value.format(instantFullDay))
}

@Composable
fun InstantColumnField (
    value : Instant,
) {
    Column (
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Create,
                contentDescription = "created date"
            )
            InstantDayField(value)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = "created time"
            )
            InstantTimeField(value)
        }
    }
}

private val instantFullDay: DateTimeFormat<DateTimeComponents> by lazy {
    DateTimeComponents.Format {
        day(); char (' '); monthName(frenchMonthNames); char (' '); year()
        char (' ')
        hour(); char('h'); char (' '); minute(); char('m'); char (' '); second(); char('s')
    }
}

private val instantDayOnly: DateTimeFormat<DateTimeComponents> by lazy {
    DateTimeComponents.Format {
        day(); char (' '); monthName(frenchMonthNames); char (' '); year()
    }
}

private val instantTimeOnly: DateTimeFormat<DateTimeComponents> by lazy {
    DateTimeComponents.Format {
        hour(); char('h'); char (' '); minute(); char('m'); char (' '); second(); char('s')
    }
}

private val frenchMonthNames = MonthNames(listOf(
        "Janvier", "Fevrier", "Mars", "Avril", "Mai", "Juin",
        "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Décembre"
    )
)
