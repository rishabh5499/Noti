package `in`.vyomsoft.noti.responses

import com.google.gson.annotations.SerializedName

data class UserDetailsResponse(
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("username") var username: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("dpUrl") var dpUrl: String? = null,
    @SerializedName("deleteUrl") var deleteUrl: String? = null,
    @SerializedName("weather") val weather: WeatherData?
)

data class WeatherData(
    @SerializedName("text") val text: String,
    @SerializedName("icon") val icon: String
)