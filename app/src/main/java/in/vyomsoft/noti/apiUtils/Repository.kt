package `in`.vyomsoft.noti.apiUtils

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
import `in`.vyomsoft.noti.locker
import `in`.vyomsoft.noti.requests.ResetPasswordRequest
import `in`.vyomsoft.noti.utils.constants.AUTH_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Callback

class Repository {
//    val BASE_URL = "http://192.168.0.106:8080/"

    val BASE_URL = "https://noti.vyomsoft.in/"
    val URL_IMGBB = "https://api.imgbb.com"

    fun getRetrofitService(baseUrl: String): ApiUtils {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiUtils::class.java)
    }

    fun performLogin(loginRequest: LoginRequests): Call<LoginResponse> {
        return getRetrofitService(BASE_URL).performLogin(loginRequest)
    }

    fun performSignUp(signinRequests: SigninRequests): Call<ResponseBody> {
        return getRetrofitService(BASE_URL).performRegister(signinRequests)
    }

    fun performLogout(): Call<ResponseBody> {
        return getRetrofitService(BASE_URL).performLogout("${UserCacheManager.get(AUTH_TOKEN)}")
    }

    fun getAllTodos(
        page: Int = 0,
        size: Int = 10
    ): Call<List<TodoResponse>> {
        return getRetrofitService(BASE_URL).getAllTodos(
            token = "${UserCacheManager.get(AUTH_TOKEN)}",
            pageNo = page,
            pageSize = size,
        )
    }

    fun getAllTimeFilteredGroups(
        page: Int = 0,
        size: Int = 10,
        date: String? = null
    ): Call<List<TodoResponse>> {
        val token = "${UserCacheManager.get(AUTH_TOKEN)}"
        return getRetrofitService(BASE_URL).getAllTimeFilteredGroupsForUser(
            token = token,
            pageNo = page,
            pageSize = size,
            date = date
        )
    }

    fun getTodo(todoId: Long): Call<TodoResponse> {
        return getRetrofitService(BASE_URL).getTodo("${UserCacheManager.get(AUTH_TOKEN)}", todoId)
    }

    fun createTodo(request: TodoRequest): Call<TodoResponse> {
        return getRetrofitService(BASE_URL).createTodo(
            "${UserCacheManager.get(AUTH_TOKEN)}",
            request
        )
    }

    fun updateTodo(request: TodoRequest, todoId: Long): Call<TodoResponse> {
        return getRetrofitService(BASE_URL).updateTodo(
            "${UserCacheManager.get(AUTH_TOKEN)}",
            todoId,
            request
        )
    }

    fun deleteTodo(todoId: Long): Call<ResponseBody> {
        return getRetrofitService(BASE_URL).deleteTodo(
            "${UserCacheManager.get(AUTH_TOKEN)}",
            todoId
        )
    }

    fun getAllNotes(): Call<List<NotesResponse>> {
        return getRetrofitService(BASE_URL).getAllNotes("${UserCacheManager.get(AUTH_TOKEN)}")
    }

    fun getNote(id: String): Call<NotesResponse> {
        return getRetrofitService(BASE_URL).getNote("${UserCacheManager.get(AUTH_TOKEN)}", id)
    }

    fun createNote(request: NotesRequest): Call<NotesResponse> {
        return getRetrofitService(BASE_URL).createNote(
            "${UserCacheManager.get(AUTH_TOKEN)}",
            request
        )
    }

    fun updateNote(request: NotesRequest, noteId: String): Call<NotesResponse> {
        return getRetrofitService(BASE_URL).updateNote(
            "${UserCacheManager.get(AUTH_TOKEN)}",
            noteId,
            request
        )
    }

    fun deleteNote(noteId: String): Call<ResponseBody> {
        return getRetrofitService(BASE_URL).deleteNote(
            "${UserCacheManager.get(AUTH_TOKEN)}",
            noteId
        )
    }

    fun uploadImage(imagePart: MultipartBody.Part): Call<ResponseBody> {
        val rawToken = UserCacheManager.get(AUTH_TOKEN).toString()
        val formattedToken = if (rawToken.startsWith("Bearer ")) rawToken else "Bearer $rawToken"
        return getRetrofitService(BASE_URL).uploadImage(formattedToken, imagePart)
    }

    fun getUserDetails(ipAddress: String): Call<UserDetailsResponse> {
        val token = "${UserCacheManager.get(AUTH_TOKEN)}"
        return getRetrofitService(BASE_URL).getUserDetails(token, ipAddress)
    }

    fun requestOtp(email: String, callback: Callback<ResponseBody>) {
        getRetrofitService(BASE_URL).requestOtp(email).enqueue(callback)
    }

    fun resetPassword(request: ResetPasswordRequest, callback: Callback<ResponseBody>) {
        getRetrofitService(BASE_URL).resetPassword(request).enqueue(callback)
    }

    fun updateUserDetails(request: UserDetailsResponse): Call<UserDetailsResponse> {
        return getRetrofitService(BASE_URL).updateUserDetails(
            "${UserCacheManager.get(AUTH_TOKEN)}",
            request
        )
    }

    fun updatePassword(request: PasswordDetailsRequest): Call<ResponseBody> {
        return getRetrofitService(BASE_URL).updatePassword(
            "${UserCacheManager.get(AUTH_TOKEN)}",
            request
        )
    }

    fun getPictureLimit(): Call<PictureLimitResponse> {
        return getRetrofitService(BASE_URL).getPictureLimit("${UserCacheManager.get(AUTH_TOKEN)}")
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