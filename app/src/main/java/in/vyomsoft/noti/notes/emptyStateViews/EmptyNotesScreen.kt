package `in`.vyomsoft.noti.notes.emptyStateViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.vyomsoft.noti.ui.theme.AppTheme

@Composable
fun EmptyNotesScreen(
    onAddNoteClick: () -> Unit
) {
    val color = AppTheme.colors
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
                    .size(140.dp)
                    .background(
                        color = color.emptyNotifLightBlue,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.EditNote,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = color.emptyNotifBlue
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Your thoughts are lonely",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color.black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Capture your ideas, daily tasks, or quick reminders before they drift away.",
                fontSize = 15.sp,
                color = color.gray.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onAddNoteClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = color.emptyNotifBlue
                ),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp),
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                Text(
                    text = "Write your first note",
                    color = color.white,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
fun previewEmptyNote() {
    EmptyNotesScreen {  }
}