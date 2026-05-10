package `in`.vyomsoft.noti.task

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.vyomsoft.noti.AddTaskCard
import `in`.vyomsoft.noti.R
import `in`.vyomsoft.noti.TaskCard
import `in`.vyomsoft.noti.responses.NotesResponse
import `in`.vyomsoft.noti.task.models.TaskItem
import `in`.vyomsoft.noti.utils.constants.NoteAction

@Composable
fun TaskTabContent(
    onNoteClick: (NoteAction, NotesResponse?) -> Unit
) {
    val tasks = listOf(
        TaskItem(1, "Task 1", date = "27/8/2023"),
        TaskItem(2, "Task 2", date = "27/8/2023")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tasks) { task ->
            TaskCard(task)
        }

        item {
            AddTaskCard(
                text = stringResource(R.string.add_task),
                onAddClick = { onNoteClick(NoteAction.ADD, null) }
            )
        }
    }
}

@Composable
fun TaskCard(task: TaskItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.priority,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = task.date,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = task.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            task.subTasks.forEach { sub ->
                Text(text = sub, fontSize = 14.sp, color = Color.DarkGray)
            }
        }
    }
}