package `in`.vyomsoft.noti.notes.notesCards.models

data class Notes(
    val id: Long? = null,
    val title: String? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val medias: List<Media>? = emptyList(),
    val reminder: String? = null,
    val label: String? = null,
    val colour: String? = null
)