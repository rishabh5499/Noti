package `in`.vyomsoft.noti

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TaskCard(title: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("TOP PRIORITY", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                (1..5).forEach { Text("$it.", fontSize = 14.sp) }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("27/6/2003", fontSize = 10.sp)
                Text("Start: 6.45PM", fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun AddTaskCard(
    text: String,
    onAddClick: () -> Unit
) {
    OutlinedCard(
        onClick = onAddClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}