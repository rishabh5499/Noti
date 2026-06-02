package `in`.vyomsoft.noti.auth

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import `in`.vyomsoft.noti.GA4.AppAnalytics
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.requests.SigninRequests
import `in`.vyomsoft.noti.responses.ImgBBResponse
import `in`.vyomsoft.noti.UserCacheManager
import `in`.vyomsoft.noti.requests.LoginRequests
import `in`.vyomsoft.noti.responses.ErrorResponse
import `in`.vyomsoft.noti.responses.LoginResponse
import `in`.vyomsoft.noti.utils.AppUtils
import `in`.vyomsoft.noti.utils.constants
import `in`.vyomsoft.noti.utils.constants.AUTH_TOKEN
import `in`.vyomsoft.noti.utils.constants.BEARER
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed interface SignupUiState {
    object Idle : SignupUiState
    object Loading : SignupUiState
    data class Success(val message: String) : SignupUiState
    data class Error(val title: String, val message: String) : SignupUiState
}

class SignupViewModel(private val repository: Repository) : ViewModel() {

    private val _signupResult = MutableLiveData<LoginResponse?>()
    val signupResult: LiveData<LoginResponse?> = _signupResult
    private val _uiState = MutableLiveData<SignupUiState>(SignupUiState.Idle)
    val uiState: LiveData<SignupUiState> = _uiState

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun performSignUp(request: SigninRequests) {
        _uiState.postValue(SignupUiState.Loading)

        // Changed the expected callback type to ResponseBody to prevent Gson from crashing on raw strings
        repository.performSignUp(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Read the successful body as a plain string safely
                    val rawResponseString = response.body()?.string()
                    Log.d("Signup", "Success Response: $rawResponseString")
                    Log.d(BEARER, "$BEARER$rawResponseString")
                    UserCacheManager.put(AUTH_TOKEN, "$BEARER$rawResponseString")
                    _uiState.postValue(SignupUiState.Success("Account registered successfully!"))
                    AppAnalytics.logEvent("Signup_Success")
                } else {
                    val errorMessage = try {
                        val errorBodyString = response.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "An unexpected error occurred"
                    }

                    _error.postValue(errorMessage)
                    val title = if (response.code() == 409) "Conflict Error" else "Registration Error"
                    val bundle = Bundle().apply {
                        putString("error_message", errorMessage)
                        putString("error_title", title)
                    }
                    AppAnalytics.logEvent("Signup_Failure", bundle)
                    _uiState.postValue(SignupUiState.Error(title, errorMessage))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Signup", "Error: ${t.message}")
                val bundle = Bundle().apply {
                    putString("error_message", t.message)
                }
                AppAnalytics.logEvent("Signup_Error", bundle)
                // Handles the network error display gracefully in your custom alert dialog box
                _uiState.postValue(SignupUiState.Error("Network Failure", t.localizedMessage ?: "Cannot connect to server"))
            }
        })
    }

    fun performLogin(request: LoginRequests) {
        repository.performLogin(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    _signupResult.value = (response.body())
                    val token = response.body()?.accessToken
                    Log.d(BEARER, "$BEARER$token")
                    UserCacheManager.put(AUTH_TOKEN, "$BEARER$token")
                } else {
                    _error.postValue("Login failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _error.postValue("Error: ${t.message}")
            }
        })
    }

    // Helper to clear the state back to idle once the dialog is closed
    fun resetUiState() {
        _uiState.postValue(SignupUiState.Idle)
    }
}

class SignupViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignupViewModel(repository) as T
    }
}