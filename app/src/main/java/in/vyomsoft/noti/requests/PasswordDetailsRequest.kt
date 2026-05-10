package `in`.vyomsoft.noti.requests

import com.google.gson.annotations.SerializedName

data class PasswordDetailsRequest(
    @SerializedName("oldPassword") var oldPassword: String? = null,
    @SerializedName("newPassword") var password: String? = null
)
