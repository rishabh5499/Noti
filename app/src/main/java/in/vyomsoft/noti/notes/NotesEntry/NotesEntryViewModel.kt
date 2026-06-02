package `in`.vyomsoft.noti.notes.NotesEntry

import NoteUiState
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vyomsoft.noti.GA4.AppAnalytics
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.responses.NotesRequest
import `in`.vyomsoft.noti.responses.NotesResponse
import `in`.vyomsoft.noti.utils.NetworkErrorHandler
import `in`.vyomsoft.noti.utils.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val connectivityManager =
        repository.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            viewModelScope.launch {
                _isOnline.emit(true)
            }
        }

        override fun onLost(network: Network) {
            viewModelScope.launch {
                _isOnline.emit(false)
            }
        }
    }

    init {
        val initialConnectionState = NetworkUtils.isInternetAvailable(repository.context)
        _isOnline.value = initialConnectionState

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    override fun onCleared() {
        super.onCleared()
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // Prevent lifecycle exceptions if unregister happens during teardown sweeps
        }
    }

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
                    AppAnalytics.logEvent("Note_Created", Bundle().apply {
                        putString("note_id", response.body()?.id.toString())
                    })
                } else {
                    AppAnalytics.logEvent("Note_Creation_Error", Bundle().apply {
                        putInt("error_code", response.code())
                    })
                    _uiState.value = NoteUiState.Error("Creation Error", "Failed to save note to server.")
                }
            }

            override fun onFailure(call: Call<NotesResponse>, t: Throwable) {
                AppAnalytics.logEvent("Note_Creation_Failure", Bundle().apply {
                    putString("error_message", t.message)
                })
                NetworkErrorHandler.handleStateFailure(t, _uiState, "Failed to reach server")
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
                    AppAnalytics.logEvent("Note_Updated", Bundle().apply {
                        putString("note_id", noteId)
                    })
                } else {
                    AppAnalytics.logEvent("Note_Update_Error", Bundle().apply {
                        putInt("error_code", response.code())
                    })
                    _uiState.value = NoteUiState.Error("Update Error", "Failed to save modifications.")
                }
            }

            override fun onFailure(call: Call<NotesResponse>, t: Throwable) {
                AppAnalytics.logEvent("Note_Update_Failure", Bundle().apply {
                    putString("error_message", t.message)
                })
                NetworkErrorHandler.handleStateFailure(t, _uiState, "Update failed")
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
                    AppAnalytics.logEvent("Note_Fetched", Bundle().apply {
                        putString("note_id", noteId)
                    })
                } else {
                    AppAnalytics.logEvent("Note_Fetch_Error", Bundle().apply {
                        putInt("error_code", response.code())
                    })
                    _error.postValue("Error Fetching Notes")
                }
            }

            override fun onFailure(call: Call<NotesResponse>, t: Throwable) {
                AppAnalytics.logEvent("Note_Fetch_Failure", Bundle().apply {
                    putString("error_message", t.message)
                })
                NetworkErrorHandler.handleLiveDataFailure(t, _error, "Error Fetching Notes")
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
                    AppAnalytics.logEvent("Note_Deleted", Bundle().apply {
                        putString("note_id", noteId)
                    })
                } else {
                    AppAnalytics.logEvent("Note_Delete_Error", Bundle().apply {
                        putInt("error_code", response.code())
                    })
                    _uiState.value = NoteUiState.Error("Delete Error", "Could not complete drop event.")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                AppAnalytics.logEvent("Note_Delete_Failure", Bundle().apply {
                    putString("error_message", t.message)
                })
                NetworkErrorHandler.handleStateFailure(t, _uiState, "Operation failed")
            }
        })
    }

    fun resetState() {
        _uiState.value = NoteUiState.Idle
    }
}