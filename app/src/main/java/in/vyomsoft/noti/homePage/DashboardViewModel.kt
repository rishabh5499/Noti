package `in`.vyomsoft.noti.homePage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.responses.UserDetailsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel(private val repository: Repository) : ViewModel() {
    private val _userDetails = MutableLiveData<UserDetailsResponse>()
    val userDetail: LiveData<UserDetailsResponse> = _userDetails

    private val _loginResult = MutableLiveData<ResponseBody?>()
    val loginResult: LiveData<ResponseBody?> = _loginResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

//    init {
//        getUserDetails()
//    }

    fun performLogout() {
        repository.performLogout().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val rawText = response.body()?.string()
                    _loginResult.postValue(response.body())
                    Log.d("Signup", "Raw response: $rawText")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Signup", "Error: ${t.message}")
            }
        })
    }

    fun getUserDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            val publicIp = try {
                java.net.URL("https://api.ipify.org").readText()
            } catch (e: Exception) {
                "0.0.0.0"
            }

            withContext(Dispatchers.Main) {
                repository.getUserDetails(publicIp).enqueue(object : Callback<UserDetailsResponse> {
                    override fun onResponse(
                        call: Call<UserDetailsResponse>,
                        response: Response<UserDetailsResponse>
                    ) {
                        if (response.isSuccessful) {
                            _userDetails.postValue(response.body())
                        } else {
                            _error.postValue("Error fetching user details")
                        }
                    }

                    override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                        _error.postValue("Network Failure: ${t.message}")
                    }
                })
            }
        }
    }
}