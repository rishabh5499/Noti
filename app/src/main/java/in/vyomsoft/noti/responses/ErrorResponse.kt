package `in`.vyomsoft.noti.responses

data class ErrorResponse(
    val timestamp: String,
    val message: String,
    val details: String
)