package `in`.vyomsoft.noti.task.TasksCards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.vyomsoft.noti.AddTaskCard
import `in`.vyomsoft.noti.R
import `in`.vyomsoft.noti.responses.TodoResponse
import `in`.vyomsoft.noti.ui.theme.AppTheme
import `in`.vyomsoft.noti.utils.AppUtils.Companion.formatTaskDate
import `in`.vyomsoft.noti.utils.constants.NoteAction

@Composable
fun TaskTabContent(
    viewModel: TasksViewModel,
    selectedDate: String,
    onTaskClick: (NoteAction, TodoResponse?) -> Unit
) {
    val color = AppTheme.colors
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
                    iconTint = color.errorRed,
                    iconBackground = color.errorPink,
                    title = stringResource(R.string.no_internet_connection),
                    description = stringResource(R.string.check_your_wi_fi),
                    actionLabel = stringResource(R.string.try_again),
                    buttonColor = color.errorRed,
                    onActionClick = { viewModel.loadFilteredGroups(selectedDate, isRefresh = true) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is TasksUiState.Empty -> {
                TaskFeedbackScreen(
                    icon = Icons.Default.CheckCircle,
                    iconTint = color.emptyNotifBlue,
                    iconBackground = color.emptyNotifLightBlue,
                    title = stringResource(R.string.your_timeline_is_quiet),
                    description = stringResource(R.string.organize_your_day_desc),
                    actionLabel = stringResource(R.string.create_your_first_task),
                    buttonColor = color.emptyNotifBlue,
                    onActionClick = { onTaskClick(NoteAction.ADD, null) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is TasksUiState.Error -> {
                if (state.errorCode == 502 && taskGroups.isEmpty()) {
                    TaskFeedbackScreen(
                        icon = Icons.Default.CloudOff,
                        iconTint = color.errorRed,
                        iconBackground = color.errorPink,
                        title = "Server error (502)",
                        description = "Our servers are having a momentary crisis. Please try opening the app again in a few minutes.",
                        actionLabel = null,
                        buttonColor = color.errorRed,
                        onActionClick = {},
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    TaskContentWithBanner(
                        stateMessage = state.message,
                        taskGroups = taskGroups,
                        onTaskClick = onTaskClick
                    )
                }
            }

            is TasksUiState.Success -> {
                TaskContentWithBanner(
                    stateMessage = null,
                    taskGroups = taskGroups,
                    onTaskClick = onTaskClick
                )
            }
        }
    }
}

@Composable
fun TaskContentWithBanner(
    stateMessage: String?,
    taskGroups: List<TodoResponse>,
    onTaskClick: (NoteAction, TodoResponse?) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (stateMessage != null) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stateMessage,
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
    val color = AppTheme.colors
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
            color = color.headingFaded,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            fontSize = 14.sp,
            color = color.gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
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
                    color = color.white,
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
    val color = AppTheme.colors
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = if (task.completed?.not() ?: true) {
            CardDefaults.cardColors(containerColor = Color.White)
        } else {
            CardDefaults.cardColors(containerColor = Color.LightGray)
        },
        border = BorderStroke(1.dp, color.cardBorder)
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
                        color = color.gray,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val visibleSubTasks = task.subTasks.take(5)

            visibleSubTasks.forEachIndexed { index, subTask ->
                Text(
                    text = "${index + 1}. ${subTask?.name}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 2.dp),
                    style = TextStyle(
                        textDecoration = if (subTask?.completed ?: false) {
                            TextDecoration.LineThrough
                        } else null,
                        color = if (subTask?.completed ?: false) color.gray else color.black
                    )
                )
            }

            if (task.subTasks.size > 5) {
                val remainingCount = task.subTasks.size - 5
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "+ $remainingCount more items",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = color.gray,
                    modifier = Modifier.padding(start = 4.dp).padding(vertical = 2.dp)
                )
            }
        }
    }
}