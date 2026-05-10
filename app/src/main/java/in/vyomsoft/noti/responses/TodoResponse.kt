package `in`.vyomsoft.noti.responses

import com.google.gson.annotations.SerializedName

data class TodoResponse(
    @SerializedName("id") var id: Long? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("completed") var completed: Boolean? = null,
    @SerializedName("reminder") var reminder: String? = null,
)