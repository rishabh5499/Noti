package `in`.vyomsoft.noti.notes.notesCards

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LockPerson
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import `in`.vyomsoft.noti.AddTaskCard
import `in`.vyomsoft.noti.GA4.AppAnalytics
import `in`.vyomsoft.noti.R
import `in`.vyomsoft.noti.notes.notesCards.models.NotesUiState
import `in`.vyomsoft.noti.responses.NotesResponse
import `in`.vyomsoft.noti.utils.constants.NoteAction

@Composable
fun NotesTabContent(
    viewModel: NotesViewModel,
    onNoteClick: (NoteAction, NotesResponse?) -> Unit,
    onReAuthenticate: () -> Unit = {}
) {
    val uiState by viewModel.uiState.observeAsState(initial = NotesUiState.Loading)

    LaunchedEffect(Unit) {
        viewModel.getAllNotes()
        AppAnalytics.logScreenView("Notes_Dashboard_Screen")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is NotesUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is NotesUiState.Empty -> {
                AppAnalytics.logEvent("Notes_Empty_Screen")
                EmptyStateScreen(
                    title = "Your thoughts are lonely",
                    description = "Capture your ideas, daily tasks, or quick reminders before they drift away.",
                    icon = Icons.Outlined.Description,
                    buttonText = "Write your first note",
                    onClick = { onNoteClick(NoteAction.ADD, null) }
                )
            }

            is NotesUiState.Success -> {
                val bundle = Bundle().apply {
                    putInt("note_count", state.notes.size)
                }
                AppAnalytics.logEvent("Notes_Success_Screen", bundle)
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        AddTaskCard(
                            text = stringResource(R.string.add_note),
                            onAddClick = { onNoteClick(NoteAction.ADD, null) }
                        )
                    }
                    items(state.notes) { note ->
                        NoteCard(
                            notes = note,
                            onClick = { onNoteClick(NoteAction.EDIT, note) }
                        )
                    }
                }
            }

            is NotesUiState.NetworkError -> {
                AppAnalytics.logError("Notes_Network_Error_Screen", "No internet connection")
                EmptyStateScreen(
                    title = "No internet connection",
                    description = "Check your Wi-Fi or cellular data and try again.",
                    icon = Icons.Outlined.WifiOff,
                    buttonText = "Try Again",
                    isError = true,
                    onClick = { viewModel.getAllNotes() }
                )
            }

            is NotesUiState.Unauthorized401 -> {
                AppAnalytics.logError("Notes_Unauthorized_Screen", "Session expired")
                EmptyStateScreen(
                    title = "Session expired",
                    description = "Your login session has ended. Please log in again to sync your notes.",
                    icon = Icons.Outlined.LockPerson,
                    buttonText = "Log In Again",
                    onClick = onReAuthenticate
                )
            }

            is NotesUiState.ServerError -> {
                AppAnalytics.logError("Notes_Server_Error_Screen", "Server error (${state.code})")
                EmptyStateScreen(
                    title = "Server error (${state.code})",
                    description = "Our servers are having a momentary crisis. We're looking into it!",
                    icon = Icons.Outlined.CloudOff,
                    buttonText = "Retry Connection",
                    isError = true,
                    onClick = { viewModel.getAllNotes() }
                )
            }

            is NotesUiState.UnknownError -> {
                AppAnalytics.logError("Notes_Unknown_Error_Screen", state.message ?: "Unknown error")
                EmptyStateScreen(
                    title = "Something went wrong",
                    description = state.message ?: "An unexpected error occurred. Please try again.",
                    icon = Icons.Outlined.ErrorOutline,
                    buttonText = "Retry",
                    isError = true,
                    onClick = { viewModel.getAllNotes() }
                )
            }
        }
    }
}

@Composable
private fun EmptyStateScreen(
    title: String,
    description: String,
    icon: ImageVector,
    buttonText: String,
    isError: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = if (isError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                if (!isError) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = buttonText)
            }
        }
    }
}