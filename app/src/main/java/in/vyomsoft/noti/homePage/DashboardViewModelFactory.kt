package `in`.vyomsoft.noti.homePage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.notes.notesCards.NotesViewModel

class DashboardViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DashboardViewModel(repository) as T
    }
}