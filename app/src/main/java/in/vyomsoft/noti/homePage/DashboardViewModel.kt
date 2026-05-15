package `in`.vyomsoft.noti.homePage

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vyomsoft.noti.UserCacheManager
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.requests.PasswordDetailsRequest
import `in`.vyomsoft.noti.requests.ResetPasswordRequest
import `in`.vyomsoft.noti.responses.ImgBBResponse
import `in`.vyomsoft.noti.responses.PictureLimitResponse
import `in`.vyomsoft.noti.responses.UserDetailsResponse
import `in`.vyomsoft.noti.task.models.TaskItem
import `in`.vyomsoft.noti.utils.AppUtils
import `in`.vyomsoft.noti.utils.constants
import `in`.vyomsoft.noti.utils.constants.AUTH_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardViewModel(private val repository: Repository) : ViewModel() {
    private val _userDetails = MutableLiveData<UserDetailsResponse>()
    val userDetail: LiveData<UserDetailsResponse> = _userDetails

    private val _loginResult = MutableLiveData<Boolean?>()
    val loginResult: LiveData<Boolean?> = _loginResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loading = MutableLiveData<Boolean?>()
    val loading: LiveData<Boolean?> = _loading

    private val _taskGroups = mutableStateListOf<TaskItem>()
    val taskGroups: List<TaskItem> = _taskGroups

    private val _pictureChangeLimit = MutableLiveData<PictureLimitResponse>()
    val pictureChangeLimit: LiveData<PictureLimitResponse> = _pictureChangeLimit

    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> = _imageUrl

    private val _passwordChangeResult = MutableLiveData<ResponseBody>()
    val passwordChangeResult: LiveData<ResponseBody> = _passwordChangeResult

    fun performLogout() {
        repository.performLogout().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val rawText = response.body()?.string()
                    _loginResult.postValue(true)
                    UserCacheManager.clear()
                    Log.d("Signup", "Raw response: $rawText")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Signup", "Error: ${t.message}")
            }
        })
    }

    fun updatePassword(passwordDetails: PasswordDetailsRequest, onSuccess: () -> Unit) {
        repository.updatePassword(passwordDetails).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    onSuccess()
                    _passwordChangeResult.postValue(response.body())
                } else {
                    _error.postValue("Error fetching todos")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _error.postValue("Error fetching todos}")
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

    fun requestOtp(email: String, onSuccess: () -> Unit) {
        _loading.value = true
        _error.value = null

        repository.requestOtp(email, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                _loading.value = false
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _error.value = "Email not found or server error"
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _loading.value = false
                _error.value = "Network failure: ${t.message}"
            }
        })
    }

    fun resetPassword(request: ResetPasswordRequest, onSuccess: () -> Unit) {
        _loading.value = true
        _error.value = null

        repository.resetPassword(request, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                _loading.value = false
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _error.value = "Invalid OTP or expired. Try again."
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _loading.value = false
                _error.value = "Network failure: ${t.message}"
            }
        })
    }

    fun uploadToS3(context: Context, imageUri: Uri, user: UserDetailsResponse) {
        val contentResolver = context.contentResolver

        val mimeType = contentResolver.getType(imageUri) ?: "image/jpeg"
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
        val fileName = "profile_${System.currentTimeMillis()}.$extension"

        val inputStream = contentResolver.openInputStream(imageUri)
        val bytes = inputStream?.readBytes() ?: return
        inputStream.close()

        val requestFile = bytes.toRequestBody(
            mimeType.toMediaTypeOrNull(),
            0, bytes.size
        )

        val body = MultipartBody.Part.createFormData("file", fileName, requestFile)
        val call = repository.uploadImage(body)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val s3Url = response.body()?.string()
                    _imageUrl.postValue(s3Url)
                    user.dpUrl = s3Url
                    user.deleteUrl = null
                    updateUserDetails(user)
                } else {
                    _error.postValue("Image Upload failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _error.postValue("Image Upload failed: ${t.message}")
            }
        })
    }

    fun updateUserDetails(userDetailsResponse: UserDetailsResponse) {
        repository.updateUserDetails(userDetailsResponse).enqueue(object : Callback<UserDetailsResponse> {
            override fun onResponse(
                call: Call<UserDetailsResponse>,
                response: Response<UserDetailsResponse>
            ) {
                if (response.isSuccessful) {
                    _userDetails.postValue(response.body())
                } else {
                    _error.postValue("Error fetching todos")
                }
            }

            override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                _error.postValue("Error fetching todos}")
            }
        })
    }

    fun getPictureChangeLimit() {
        repository.getPictureLimit().enqueue(object : Callback<PictureLimitResponse> {
            override fun onResponse(
                call: Call<PictureLimitResponse>,
                response: Response<PictureLimitResponse>
            ) {
                if (response.isSuccessful) {
                    _pictureChangeLimit.postValue(response.body())
                } else {
                    _error.postValue("Error fetching todos")
                }
            }

            override fun onFailure(call: Call<PictureLimitResponse>, t: Throwable) {
                _error.postValue("Error fetching todos}")
            }
        })
    }

    fun setError(error: String) {
        _error.postValue(error)
    }
}