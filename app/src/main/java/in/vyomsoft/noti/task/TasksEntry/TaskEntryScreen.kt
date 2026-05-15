package `in`.vyomsoft.noti.task.TasksEntry

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.vyomsoft.noti.requests.TodoRequest
import `in`.vyomsoft.noti.responses.TodoResponse
import `in`.vyomsoft.noti.task.TasksCards.TasksViewModel
import `in`.vyomsoft.noti.utils.AppUtils.Companion.formatNoteDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEntryScreen(
    viewModel: TasksEntryViewModel,
    onBack: () -> Unit
) {
    var content by remember { mutableStateOf<List<TodoResponse?>>(emptyList()) }
    var createdAt by remember { mutableStateOf("") }
    var groupId by remember { mutableStateOf("") }
    var groupTitle by remember { mutableStateOf("") }
    val subTasks = remember { mutableStateListOf<TodoRequest>() }
    val uiState by viewModel.uiState.collectAsState()
    val updatedTodoResult by viewModel.updatedTodoResult.collectAsState()
    val focusRequester = remember { FocusRequester() }
    var focusedIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(subTasks.size, focusedIndex) {
        if (focusedIndex != -1 && focusedIndex < subTasks.size) {
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {

            }
        }
    }

    LaunchedEffect(updatedTodoResult) {
        updatedTodoResult?.let {
            groupTitle = it.name ?: ""
            groupId = it.id.toString()

            subTasks.clear()
            it.subTasks.forEach { response ->
                subTasks.add(TodoRequest(
                    id = response?.id?.toInt() ?: 0,
                    name = response?.name,
                    completed = response?.completed ?: false
                ))
            }
            subTasks.add(TodoRequest(id = 0, name = "", completed = false))
        }
    }

    when (uiState) {
        is TodoUiState.Loading -> CircularProgressIndicator()
        is TodoUiState.Success -> {
            onBack()
            viewModel.resetState()
        }

        is TodoUiState.Error -> Toast.makeText(
            LocalContext.current,
            (uiState as TodoUiState.Error).message,
            Toast.LENGTH_SHORT
        )

        is TodoUiState.Delete -> {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Delete Note?") },
                text = { Text("This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteTodo((uiState as TodoUiState.Delete).taskId)
                    }) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.stopDelete() }) {
                        Text("Cancel")
                    }
                }
            )
        }

        else -> {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = if (groupId.isNotBlank()) groupTitle else "New Task Group"
                    Text(title, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                actions = {
                    if (groupId.isNotBlank()) {
                        IconButton(onClick = { viewModel.startDelete(groupId.toInt()) }) {
                            Icon(Icons.Default.Delete, "Delete Group", tint = Color.Red)
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.navigationBarsPadding()) {
                Button(
                    // Update the onClick in your Save Button
                    onClick = {
                        val idToUse = if (groupId.isBlank()) 0L else groupId.toLong()
                        val request = prepareTodoRequest(idToUse, groupTitle, subTasks)
                        viewModel.saveTodo(request, idToUse)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    enabled = groupTitle.isNotBlank() && subTasks.any { it.name?.isNotBlank() == true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (groupId.isNotBlank()) "Update Task Group" else "Create Task Group")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = groupTitle,
                onValueChange = { groupTitle = it },
                label = { Text("Group Title (e.g., Morning Routine)") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Sub-Tasks", fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            if (content != null) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(subTasks) { index, task ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.StartToEnd && task.name?.isNotBlank() == true) {
                                    subTasks[index] = subTasks[index].copy(completed = !subTasks[index].completed)
                                    false
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = task.name?.isNotBlank() == true,
                            enableDismissFromEndToStart = false,
                            backgroundContent = {
                                val isSwiping = dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd ||
                                        dismissState.progress > 0.1f

                                if (isSwiping) {
                                    val color = if (task.completed) Color.Gray else Color.Green
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .background(color, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 16.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            if (task.completed) "Mark Incomplete" else "Mark Complete",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Box(modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp))) {
                                TaskInputRow(
                                    index = index,
                                    value = task.name ?: "",
                                    isCompleted = task.completed,
                                    onToggleCompletion = { isChecked ->
                                        subTasks[index] =
                                            subTasks[index].copy(completed = isChecked)
                                    },
                                    modifier = if (index == focusedIndex) Modifier.focusRequester(
                                        focusRequester
                                    ) else Modifier,
                                    onValueChange = { newValue ->
                                        subTasks[index] = subTasks[index].copy(name = newValue)
                                        if (index == subTasks.lastIndex && newValue.isNotBlank()) {
                                            subTasks.add(
                                                TodoRequest(
                                                    id = 0,
                                                    name = "",
                                                    completed = false
                                                )
                                            )
                                        }
                                    },
                                    onAddLine = {
                                        subTasks.add(
                                            TodoRequest(
                                                id = 0,
                                                name = "",
                                                completed = false
                                            )
                                        )
                                        focusedIndex = subTasks.size - 1
                                    },
                                    onRemoveLine = {
                                        if (subTasks.size > 1) subTasks.removeAt(index)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskInputRow(
    index: Int,
    value: String,
    isCompleted: Boolean,
    onValueChange: (String) -> Unit,
    onToggleCompletion: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
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
            placeholder = { Text("Add sub-task...") },
            modifier = modifier.weight(1f),
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