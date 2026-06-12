package `in`.vyomsoft.noti.task.TasksCards

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import `in`.vyomsoft.noti.GA4.AppAnalytics
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.responses.TodoResponse
import `in`.vyomsoft.noti.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed interface TasksUiState {
    object Loading : TasksUiState
    object Empty : TasksUiState
    object NetworkError : TasksUiState
    object Success : TasksUiState
    data class Error(val message: String, val errorCode: Int? = null) : TasksUiState
}

class TasksViewModel(private val repository: Repository) : ViewModel() {

    private val _todoResult = MutableLiveData<List<TodoResponse>>(emptyList())
    val todoResult: LiveData<List<TodoResponse>> = _todoResult

    private val _uiState = MutableLiveData<TasksUiState>(TasksUiState.Loading)
    val uiState: LiveData<TasksUiState> = _uiState

    private var currentFilteredPage = 0
    private var currentAllPage = 0
    private var isLoading = false

    fun loadFilteredGroups(date: String?, isRefresh: Boolean = false) {
        if (isLoading) return

        if (isRefresh) {
            currentFilteredPage = 0
            _todoResult.value = emptyList()
        }

        if (!NetworkUtils.isInternetAvailable(repository.context)) {
            if (_todoResult.value.isNullOrEmpty()) {
                _uiState.value = TasksUiState.NetworkError
            } else {
                _uiState.value = TasksUiState.Error("No connection available to load more tasks.")
            }
            return
        }

        if (_todoResult.value.isNullOrEmpty()) {
            _uiState.value = TasksUiState.Loading
        }

        isLoading = true
        repository.getAllTimeFilteredGroups(page = currentFilteredPage, size = 15, date = date)
            .enqueue(object : Callback<List<TodoResponse>> {
                override fun onResponse(
                    call: Call<List<TodoResponse>>,
                    response: Response<List<TodoResponse>>
                ) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val newItems = response.body() ?: emptyList()
                        val currentList = _todoResult.value ?: emptyList()
                        val updatedList = currentList + newItems

                        _todoResult.postValue(updatedList)

                        if (newItems.isNotEmpty()) {
                            currentFilteredPage++
                        }

                        if (updatedList.isEmpty()) {
                            _uiState.postValue(TasksUiState.Empty)
                            AppAnalytics.logEvent("Tasks_Empty")
                        } else {
                            _uiState.postValue(TasksUiState.Success)
                            AppAnalytics.logEvent("Tasks_Loaded", Bundle().apply {
                                putInt("task_count", newItems.size)
                            })
                        }
                    } else {
                        AppAnalytics.logEvent("Tasks_Error", Bundle().apply {
                            putInt("error_code", response.code())
                        })

                        // Pass the actual server code (e.g., 502) down to the UI layout handler
                        if (_todoResult.value.isNullOrEmpty()) {
                            _uiState.postValue(TasksUiState.Error("Server error (${response.code()})", response.code()))
                        } else {
                            _uiState.postValue(TasksUiState.Error("Error Fetching Filtered Tasks: ${response.code()}"))
                        }
                    }
                }

                override fun onFailure(call: Call<List<TodoResponse>>, t: Throwable) {
                    isLoading = false
                    AppAnalytics.logEvent("Tasks_Failure", Bundle().apply {
                        putString("error_message", t.message)
                    })

                    if (_todoResult.value.isNullOrEmpty()) {
                        _uiState.postValue(TasksUiState.NetworkError)
                    } else {
                        _uiState.postValue(TasksUiState.Error("Connection Failed: ${t.message}"))
                    }
                }
            })
    }

    fun loadAllTodos(isRefresh: Boolean = false) {
        if (isLoading) return

        if (isRefresh) {
            currentAllPage = 0
            _todoResult.value = emptyList()
        }

        if (!NetworkUtils.isInternetAvailable(repository.context)) {
            if (_todoResult.value.isNullOrEmpty()) {
                _uiState.value = TasksUiState.NetworkError
            } else {
                _uiState.value = TasksUiState.Error("No internet connectivity.")
            }
            return
        }

        if (_todoResult.value.isNullOrEmpty()) {
            _uiState.value = TasksUiState.Loading
        }

        isLoading = true
        repository.getAllTodos(page = currentAllPage, size = 15)
            .enqueue(object : Callback<List<TodoResponse>> {
                override fun onResponse(
                    call: Call<List<TodoResponse>>,
                    response: Response<List<TodoResponse>>
                ) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val newItems = response.body() ?: emptyList()
                        val currentList = _todoResult.value ?: emptyList()
                        val updatedList = currentList + newItems

                        _todoResult.postValue(updatedList)

                        if (newItems.isNotEmpty()) {
                            currentAllPage++
                        }

                        if (updatedList.isEmpty()) {
                            _uiState.postValue(TasksUiState.Empty)
                        } else {
                            _uiState.postValue(TasksUiState.Success)
                        }
                    } else {
                        if (_todoResult.value.isNullOrEmpty()) {
                            _uiState.postValue(TasksUiState.Error("Server error (${response.code()})", response.code()))
                        } else {
                            _uiState.postValue(TasksUiState.Error("Error fetching all todos"))
                        }
                    }
                }

                override fun onFailure(call: Call<List<TodoResponse>>, t: Throwable) {
                    isLoading = false
                    if (_todoResult.value.isNullOrEmpty()) {
                        _uiState.postValue(TasksUiState.NetworkError)
                    } else {
                        _uiState.postValue(TasksUiState.Error("Error fetching todos: ${t.message}"))
                    }
                }
            })
    }

    fun resetPagination() {
        currentFilteredPage = 0
        currentAllPage = 0
        _todoResult.value = emptyList()
        _uiState.value = TasksUiState.Loading
        isLoading = false
    }
}