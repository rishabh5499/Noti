package `in`.vyomsoft.noti.utils

enum class AlertMessageType {
    SUCCESS,
    ERROR,
    GENERAL
}

data class AlertDialogState(
    val isOpen: Boolean = false,
    val title: String = "",
    val message: String = "",
    val type: AlertMessageType = AlertMessageType.GENERAL,
    val positiveButtonText: String = "OK",
    val negativeButtonText: String? = null,
    val onPositiveClick: () -> Unit = {},
    val onNegativeClick: () -> Unit = {}
)