package `in`.vyomsoft.noti.requests

import com.google.gson.annotations.SerializedName

data class TodoRequest(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("name") var name: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("reminder") var reminder: String? = null,
    @SerializedName("completed") var completed: Boolean = false,
    @SerializedName("subTasks") var subTasks: List<TodoRequest>? = null
)