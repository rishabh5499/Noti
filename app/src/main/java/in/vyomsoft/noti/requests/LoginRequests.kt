package `in`.vyomsoft.noti.requests

import com.google.gson.annotations.SerializedName

data class LoginRequests(
    @SerializedName("usernameOrEmail") var usernameOrEmail: String? = null,
    @SerializedName("password") var password: String? = null
)