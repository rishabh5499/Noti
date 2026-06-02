package `in`.vyomsoft.noti.notes.NotesEntry

import NoteUiState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.vyomsoft.noti.utils.AlertDialog
import `in`.vyomsoft.noti.utils.AlertDialogState
import `in`.vyomsoft.noti.utils.AlertMessageType
import `in`.vyomsoft.noti.utils.AppUtils.Companion.formatNoteDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEntryScreen(
    viewModel: NotesEntryViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var createdAt by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val updatedNoteResult by viewModel.updatedNoteResult.collectAsState()

    val isOnline by viewModel.isOnline.collectAsState()

    LaunchedEffect(updatedNoteResult) {
        updatedNoteResult?.let {
            title = it.title ?: ""
            content = it.description ?: ""
            createdAt = formatNoteDate(it.createdAt).takeIf { !it.isNullOrBlank() } ?: "Edited Today"
        }
    }

    val noteId = updatedNoteResult?.id ?: -1L

    // UI state mapper to drive your beautiful custom alert dialog
    when (val state = uiState) {
        is NoteUiState.Success -> {
            AlertDialog(
                state = AlertDialogState(
                    isOpen = true,
                    title = "Success!",
                    message = state.message,
                    type = AlertMessageType.SUCCESS,
                    positiveButtonText = "Done",
                    onPositiveClick = {
                        viewModel.resetState()
                        onNavigateBack()
                    }
                ),
                onDismissRequest = { viewModel.resetState() }
            )
        }
        is NoteUiState.Error -> {
            AlertDialog(
                state = AlertDialogState(
                    isOpen = true,
                    title = state.title,
                    message = state.message,
                    type = AlertMessageType.ERROR,
                    positiveButtonText = "Dismiss",
                    onPositiveClick = { viewModel.resetState() }
                ),
                onDismissRequest = { viewModel.resetState() }
            )
        }
        is NoteUiState.Delete -> {
            AlertDialog(
                state = AlertDialogState(
                    isOpen = true,
                    title = "Delete Note?",
                    message = "This action cannot be undone.",
                    type = AlertMessageType.ERROR,
                    positiveButtonText = "Delete",
                    negativeButtonText = "Cancel",
                    onPositiveClick = { viewModel.deleteNote(noteId.toString()) },
                    onNegativeClick = { viewModel.resetState() }
                ),
                onDismissRequest = { viewModel.resetState() }
            )
        }
        else -> {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val isSaving = uiState is NoteUiState.Loading
                    val isEditing = noteId != -1L

                    if (isEditing && !isSaving) {
                        IconButton(onClick = { viewModel.startDelete() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Note",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            if (title.isNotBlank() || content.isNotBlank()) {
                                viewModel.saveNote(title, content, noteId.takeIf { isEditing } ?: 0L)
                            }
                        },
                        enabled = !isSaving && (title.isNotEmpty() || content.isNotEmpty())
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            val btnLabel = if (noteId != -1L) "Update" else "Save"
                            Text(btnLabel, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
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
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "You're viewing this note offline. Changes cannot be synchronized right now.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.LightGray
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = createdAt,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = {
                    Text(
                        text = "Start typing...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.LightGray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.imePadding())
        }
    }
}