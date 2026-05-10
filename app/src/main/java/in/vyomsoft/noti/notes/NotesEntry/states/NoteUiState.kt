    sealed class NoteUiState {
        object Idle : NoteUiState()
        object Loading : NoteUiState()
        object Success : NoteUiState()
        object Ready : NoteUiState()
        object Delete : NoteUiState()
        data class Error(val message: String) : NoteUiState()
    }