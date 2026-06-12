package `in`.vyomsoft.noti.apiUtils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import `in`.vyomsoft.noti.requests.LoginRequests
import `in`.vyomsoft.noti.requests.PasswordDetailsRequest
import `in`.vyomsoft.noti.requests.SigninRequests
import `in`.vyomsoft.noti.responses.LoginResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import `in`.vyomsoft.noti.requests.TodoRequest
import `in`.vyomsoft.noti.responses.ImgBBResponse
import `in`.vyomsoft.noti.responses.NotesRequest
import `in`.vyomsoft.noti.responses.NotesResponse
import `in`.vyomsoft.noti.responses.PictureLimitResponse
import `in`.vyomsoft.noti.responses.TodoResponse
import `in`.vyomsoft.noti.responses.UserDetailsResponse
import `in`.vyomsoft.noti.UserCacheManager
import `in`.vyomsoft.noti.requests.ResetPasswordRequest
import `in`.vyomsoft.noti.utils.constants.AUTH_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Callback

class Repository(val context: Context) {

//    val BASE_URL = "http://192.168.0.6:8080/"
    val BASE_URL = "https://noti.vyomsoft.in/"
    val URL_IMGBB = "https://api.imgbb.com"

    private fun getRetrofitService(): ApiUtils {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(ConnectivityInterceptor(context))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiUtils::class.java)
    }

    fun performLogin(loginRequest: LoginRequests): Call<LoginResponse> {
        return getRetrofitService().performLogin(loginRequest)
    }

    fun performSignUp(signinRequests: SigninRequests): Call<ResponseBody> {
        return getRetrofitService().performRegister(signinRequests)
    }

    fun performLogout(): Call<ResponseBody> {
        return getRetrofitService().performLogout("${UserCacheManager.get(AUTH_TOKEN)}")
    }

    fun getAllTodos(page: Int = 0, size: Int = 10): Call<List<TodoResponse>> {
        return getRetrofitService().getAllTodos(
            token = "${UserCacheManager.get(AUTH_TOKEN)}",
            pageNo = page,
            pageSize = size,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllTimeFilteredGroups(
        page: Int = 0,
        size: Int = 10,
        date: String? = null
    ): Call<List<TodoResponse>> {
        val token = "${UserCacheManager.get(AUTH_TOKEN)}"
        return getRetrofitService().getAllTimeFilteredGroupsForUser(
            token = token,
            pageNo = page,
            pageSize = size,
            date = date
        )
    }

    fun getTodo(todoId: Long): Call<TodoResponse> {
        return getRetrofitService().getTodo("${UserCacheManager.get(AUTH_TOKEN)}", todoId)
    }

    fun createTodo(request: TodoRequest): Call<TodoResponse> {
        return getRetrofitService().createTodo("${UserCacheManager.get(AUTH_TOKEN)}", request)
    }

    fun updateTodo(request: TodoRequest, todoId: Long): Call<TodoResponse> {
        return getRetrofitService().updateTodo(
            "${UserCacheManager.get(AUTH_TOKEN)}",
            todoId,
            request
        )
    }

    fun deleteTodo(todoId: Long): Call<ResponseBody> {
        return getRetrofitService().deleteTodo("${UserCacheManager.get(AUTH_TOKEN)}", todoId)
    }

    fun getAllNotes(): Call<List<NotesResponse>> {
        return getRetrofitService().getAllNotes("${UserCacheManager.get(AUTH_TOKEN)}")
    }

    fun getNote(id: String): Call<NotesResponse> {
        return getRetrofitService().getNote("${UserCacheManager.get(AUTH_TOKEN)}", id)
    }

    fun createNote(request: NotesRequest): Call<NotesResponse> {
        return getRetrofitService().createNote("${UserCacheManager.get(AUTH_TOKEN)}", request)
    }

    fun updateNote(request: NotesRequest, noteId: String): Call<NotesResponse> {
        return getRetrofitService().updateNote("${UserCacheManager.get(AUTH_TOKEN)}", noteId, request)
    }

    fun deleteNote(noteId: String): Call<ResponseBody> {
        return getRetrofitService().deleteNote("${UserCacheManager.get(AUTH_TOKEN)}", noteId)
    }

    fun uploadImage(imagePart: MultipartBody.Part): Call<ResponseBody> {
        val rawToken = UserCacheManager.get(AUTH_TOKEN).toString()
        val formattedToken = if (rawToken.startsWith("Bearer ")) rawToken else "Bearer $rawToken"
        return getRetrofitService().uploadImage(formattedToken, imagePart)
    }

    fun getUserDetails(ipAddress: String): Call<UserDetailsResponse> {
        val token = "${UserCacheManager.get(AUTH_TOKEN)}"
        return getRetrofitService().getUserDetails(token, ipAddress)
    }

    fun requestOtp(email: String, callback: Callback<ResponseBody>) {
        getRetrofitService().requestOtp(email).enqueue(callback)
    }

    fun resetPassword(request: ResetPasswordRequest, callback: Callback<ResponseBody>) {
        getRetrofitService().resetPassword(request).enqueue(callback)
    }

    fun updateUserDetails(request: UserDetailsResponse): Call<UserDetailsResponse> {
        return getRetrofitService().updateUserDetails("${UserCacheManager.get(AUTH_TOKEN)}", request)
    }

    fun updatePassword(request: PasswordDetailsRequest): Call<ResponseBody> {
        return getRetrofitService().updatePassword("${UserCacheManager.get(AUTH_TOKEN)}", request)
    }

    fun getPictureLimit(): Call<PictureLimitResponse> {
        return getRetrofitService().getPictureLimit("${UserCacheManager.get(AUTH_TOKEN)}")
    }

    suspend fun getPublicIp(): String? {
        return withContext(Dispatchers.IO) {
            try {
                java.net.URL("https://api.ipify.org").openStream()
                    .bufferedReader().use { it.readLine() }
            } catch (e: Exception) {
                null
            }
        }
    }
}