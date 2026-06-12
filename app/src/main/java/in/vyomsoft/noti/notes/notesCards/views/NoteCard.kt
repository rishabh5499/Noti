package `in`.vyomsoft.noti.notes.notesCards.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.vyomsoft.noti.responses.NotesResponse
import `in`.vyomsoft.noti.ui.theme.AppTheme
import `in`.vyomsoft.noti.utils.AppUtils.Companion.formatNoteDate

@Composable
fun NoteCard(
    notes: NotesResponse,
    onClick: () -> Unit
) {
    val color = AppTheme.colors
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, color.lightGray),
        colors = CardDefaults.outlinedCardColors(containerColor = color.white)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            notes.title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            notes.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = color.black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            notes.createdAt?.let {
                Text(
                    text = formatNoteDate(it),
                    style = MaterialTheme.typography.labelSmall,
                    color = color.lightGray
                )
            }
        }
    }
}