package `in`.vyomsoft.noti.requests

import com.google.gson.annotations.SerializedName

data class SigninRequests(
    @SerializedName("name") var name: String? = null,
    @SerializedName("username") var username: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("dpUrl") var dpUrl: String? = null,
    @SerializedName("deleteUrl") var deleteUrl: String? = null
)