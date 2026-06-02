package `in`.vyomsoft.noti.auth

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import `in`.vyomsoft.noti.GA4.AppAnalytics
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.requests.LoginRequests
import `in`.vyomsoft.noti.responses.LoginResponse
import `in`.vyomsoft.noti.UserCacheManager
import `in`.vyomsoft.noti.utils.NetworkErrorHandler
import `in`.vyomsoft.noti.utils.constants.AUTH_TOKEN
import `in`.vyomsoft.noti.utils.constants.BEARER
import `in`.vyomsoft.noti.utils.constants.USER_ID
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val repository: Repository) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    private val _logoutResult = MutableLiveData<ResponseBody?>()
    val logoutResult: LiveData<ResponseBody?> = _logoutResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun performLogin(request: LoginRequests) {
        repository.performLogin(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    _loginResult.value = (response.body())
                    val token = response.body()?.accessToken
                    Log.d(BEARER, "$BEARER$token")
                    AppAnalytics.logEvent("Login_Success")
                    UserCacheManager.put(AUTH_TOKEN, "$BEARER$token")
                } else {
                    val bundle = Bundle().apply {
                        putString("error_message", response.message())
                    }
                    AppAnalytics.logEvent("Login_Failure", bundle)
                    _error.postValue("Login failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val bundle = Bundle().apply {
                    putString("error_message", t.message)
                }
                AppAnalytics.logEvent("Login_Error", bundle)
                NetworkErrorHandler.handleLiveDataFailure(t, _error, "Error: ${t.message}")
            }
        })
    }

    fun performLogout() {
        repository.performLogout().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val rawText = response.body()?.string()
                    _logoutResult.postValue(response.body())
                    Log.d("Signup", "Raw response: $rawText")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Signup", "Error: ${t.message}")
                NetworkErrorHandler.handleLiveDataFailure(t, _error, "Logout failed: ${t.message}")
            }
        })
    }

    fun logout() {
        UserCacheManager.put(AUTH_TOKEN, "")
    }
}

class LoginViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(repository) as T
    }
}