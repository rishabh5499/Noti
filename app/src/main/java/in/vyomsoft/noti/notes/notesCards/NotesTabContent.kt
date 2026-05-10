package `in`.vyomsoft.noti.notes.notesCards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import `in`.vyomsoft.noti.AddTaskCard
import `in`.vyomsoft.noti.R
import `in`.vyomsoft.noti.responses.NotesResponse
import `in`.vyomsoft.noti.utils.constants.NoteAction

@Composable
fun NotesTabContent(
    viewModel: NotesViewModel,
    onNoteClick: (NoteAction, NotesResponse?) -> Unit
) {
    val notesList by viewModel.noteResult.observeAsState(initial = emptyList())
    val errorMessage by viewModel.error.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllNotes()
    }

    if (errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = errorMessage ?: stringResource(R.string.something_went_wrong), color = Color.Red)
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
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

            items(notesList ?: emptyList()) { note ->
                NoteCard(
                    notes = note,
                    onClick = { onNoteClick(NoteAction.EDIT, note) }
                )
            }
        }
    }
}