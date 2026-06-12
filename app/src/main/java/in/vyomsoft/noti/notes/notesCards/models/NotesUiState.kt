package `in`.vyomsoft.noti.notes.notesCards.models

import `in`.vyomsoft.noti.responses.NotesResponse

sealed interface NotesUiState {
    data object Loading : NotesUiState
    data class Success(val notes: List<NotesResponse>) : NotesUiState
    data object Empty : NotesUiState
    data object NetworkError : NotesUiState
    data object Unauthorized401 : NotesUiState
    data class ServerError(val code: Int) : NotesUiState
    data class UnknownError(val message: String?) : NotesUiState
}
