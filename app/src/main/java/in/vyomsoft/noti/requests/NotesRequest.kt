package `in`.vyomsoft.noti.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

data class NotesRequest(
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("medias") var medias: ArrayList<Medias> = arrayListOf(),
    @SerializedName("label") var label: String? = null,
    @SerializedName("colour") var colour: String? = null,
    @SerializedName("reminder") var date: String? = null
)

@Parcelize
data class Medias(
    @SerializedName("data" ) var data : Data? = Data()
) : Parcelable