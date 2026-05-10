package `in`.vyomsoft.noti.responses

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("accessToken") var accessToken: String? = null,
    @SerializedName("tokenType") var tokenType: String? = null
)