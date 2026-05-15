package `in`.vyomsoft.noti.task.TasksCards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.responses.TodoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TasksViewModel(private val repository: Repository) : ViewModel() {

    // Main data stream for the UI
    private val _todoResult = MutableLiveData<List<TodoResponse>>(emptyList())
    val todoResult: LiveData<List<TodoResponse>> = _todoResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Pagination State
    private var currentFilteredPage = 0
    private var currentAllPage = 0
    private var isLoading = false

    /**
     * Fetch tasks filtered by date with pagination.
     * Appends results to the existing list.
     */
    fun loadFilteredGroups(date: String?, isRefresh: Boolean = false) {
        if (isLoading) return

        if (isRefresh) {
            currentFilteredPage = 0
            _todoResult.value = emptyList()
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

                        _todoResult.postValue(currentList + newItems)

                        if (newItems.isNotEmpty()) {
                            currentFilteredPage++
                        }
                    } else {
                        _error.postValue("Error Fetching Filtered Tasks")
                    }
                }

                override fun onFailure(call: Call<List<TodoResponse>>, t: Throwable) {
                    isLoading = false
                    _error.postValue("Connection Failed: ${t.message}")
                }
            })
    }

    /**
     * Fetch all tasks (Debug implementation) with pagination.
     * Appends results to the existing list.
     */
    fun loadAllTodos(isRefresh: Boolean = false) {
        if (isLoading) return

        if (isRefresh) {
            currentAllPage = 0
            _todoResult.value = emptyList()
        }

        isLoading = true
        // Assuming your repository.getAllTodos() is updated to accept (page, size)
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

                        _todoResult.postValue(currentList + newItems)

                        if (newItems.isNotEmpty()) {
                            currentAllPage++
                        }
                    } else {
                        _error.postValue("Error fetching all todos")
                    }
                }

                override fun onFailure(call: Call<List<TodoResponse>>, t: Throwable) {
                    isLoading = false
                    _error.postValue("Error fetching todos: ${t.message}")
                }
            })
    }

    /**
     * Resets pagination and clears the list.
     * Useful when switching modes or dates.
     */
    fun resetPagination() {
        currentFilteredPage = 0
        currentAllPage = 0
        _todoResult.value = emptyList()
        _error.value = null
        isLoading = false
    }
}