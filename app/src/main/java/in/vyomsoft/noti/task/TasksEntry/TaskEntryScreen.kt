package `in`.vyomsoft.noti.task.TasksEntry

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.vyomsoft.noti.requests.TodoRequest
import `in`.vyomsoft.noti.responses.TodoResponse
import kotlinx.coroutines.launch
import `in`.vyomsoft.noti.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEntryScreen(
    viewModel: TasksEntryViewModel,
    onBack: () -> Unit
) {
    var groupTitle by remember { mutableStateOf("") }
    var groupId by remember { mutableStateOf("") }
    val subTasks = remember { mutableStateListOf<TodoRequest>() }
    val uiState by viewModel.uiState.collectAsState()
    val updatedTodoResult by viewModel.updatedTodoResult.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var taskKeyCounter by remember { mutableIntStateOf(1) }
    val taskKeys = remember { mutableStateListOf<Int>() }

    LaunchedEffect(updatedTodoResult) {
        updatedTodoResult?.let {
            groupTitle = it.name ?: ""
            groupId = it.id.toString()

            subTasks.clear()
            taskKeys.clear()
            it.subTasks.forEach { response ->
                subTasks.add(TodoRequest(
                    id = response?.id?.toInt() ?: 0,
                    name = response?.name,
                    completed = response?.completed ?: false
                ))
                taskKeys.add(taskKeyCounter++)
            }
            subTasks.add(TodoRequest(id = 0, name = "", completed = false))
            taskKeys.add(taskKeyCounter++)
        }
    }

    LaunchedEffect(Unit) {
        if (updatedTodoResult == null && groupId.isBlank() && subTasks.isEmpty()) {
            subTasks.add(TodoRequest(id = 0, name = "", completed = false))
            taskKeys.add(taskKeyCounter++)
        }
    }

    when (val state = uiState) {
        is TodoUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is TodoUiState.Success -> {
            onBack()
            viewModel.resetState()
        }
        is TodoUiState.Error -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetState() },
                title = { Text(stringResource(R.string.operation_failed)) },
                text = { Text(state.message) },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetState() }) { Text(stringResource(R.string.dismiss)) }
                }
            )
        }
        is TodoUiState.Delete -> {
            AlertDialog(
                onDismissRequest = { viewModel.stopDelete() },
                title = { Text(stringResource(R.string.delete_task_group)) },
                text = { Text(stringResource(R.string.this_action_cannot_be_undone)) },
                confirmButton = {
                    TextButton(onClick = { viewModel.deleteTodo(state.taskId) }) {
                        Text(stringResource(R.string.delete), color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.stopDelete() }) { Text("Cancel") }
                }
            )
        }
        else -> {}
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    val title = if (groupId.isNotBlank()) groupTitle else stringResource(R.string.new_task_group)
                    Text(title, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack,
                        stringResource(R.string.back)) }
                },
                actions = {
                    if (groupId.isNotBlank()) {
                        IconButton(onClick = { viewModel.startDelete(groupId.toInt()) }) {
                            Icon(Icons.Default.Delete,
                                stringResource(R.string.delete_group), tint = Color.Red)
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()) {
                Button(
                    onClick = {
                        val idToUse = if (groupId.isBlank()) 0L else groupId.toLong()
                        val request = prepareTodoRequest(idToUse, groupTitle, subTasks)
                        viewModel.saveTodo(request, idToUse)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(50.dp),
                    enabled = groupTitle.isNotBlank() && subTasks.any { it.name?.isNotBlank() == true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (groupId.isNotBlank()) stringResource(R.string.update_task_group) else stringResource(R.string.create_task_group))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = !isOnline,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.tasks_offline_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = groupTitle,
                onValueChange = { groupTitle = it },
                label = { Text(stringResource(R.string.group_title)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text("Sub-Tasks", fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = subTasks,
                    key = { index, _ -> if (index < taskKeys.size) taskKeys[index] else index }
                ) { index, task ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { false }
                    )

                    LaunchedEffect(dismissState.targetValue) {
                        if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd && task.name?.isNotBlank() == true) {
                            subTasks[index] = subTasks[index].copy(completed = !subTasks[index].completed)
                            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                        }
                    }

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = task.name?.isNotBlank() == true,
                        enableDismissFromEndToStart = false,
                        backgroundContent = {
                            val isSwiping = dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd || dismissState.progress > 0.1f
                            if (isSwiping) {
                                val isInitiallyCompleted = remember(dismissState.currentValue == SwipeToDismissBoxValue.Settled) {
                                    task.completed
                                }
                                val color = if (isInitiallyCompleted) Color.Gray else Color.Green
                                val label = if (isInitiallyCompleted) stringResource(R.string.mark_incomplete) else stringResource(R.string.mark_complete)

                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(text = label, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    ) {
                        Box(modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp))) {
                            TaskInputRow(
                                value = task.name ?: "",
                                isCompleted = task.completed,
                                onToggleCompletion = { isChecked ->
                                    subTasks[index] = subTasks[index].copy(completed = isChecked)
                                },
                                onValueChange = { newValue ->
                                    val wasBlank = subTasks[index].name.isNullOrBlank()
                                    subTasks[index] = subTasks[index].copy(name = newValue)

                                    if (index == subTasks.lastIndex && wasBlank && newValue.isNotBlank()) {
                                        subTasks.add(TodoRequest(id = 0, name = "", completed = false))
                                        taskKeys.add(taskKeyCounter++)

                                        coroutineScope.launch {
                                            listState.animateScrollToItem(subTasks.lastIndex)
                                        }
                                    }
                                },
                                onAddLine = {
                                    subTasks.add(TodoRequest(id = 0, name = "", completed = false))
                                    taskKeys.add(taskKeyCounter++)
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(subTasks.lastIndex)
                                    }
                                },
                                onRemoveLine = {
                                    if (subTasks.size > 1) {
                                        subTasks.removeAt(index)
                                        if (index < taskKeys.size) taskKeys.removeAt(index)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskInputRow(
    value: String,
    isCompleted: Boolean,
    onValueChange: (String) -> Unit,
    onToggleCompletion: (Boolean) -> Unit,
    onAddLine: () -> Unit,
    onRemoveLine: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (value.isNotBlank()) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = onToggleCompletion
            )
        }

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(stringResource(R.string.add_sub_task)) },
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(
                textDecoration = if (isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                color = if (isCompleted) Color.Gray else Color.Black
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        if (value.isNotBlank()) {
            IconButton(onClick = onAddLine) { Icon(Icons.Default.AddCircle, null, tint = Color.Blue) }
        }
        IconButton(onClick = onRemoveLine) { Icon(Icons.Default.Delete, null, tint = Color.LightGray) }
    }
}

fun prepareTodoRequest(id: Long, title: String, subTaskObjects: List<TodoRequest>): TodoRequest {
    return TodoRequest(
        id = id.toInt(),
        name = title,
        completed = false,
        subTasks = subTaskObjects.filter { !it.name.isNullOrBlank() }.map { task ->
            TodoRequest(
                id = task.id,
                name = task.name?.trim(),
                completed = task.completed
            )
        }
    )
}