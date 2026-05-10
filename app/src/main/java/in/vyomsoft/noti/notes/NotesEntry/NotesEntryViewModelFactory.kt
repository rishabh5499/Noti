package `in`.vyomsoft.noti.notes.NotesEntry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import `in`.vyomsoft.noti.apiUtils.Repository

class NotesEntryViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotesEntryViewModel(repository) as T
    }
}