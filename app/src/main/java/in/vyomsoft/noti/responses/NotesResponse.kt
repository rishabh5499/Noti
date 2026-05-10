package `in`.vyomsoft.noti.responses

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class NotesResponse(
    @SerializedName("id") var id: Long? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("medias") var medias: ArrayList<Medias> = arrayListOf(),
    @SerializedName("reminder") var reminder: String? = null,
    @SerializedName("label") var label: String? = null,
    @SerializedName("colour") var color: String? = null
) : Parcelable