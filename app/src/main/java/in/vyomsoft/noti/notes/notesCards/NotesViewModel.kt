package `in`.vyomsoft.noti.notes.notesCards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.responses.NotesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotesViewModel(private val repository: Repository) : ViewModel() {
    private val _noteResult = MutableLiveData<List<NotesResponse>?>()
    val noteResult: LiveData<List<NotesResponse>?> = _noteResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getAllNotes() {
        repository.getAllNotes().enqueue(object : Callback<List<NotesResponse>> {
            override fun onResponse(
                call: Call<List<NotesResponse>>,
                response: Response<List<NotesResponse>>
            ) {
                if (response.isSuccessful) {
                    _noteResult.postValue(response.body())
                } else {
                    _error.postValue("Error Fetching Notes")
                }
            }

            override fun onFailure(call: Call<List<NotesResponse>>, t: Throwable) {
                _error.postValue("Error Fetching Notes")
            }
        })
    }
}
