package `in`.vyomsoft.noti.notes.NotesEntry

import NoteUiState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.responses.NotesRequest
import `in`.vyomsoft.noti.responses.NotesResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotesEntryViewModel(private val repository: Repository) : ViewModel() {

    private val _uiState = MutableStateFlow<NoteUiState>(NoteUiState.Idle)
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    private val _updatedNoteResult = MutableStateFlow<NotesResponse?>(null)
    val updatedNoteResult: StateFlow<NotesResponse?> = _updatedNoteResult.asStateFlow()

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun saveNote(title: String, content: String, noteId: Long? = -1L) {
        val request = NotesRequest(title, content)
        if (noteId == -1L || noteId == 0L) {
            createNote(request)
        } else {
            updateNote(request, noteId.toString())
        }
    }

    fun createNote(note: NotesRequest) {
        _uiState.value = NoteUiState.Loading
        repository.createNote(note).enqueue(object : Callback<NotesResponse> {
            override fun onResponse(call: Call<NotesResponse>, response: Response<NotesResponse>) {
                if (response.isSuccessful) {
                    _updatedNoteResult.value = response.body()
                    _uiState.value = NoteUiState.Success("Note created successfully!")
                } else {
                    _uiState.value = NoteUiState.Error("Creation Error", "Failed to save note to server.")
                }
            }

            override fun onFailure(call: Call<NotesResponse>, t: Throwable) {
                _uiState.value = NoteUiState.Error("Network Failure", t.message ?: "Failed to reach server")
            }
        })
    }

    fun updateNote(note: NotesRequest, noteId: String) {
        _uiState.value = NoteUiState.Loading
        repository.updateNote(note, noteId).enqueue(object : Callback<NotesResponse> {
            override fun onResponse(call: Call<NotesResponse>, response: Response<NotesResponse>) {
                if (response.isSuccessful) {
                    _updatedNoteResult.value = response.body()
                    _uiState.value = NoteUiState.Success("Note updated successfully!")
                } else {
                    _uiState.value = NoteUiState.Error("Update Error", "Failed to save modifications.")
                }
            }

            override fun onFailure(call: Call<NotesResponse>, t: Throwable) {
                _uiState.value = NoteUiState.Error("Network Failure", t.message ?: "Update failed")
            }
        })
    }

    fun getNote(noteId: String) {
        _uiState.value = NoteUiState.Loading
        repository.getNote(noteId).enqueue(object : Callback<NotesResponse> {
            override fun onResponse(call: Call<NotesResponse>, response: Response<NotesResponse>) {
                if (response.isSuccessful) {
                    _updatedNoteResult.value = response.body()
                    _uiState.value = NoteUiState.Ready
                } else {
                    _error.postValue("Error Fetching Notes")
                }
            }

            override fun onFailure(call: Call<NotesResponse>, t: Throwable) {
                _error.postValue("Error Fetching Notes")
            }
        })
    }

    fun startDelete(){
        _uiState.value = NoteUiState.Delete
    }

    fun deleteNote(noteId: String) {
        _uiState.value = NoteUiState.Loading
        repository.deleteNote(noteId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    _uiState.value = NoteUiState.Success("Note deleted successfully!")
                } else {
                    _uiState.value = NoteUiState.Error("Delete Error", "Could not complete drop event.")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _uiState.value = NoteUiState.Error("Delete Error", t.message ?: "Operation failed")
            }
        })
    }

    fun resetState() {
        _uiState.value = NoteUiState.Idle
    }
}