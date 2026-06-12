package `in`.vyomsoft.noti.utils

import NoteUiState
import androidx.lifecycle.MutableLiveData
import `in`.vyomsoft.noti.apiUtils.NoConnectivityException
import `in`.vyomsoft.noti.notes.notesCards.models.NotesUiState
import kotlinx.coroutines.flow.MutableStateFlow

object NetworkErrorHandler {
    fun handleStateFailure(
        throwable: Throwable,
        uiState: MutableStateFlow<NoteUiState>,
        defaultMsg: String
    ) {
        if (throwable is NoConnectivityException) {
            uiState.value = NoteUiState.Error("No Internet", "No internet connection available.")
        } else {
            uiState.value = NoteUiState.Error("Network Failure", throwable.message ?: defaultMsg)
        }
    }

    fun handleNotesListFailure(
        throwable: Throwable,
        uiState: MutableStateFlow<NotesUiState>
    ) {
        if (throwable is NoConnectivityException) {
            uiState.value = NotesUiState.NetworkError
        } else {
            uiState.value = NotesUiState.UnknownError(throwable.message)
        }
    }

    fun handleLiveDataFailure(throwable: Throwable, liveData: MutableLiveData<String?>, defaultMsg: String) {
        if (throwable is NoConnectivityException) {
            liveData.postValue("No internet connection available.")
        } else {
            liveData.postValue(defaultMsg)
        }
    }
}