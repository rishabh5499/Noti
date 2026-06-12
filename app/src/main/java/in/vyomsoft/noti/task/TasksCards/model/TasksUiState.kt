package `in`.vyomsoft.noti.task.TasksCards.model

import `in`.vyomsoft.noti.responses.TodoResponse

sealed interface TasksUiState {
    data object Loading : TasksUiState
    data class Success(val tasks: List<TodoResponse>) : TasksUiState
    data object Empty : TasksUiState
    data object NetworkError : TasksUiState
    data object Unauthorized401 : TasksUiState
    data class ServerError(val code: Int) : TasksUiState
    data class UnknownError(val message: String?) : TasksUiState
}