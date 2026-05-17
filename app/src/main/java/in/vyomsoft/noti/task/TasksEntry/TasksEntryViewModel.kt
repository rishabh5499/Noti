package `in`.vyomsoft.noti.task.TasksEntry

import TodoUiState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.requests.TodoRequest
import `in`.vyomsoft.noti.responses.NotesRequest
import `in`.vyomsoft.noti.responses.NotesResponse
import `in`.vyomsoft.noti.responses.TodoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TasksEntryViewModel(private val repository: Repository) : ViewModel() {
    private val _uiState = MutableStateFlow<TodoUiState>(TodoUiState.Idle)
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    private val _updatedTodoResult = MutableStateFlow<TodoResponse?>(null)
    val updatedTodoResult: StateFlow<TodoResponse?> = _updatedTodoResult.asStateFlow()

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun saveTodo(request: TodoRequest, todoId: Long = -1L) {
        if (todoId == -1L || todoId == 0L) {
            createTodo(request)
        } else {
            updateTodo(todoId, request)
        }
    }

    fun createTodo(todo: TodoRequest) {
        _uiState.value = TodoUiState.Loading
        repository.createTodo(todo).enqueue(object : Callback<TodoResponse> {
            override fun onResponse(
                call: Call<TodoResponse>,
                response: Response<TodoResponse>
            ) {
                if (response.isSuccessful) {
                    _uiState.value = TodoUiState.Success
                    _updatedTodoResult.value = null
                    _updatedTodoResult.value = response.body()
                } else {
                    _error.postValue("Error Creating Todo")
                }
            }

            override fun onFailure(call: Call<TodoResponse>, t: Throwable) {
                _error.postValue("Error Creating Todo}")
            }
        })
    }

    fun getTask(taskId: Long) {
        _uiState.value = TodoUiState.Loading
        repository.getTodo(taskId).enqueue(object : Callback<TodoResponse> {
            override fun onResponse(
                call: Call<TodoResponse>,
                response: Response<TodoResponse>
            ) {
                if (response.isSuccessful) {
                    _updatedTodoResult.value = response.body()
                    _uiState.value = TodoUiState.Ready
                } else {
                    _error.postValue("Error Fetching Notes")
                }
            }

            override fun onFailure(call: Call<TodoResponse>, t: Throwable) {
                _error.postValue("Error Fetching Notes")
            }
        })
    }

    fun updateTodo(taskId: Long, todo: TodoRequest) {
        _uiState.value = TodoUiState.Loading
        repository.updateTodo(todo, taskId).enqueue(object : Callback<TodoResponse> {
            override fun onResponse(
                call: Call<TodoResponse>,
                response: Response<TodoResponse>
            ) {
                if (response.isSuccessful) {
                    _updatedTodoResult.value = response.body()
                    _uiState.value = TodoUiState.Success
                } else {
                    _error.postValue("Error Updating Task")
                    _uiState.value = TodoUiState.Idle
                }
            }

            override fun onFailure(call: Call<TodoResponse>, t: Throwable) {
                _error.postValue("Connection Failed: ${t.message}")
                _uiState.value = TodoUiState.Idle
            }
        })
    }

    fun startDelete(taskId: Int) {
        _uiState.value = TodoUiState.Delete(taskId)
    }

    fun stopDelete() {
        _uiState.value = TodoUiState.Idle
    }

    fun deleteTodo(taskId: Int) {
        _uiState.value = TodoUiState.Loading

        repository.deleteTodo(taskId.toLong()).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    _uiState.value = TodoUiState.Success
                } else {
                    _error.postValue("Error Deleting Task")
                    _uiState.value = TodoUiState.Idle
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _error.postValue("Connection Failed")
                _uiState.value = TodoUiState.Idle
            }
        })
    }

    fun resetState() {
        _uiState.value = TodoUiState.Idle
        _updatedTodoResult.value = null
    }
}