package `in`.vyomsoft.noti.task.TasksEntry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import `in`.vyomsoft.noti.apiUtils.Repository

class TasksEntryViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TasksEntryViewModel(repository) as T
    }
}