package `in`.vyomsoft.noti.task.TasksEntry

import TodoUiState
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vyomsoft.noti.GA4.AppAnalytics
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.requests.TodoRequest
import `in`.vyomsoft.noti.responses.TodoResponse
import `in`.vyomsoft.noti.utils.NetworkUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TasksEntryViewModel(private val repository: Repository) : ViewModel() {
    private val _uiState = MutableStateFlow<TodoUiState>(TodoUiState.Idle)
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    private val _updatedTodoResult = MutableStateFlow<TodoResponse?>(null)
    val updatedTodoResult: StateFlow<TodoResponse?> = _updatedTodoResult.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    init {
        monitorConnectivity()
    }

    private fun monitorConnectivity() {
        viewModelScope.launch {
            while (true) {
                _isOnline.value = NetworkUtils.isInternetAvailable(repository.context)
                delay(3000)
            }
        }
    }

    fun saveTodo(request: TodoRequest, todoId: Long = -1L) {
        if (todoId == -1L || todoId == 0L) {
            createTodo(request)
        } else {
            updateTodo(todoId, request)
        }
    }

    fun createTodo(todo: TodoRequest) {
        if (!_isOnline.value) {
            _uiState.value = TodoUiState.Error("No internet connection")
            return
        }

        _uiState.value = TodoUiState.Loading
        repository.createTodo(todo).enqueue(object : Callback<TodoResponse> {
            override fun onResponse(call: Call<TodoResponse>, response: Response<TodoResponse>) {
                if (response.isSuccessful) {
                    _uiState.value = TodoUiState.Success
                    _updatedTodoResult.value = response.body()
                    AppAnalytics.logEvent("Task_Created", Bundle().apply {
                        putString("task_id", response.body()?.id.toString())
                    })
                } else {
                    _uiState.value = TodoUiState.Error("Error Creating Todo")
                }
            }

            override fun onFailure(call: Call<TodoResponse>, t: Throwable) {
                _uiState.value = TodoUiState.Error("Check your Wi-Fi or cellular data and try again.")
            }
        })
    }

    fun getTask(taskId: Long) {
        if (!_isOnline.value) {
            _uiState.value = TodoUiState.Error("No internet connection")
            return
        }

        _uiState.value = TodoUiState.Loading
        repository.getTodo(taskId).enqueue(object : Callback<TodoResponse> {
            override fun onResponse(call: Call<TodoResponse>, response: Response<TodoResponse>) {
                if (response.isSuccessful) {
                    _updatedTodoResult.value = response.body()
                    _uiState.value = TodoUiState.Ready
                } else {
                    _uiState.value = TodoUiState.Error("Error Fetching Task Group")
                }
            }

            override fun onFailure(call: Call<TodoResponse>, t: Throwable) {
                _uiState.value = TodoUiState.Error("Check your Wi-Fi or cellular data and try again.")
            }
        })
    }

    fun updateTodo(taskId: Long, todo: TodoRequest) {
        if (!_isOnline.value) {
            _uiState.value = TodoUiState.Error("No internet connection")
            return
        }

        _uiState.value = TodoUiState.Loading
        repository.updateTodo(todo, taskId).enqueue(object : Callback<TodoResponse> {
            override fun onResponse(call: Call<TodoResponse>, response: Response<TodoResponse>) {
                if (response.isSuccessful) {
                    _updatedTodoResult.value = response.body()
                    _uiState.value = TodoUiState.Success
                } else {
                    _uiState.value = TodoUiState.Error("Error Updating Task")
                }
            }

            override fun onFailure(call: Call<TodoResponse>, t: Throwable) {
                _uiState.value = TodoUiState.Error("Check your Wi-Fi or cellular data and try again.")
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
        if (!_isOnline.value) {
            _uiState.value = TodoUiState.Error("No internet connection")
            return
        }

        _uiState.value = TodoUiState.Loading
        repository.deleteTodo(taskId.toLong()).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    _uiState.value = TodoUiState.Success
                } else {
                    _uiState.value = TodoUiState.Error("Error Deleting Task")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _uiState.value = TodoUiState.Error("Check your Wi-Fi or cellular data and try again.")
            }
        })
    }

    fun resetState() {
        _uiState.value = TodoUiState.Idle
    }
}