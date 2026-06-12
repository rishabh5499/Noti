package `in`.vyomsoft.noti.notes.notesCards

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import `in`.vyomsoft.noti.GA4.AppAnalytics
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.notes.notesCards.models.NotesUiState
import `in`.vyomsoft.noti.responses.NotesResponse
import `in`.vyomsoft.noti.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class NotesViewModel(private val repository: Repository) : ViewModel() {
    private val _uiState = MutableLiveData<NotesUiState>(NotesUiState.Loading)
    val uiState: LiveData<NotesUiState> = _uiState

    fun getAllNotes() {
        _uiState.value = NotesUiState.Loading
        repository.getAllNotes().enqueue(object : Callback<List<NotesResponse>> {
            override fun onResponse(
                call: Call<List<NotesResponse>>,
                response: Response<List<NotesResponse>>
            ) {
                if (response.isSuccessful) {
                    val notes = response.body()
                    if (notes.isNullOrEmpty()) {
                        _uiState.postValue(NotesUiState.Empty)
                        AppAnalytics.logEvent("Notes_Empty")
                    } else {
                        AppAnalytics.logEvent("Notes_Loaded", Bundle().apply {
                            putInt("note_count", notes.size)
                        })
                        _uiState.postValue(NotesUiState.Success(notes))
                    }
                } else {
                    AppAnalytics.logEvent("Notes_Error", Bundle().apply {
                        putInt("error_code", response.code())
                    })
                    when (response.code()) {
                        401 -> _uiState.postValue(NotesUiState.Unauthorized401)
                        in 500..599 -> _uiState.postValue(NotesUiState.ServerError(response.code()))
                        else -> _uiState.postValue(NotesUiState.UnknownError("Error code: ${response.code()}"))
                    }
                }
            }

            override fun onFailure(call: Call<List<NotesResponse>>, t: Throwable) {
                AppAnalytics.logEvent("Notes_Failure", Bundle().apply {
                    putString("error_message", t.message)
                })
                if (t is IOException) {
                    _uiState.postValue(NotesUiState.NetworkError)
                } else {
                    _uiState.postValue(NotesUiState.UnknownError(t.localizedMessage))
                }
            }
        })
    }
}
