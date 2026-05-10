package `in`.vyomsoft.noti

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.vyomsoft.noti.Utils.Companion.alegreyaScBold

@Composable
fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(Color(0xFF434343)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Noti",
                style = TextStyle(
                    fontFamily = alegreyaScBold,
                    fontSize = 48.sp,
                    color = Color.White
                )
            )
            Text(
                text = "By   vyomsoft",
                style = TextStyle(
                    fontFamily = alegreyaScBold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
fun Footer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFFE0E0E0))
    )
}