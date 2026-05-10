package `in`.vyomsoft.noti.responses

import com.google.gson.annotations.SerializedName

data class PictureLimitResponse(
    @SerializedName("maxAllowedChanges") var maxAllowedChanges: Int? = null,
    @SerializedName("changesDone") var changesDone: Int? = null,
    @SerializedName("changesRemaining") var changesRemaining: Int? = null
)