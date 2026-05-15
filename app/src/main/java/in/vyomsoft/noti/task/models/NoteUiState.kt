    sealed class TodoUiState {
        object Idle : TodoUiState()
        object Loading : TodoUiState()
        object Success : TodoUiState()
        object Ready : TodoUiState()
        data class Delete(val taskId: Int) : TodoUiState()
        data class Error(val message: String) : TodoUiState()
    }