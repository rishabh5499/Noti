package `in`.vyomsoft.noti.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.annotation.RequiresApi
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AppUtils {
    companion object {
        fun encodeImageToBase64(context: Context, imageUri: Uri): String? {
            return try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                }

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val byteArray = outputStream.toByteArray()
                Base64.encodeToString(byteArray, Base64.NO_WRAP)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun formatNoteDate(isoString: String?): String {
            if (isoString.isNullOrBlank()) return "Edited Today"

            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = inputFormat.parse(isoString)
                val outputFormat = SimpleDateFormat("d MMMM, yyyy hh:mm a", Locale.getDefault())
                date?.let { outputFormat.format(it) } ?: "Edited Today"
            } catch (_: Exception) {
                "Edited Today"
            }
        }
    }
}