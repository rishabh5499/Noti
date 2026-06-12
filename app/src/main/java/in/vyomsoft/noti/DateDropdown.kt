package `in`.vyomsoft.noti

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import `in`.vyomsoft.noti.responses.TodoResponse
import `in`.vyomsoft.noti.ui.theme.AppTheme
import `in`.vyomsoft.noti.utils.constants.NoteAction
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDropdown(
    selectedDate: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var currentDate by remember { mutableStateOf("") }
    val color = AppTheme.colors

    val apiFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val displayFormatter = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        val today = Calendar.getInstance().time
        selectedDate(apiFormatter.format(today))
    }

    val selectedDateText = datePickerState.selectedDateMillis?.let {
        val date = java.util.Date(it)
        val apiDate = apiFormatter.format(date)

        LaunchedEffect(apiDate) {
            selectedDate(apiDate)
        }

        displayFormatter.format(date)
    } ?: run {
        displayFormatter.format(Calendar.getInstance().time)
    }

    Surface(
        modifier = Modifier
            .wrapContentSize()
            .clickable { showDatePicker = true },
        shape = RoundedCornerShape(12.dp),
        color = color.lightGray,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DateRange, null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = selectedDateText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Icon(Icons.Default.KeyboardArrowDown, null)
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}