package `in`.vyomsoft.noti.requests

import com.google.gson.annotations.SerializedName

data class TodoRequest(
    @SerializedName("name") var name: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("reminder") var date: String? = null,
    @SerializedName("completed") var completed: Boolean? = null
)