package `in`.vyomsoft.noti.notes.notesCards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import `in`.vyomsoft.noti.apiUtils.Repository

class NotesViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotesViewModel(repository) as T
    }
}