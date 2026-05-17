package `in`.vyomsoft.noti.task.TasksCards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.vyomsoft.noti.AddTaskCard
import `in`.vyomsoft.noti.R
import `in`.vyomsoft.noti.responses.TodoResponse
import `in`.vyomsoft.noti.task.models.TaskItem
import `in`.vyomsoft.noti.utils.AppUtils.Companion.formatTaskDate
import `in`.vyomsoft.noti.utils.constants.NoteAction

@Composable
fun TaskTabContent(
    viewModel: TasksViewModel,
    selectedDate: String,
    onTaskClick: (NoteAction, TodoResponse?) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadFilteredGroups(selectedDate, isRefresh = true)
    }
    val taskGroups by viewModel.todoResult.observeAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(taskGroups ?: emptyList()) { group ->
                TaskCard(
                    task = group,
                    onClick = { onTaskClick(NoteAction.EDIT, group) }
                )
            }

            item {
                AddTaskCard(
                    text = stringResource(R.string.add_task),
                    onAddClick = { onTaskClick(NoteAction.ADD, null) }
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TodoResponse,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            if (task.completed?.not() ?: false) CardDefaults.cardColors(containerColor = Color.White)
            else CardDefaults.cardColors(containerColor = Color.LightGray),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                task.name?.let {
                    Text(
                        text = it,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                task.createdAt?.let {
                    Text(
                        text = formatTaskDate(it),
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            task.subTasks.forEachIndexed { index, subTask ->
                Text(
                    text = "${index + 1}. ${subTask?.name}",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(vertical = 2.dp),
                    style = TextStyle(
                        textDecoration = if (subTask?.completed ?: false) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                        color = if (subTask?.completed ?: false) Color.Gray else Color.Black
                    )
                )
            }
        }
    }
}