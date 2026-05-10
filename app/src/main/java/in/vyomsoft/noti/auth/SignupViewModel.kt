package `in`.vyomsoft.noti.auth

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.requests.SigninRequests
import `in`.vyomsoft.noti.responses.ImgBBResponse
import `in`.vyomsoft.noti.UserCacheManager
import `in`.vyomsoft.noti.utils.AppUtils
import `in`.vyomsoft.noti.utils.constants
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupViewModel(private val repository: Repository) : ViewModel() {
    private val _signupResult = MutableLiveData<ResponseBody?>()
    val signupResult: LiveData<ResponseBody?> = _signupResult

    private val _imgBBResponse = MutableLiveData<ImgBBResponse>()
    val imgBBResponse: LiveData<ImgBBResponse> = _imgBBResponse

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun performSignUp(request: SigninRequests) {
        repository.performSignUp(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    _signupResult.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Signup", "Error: ${t.message}")
            }
        })
    }

    fun uploadToImgbb(context: Context, imageUri: Uri, note: SigninRequests) {
        val base64 = AppUtils.encodeImageToBase64(context, imageUri) ?: return
        val imageRequestBody = base64.toRequestBody("text/plain".toMediaType())
        val call = repository.uploadImage(constants.IMAGE_EXPIRY, imageRequestBody)

        call.enqueue(object : Callback<ImgBBResponse> {
            override fun onResponse(call: Call<ImgBBResponse>, response: Response<ImgBBResponse>) {
                if (response.isSuccessful) {
                    _imgBBResponse.postValue(response.body())
                    val response = response.body()
                    note.dpUrl = response?.data?.image?.url
                    note.deleteUrl = response?.data?.deleteUrl
                    performSignUp(note)
                } else {
                    _error.postValue("Image Upload failed}")
                }
            }

            override fun onFailure(call: Call<ImgBBResponse>, t: Throwable) {
                _error.postValue("Image Upload failed}")
            }
        })
    }
}

class SignupViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignupViewModel(repository) as T
    }
}