    sealed class NoteUiState {
            object Idle : NoteUiState()
            object Loading : NoteUiState()
            object Ready : NoteUiState()
            object Delete : NoteUiState()
            data class Success(val message: String) : NoteUiState()
            data class Error(val title: String, val message: String) : NoteUiState()
    }