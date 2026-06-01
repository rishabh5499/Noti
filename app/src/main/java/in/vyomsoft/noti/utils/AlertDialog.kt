package `in`.vyomsoft.noti.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun AlertDialog(
    state: AlertDialogState,
    onDismissRequest: () -> Unit
) {
    if (!state.isOpen) return

    val themeColor = when (state.type) {
        AlertMessageType.ERROR -> Color(0xFFD32F2F)  // Crimson Red
        AlertMessageType.SUCCESS -> Color(0xFF2E7D32) // Deep Emerald Green
        AlertMessageType.GENERAL -> Color(0xFF434343) // Your Dark Charcoal Theme Color
    }

    val icon = when (state.type) {
        AlertMessageType.ERROR -> Icons.Default.Close
        AlertMessageType.SUCCESS -> Icons.Default.Check
        AlertMessageType.GENERAL -> Icons.Default.Info
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Status Icon Circle
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(themeColor.copy(alpha = 0.12f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = themeColor
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title
                Text(
                    text = state.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Message Text
                Text(
                    text = state.message,
                    fontSize = 15.sp,
                    color = Color(0xFF616161),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Actions Column / Row
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Main Action Button
                    Button(
                        onClick = {
                            state.onPositiveClick()
                            onDismissRequest()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = state.positiveButtonText.uppercase(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Optional Dismiss/Cancel Button
                    if (state.negativeButtonText != null) {
                        TextButton(
                            onClick = {
                                state.onNegativeClick()
                                onDismissRequest()
                            },
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Text(
                                text = state.negativeButtonText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF757575)
                            )
                        }
                    }
                }
            }
        }
    }
}