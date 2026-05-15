package `in`.vyomsoft.noti.requests

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)