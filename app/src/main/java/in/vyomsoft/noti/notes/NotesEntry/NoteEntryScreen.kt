package `in`.vyomsoft.noti.notes.NotesEntry

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    var showDeleteDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val updatedNoteResult by viewModel.updatedNoteResult.collectAsState()

    LaunchedEffect(updatedNoteResult) {
        updatedNoteResult?.let {
            title = it.title ?: ""
            content = it.description ?: ""
            createdAt = formatNoteDate(it.createdAt).takeIf { it.isNullOrBlank().not() } ?: "Edited Today"
        }
    }

    val noteId = updatedNoteResult?.id ?: -1L
    when (uiState) {
        is NoteUiState.Loading -> CircularProgressIndicator()
        is NoteUiState.Success -> {
            onNavigateBack()
            viewModel.resetState()
        }
        is NoteUiState.Error -> Toast.makeText(
            LocalContext.current,
            (uiState as NoteUiState.Error).message,
            Toast.LENGTH_SHORT
        )
        is NoteUiState.Delete -> {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Delete Note?") },
                text = { Text("This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteNote(noteId.toString())
                    }) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        else -> { }
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
                                viewModel.saveNote(title, content, noteId.takeIf { isEditing })
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
                            val title = if (title.isNotEmpty() || content.isNotEmpty()) "Update" else "Save"
                            Text(title, style = MaterialTheme.typography.labelLarge)
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
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedPlaceholderColor = Color.LightGray,
                    unfocusedPlaceholderColor = Color.LightGray
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // OPTIONAL: TIMESTAMP (Matches the "Contextual" requirement in Confluence)
            Text(
                text = createdAt,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // CONTENT / BODY FIELD
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
                    .weight(1f), // Takes up remaining space
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 28.sp // Increased line height for "low-anxiety" readability
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedPlaceholderColor = Color.LightGray,
                    unfocusedPlaceholderColor = Color.LightGray
                )
            )
            Spacer(modifier = Modifier.imePadding())
        }
    }
}