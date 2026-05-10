package `in`.vyomsoft.noti.notes.notesCards.models

data class Media(
    val data: ImageDataDTO? = null
) {
    data class ImageDataDTO(
        val id: String? = null,
        val title: String? = null,
        val url_viewer: String? = null,
        val image: ImageDetailDTO? = null,
        val thumb: ImageDetailDTO? = null,
        val delete_url: String? = null
    )

    data class ImageDetailDTO(
        val filename: String? = null,
        val mime: String? = null,
        val url: String? = null
    )
}