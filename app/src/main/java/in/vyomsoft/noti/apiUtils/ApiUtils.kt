package `in`.vyomsoft.noti.apiUtils

import `in`.vyomsoft.noti.requests.LoginRequests
import `in`.vyomsoft.noti.requests.PasswordDetailsRequest
import `in`.vyomsoft.noti.requests.SigninRequests
import `in`.vyomsoft.noti.requests.TodoRequest
import `in`.vyomsoft.noti.responses.ImgBBResponse
import `in`.vyomsoft.noti.responses.LoginResponse
import `in`.vyomsoft.noti.responses.NotesRequest
import `in`.vyomsoft.noti.responses.NotesResponse
import `in`.vyomsoft.noti.responses.PictureLimitResponse
import `in`.vyomsoft.noti.responses.TodoResponse
import `in`.vyomsoft.noti.responses.UserDetailsResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiUtils {
    @Headers("Content-Type: application/json")
    @POST("api/auth/signin")
    fun performLogin(@Body request: LoginRequests): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("api/auth/signup")
    fun performRegister(@Body request: SigninRequests): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("api/auth/logout")
    fun performLogout(@Header("Authorization") token: String?): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("todos")
    fun getAllTodos(@Header("Authorization") token: String?): Call<List<TodoResponse>>

    @Headers("Content-Type: application/json")
    @GET("todos/{id}")
    fun getTodo(@Header("Authorization") token: String?, @Path("id") id: String): Call<List<TodoResponse>>

    @Headers("Content-Type: application/json")
    @POST("todos")
    fun createTodo(@Header("Authorization") token: String?, @Body request: TodoRequest): Call<TodoResponse>

    @Headers("Content-Type: application/json")
    @PUT("todos/{id}")
    fun updateTodo(@Header("Authorization") token: String?, @Path("id") id: String, @Body request: TodoRequest): Call<TodoResponse>

    @Headers("Content-Type: application/json")
    @DELETE("todos/{id}")
    fun deleteTodo(@Header("Authorization") token: String?, @Path("id") id: String): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("notes")
    fun getAllNotes(@Header("Authorization") token: String?): Call<List<NotesResponse>>

    @Headers("Content-Type: application/json")
    @GET("notes/{id}")
    fun getNote(@Header("Authorization") token: String?, @Path("id") id: String): Call<NotesResponse>

    @Headers("Content-Type: application/json")
    @POST("notes")
    fun createNote(@Header("Authorization") token: String?, @Body request: NotesRequest): Call<NotesResponse>

    @Headers("Content-Type: application/json")
    @PUT("notes/{id}")
    fun updateNote(@Header("Authorization") token: String?, @Path("id") id: String, @Body request: NotesRequest): Call<NotesResponse>

    @Headers("Content-Type: application/json")
    @DELETE("notes/{id}")
    fun deleteNote(@Header("Authorization") token: String?, @Path("id") id: String): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("/userDetails")
    fun getUserDetails(
        @Header("Authorization") token: String?,
        @Header("X-Forwarded-For") userIp: String
    ): Call<UserDetailsResponse>

    @Headers("Content-Type: application/json")
    @PUT("/userDetails")
    fun updateUserDetails(@Header("Authorization") token: String?, @Body request: UserDetailsResponse): Call<UserDetailsResponse>

    @Headers("Content-Type: application/json")
    @PUT("/userDetails/password")
    fun updatePassword(@Header("Authorization") token: String?, @Body request: PasswordDetailsRequest): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("/userDetails/picture-limit")
    fun getPictureLimit(@Header("Authorization") token: String?): Call<PictureLimitResponse>

    @Multipart
    @POST("1/upload")
    fun uploadImage(
        @Query("key") apiKey: String,
        @Query("expiration") expiration: Int?,
        @Part("image") imageBase64: RequestBody
    ): Call<ImgBBResponse>
}