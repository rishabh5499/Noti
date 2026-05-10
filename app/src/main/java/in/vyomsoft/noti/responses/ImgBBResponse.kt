package `in`.vyomsoft.noti.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ImgBBResponse(
    @SerializedName("data") var data: Data? = Data(),
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("status") var status: Int? = null
)

@Parcelize
data class Image(
    @SerializedName("filename") var filename: String? = null,
    @SerializedName("mime") var mime: String? = null,
    @SerializedName("url") var url: String? = null
) : Parcelable

@Parcelize
data class Thumb(
    @SerializedName("filename") var filename: String? = null,
    @SerializedName("mime") var mime: String? = null,
    @SerializedName("url") var url: String? = null
) : Parcelable

@Parcelize
data class Data(
    @SerializedName("id") var id: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("url_viewer") var urlViewer: String? = null,
    @SerializedName("delete_url") var deleteUrl: String? = null,
    @SerializedName("image") var image: Image? = Image(),
    @SerializedName("thumb") var thumb: Thumb? = Thumb()
) : Parcelable