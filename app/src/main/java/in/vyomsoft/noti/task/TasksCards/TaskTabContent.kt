package `in`.vyomsoft.noti.task.TasksCards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.vyomsoft.noti.AddTaskCard
import `in`.vyomsoft.noti.R
import `in`.vyomsoft.noti.responses.TodoResponse
import `in`.vyomsoft.noti.utils.AppUtils.Companion.formatTaskDate
import `in`.vyomsoft.noti.utils.constants.NoteAction

@Composable
fun TaskTabContent(
    viewModel: TasksViewModel,
    selectedDate: String,
    onTaskClick: (NoteAction, TodoResponse?) -> Unit
) {
    LaunchedEffect(selectedDate) {
        viewModel.loadFilteredGroups(selectedDate, isRefresh = true)
    }
    val taskGroups by viewModel.todoResult.observeAsState(initial = emptyList())
    val uiState by viewModel.uiState.observeAsState(initial = TasksUiState.Loading)

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is TasksUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is TasksUiState.NetworkError -> {
                TaskFeedbackScreen(
                    icon = Icons.Default.WifiOff,
                    iconTint = Color(0xFFB71C1C),
                    iconBackground = Color(0xFFFEEBEE),
                    title = stringResource(R.string.no_internet_connection),
                    description = stringResource(R.string.check_your_wi_fi),
                    actionLabel = stringResource(R.string.try_again),
                    buttonColor = Color(0xFFB71C1C),
                    onActionClick = { viewModel.loadFilteredGroups(selectedDate, isRefresh = true) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is TasksUiState.Empty -> {
                TaskFeedbackScreen(
                    icon = Icons.Default.CheckCircle,
                    iconTint = Color(0xFF00668B),
                    iconBackground = Color(0xFFE1F5FE),
                    title = stringResource(R.string.your_timeline_is_quiet),
                    description = stringResource(R.string.organize_your_day_desc),
                    actionLabel = stringResource(R.string.create_your_first_task),
                    buttonColor = Color(0xFF00668B),
                    onActionClick = { onTaskClick(NoteAction.ADD, null) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (state is TasksUiState.Error) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(taskGroups) { group ->
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
        }
    }
}

@Composable
fun TaskFeedbackScreen(
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color,
    title: String,
    description: String,
    actionLabel: String?,
    buttonColor: Color,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(color = iconBackground, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )

        if (actionLabel != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onActionClick,
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                contentPadding = PaddingValues(horizontal = 32.dp)
            ) {
                Text(
                    text = actionLabel,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
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
        colors = if (task.completed?.not() ?: true) {
            CardDefaults.cardColors(containerColor = Color.White)
        } else {
            CardDefaults.cardColors(containerColor = Color.LightGray)
        },
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                task.name?.let {
                    Text(
                        text = it,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart)
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
                    modifier = Modifier.padding(vertical = 2.dp),
                    style = TextStyle(
                        textDecoration = if (subTask?.completed ?: false) {
                            androidx.compose.ui.text.style.TextDecoration.LineThrough
                        } else null,
                        color = if (subTask?.completed ?: false) Color.Gray else Color.Black
                    )
                )
            }
        }
    }
}