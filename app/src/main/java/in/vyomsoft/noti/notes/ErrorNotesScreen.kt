package `in`.vyomsoft.noti.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LockPerson
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

sealed interface NotesErrorState {
    data object NetworkError : NotesErrorState
    data object Unauthorized401 : NotesErrorState
    data class ServerError(val code: Int) : NotesErrorState
    data class Unknown(val message: String?) : NotesErrorState
}

@Composable
fun ErrorNotesScreen(
    errorState: NotesErrorState,
    onRetryClick: () -> Unit,
    onReAuthenticateClick: () -> Unit
) {
    val (icon, title, description, buttonText, onButtonClick) = when (errorState) {
        NotesErrorState.NetworkError -> quintetOf(
            Icons.Outlined.WifiOff,
            "No internet connection",
            "Check your Wi-Fi or cellular data and try again.",
            "Try Again",
            onRetryClick
        )
        NotesErrorState.Unauthorized401 -> quintetOf(
            Icons.Outlined.LockPerson,
            "Session expired",
            "Your login session has ended. Please log in again to sync your notes.",
            "Log In Again",
            onReAuthenticateClick
        )
        is NotesErrorState.ServerError -> quintetOf(
            Icons.Outlined.CloudOff,
            "Server error (${errorState.code})",
            "Our servers are having a momentary crisis. We're on it!",
            "Retry Connection",
            onRetryClick
        )
        is NotesErrorState.Unknown -> quintetOf(
            Icons.Outlined.ErrorOutline,
            "Something went wrong",
            errorState.message ?: "An unexpected error occurred. Please try again.",
            "Retry",
            onRetryClick
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (errorState is NotesErrorState.Unauthorized401)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = buttonText)
            }
        }
    }
}

private data class Quintet<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)
private fun <A, B, C, D, E> quintetOf(a: A, b: B, c: C, d: D, e: E) = Quintet(a, b, c, d, e)