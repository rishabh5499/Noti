package `in`.vyomsoft.noti.task.TasksCards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import `in`.vyomsoft.noti.apiUtils.Repository

class TasksViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TasksViewModel(repository) as T
    }
}