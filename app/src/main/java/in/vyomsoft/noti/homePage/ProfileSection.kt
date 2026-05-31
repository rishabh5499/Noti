package `in`.vyomsoft.noti.homePage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import `in`.vyomsoft.noti.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ProfileSection(
    dashboardViewModel: DashboardViewModel,
    onProfileClick: () -> Unit = {}
) {
    var currentTime by remember { mutableStateOf("") }
    val userDetails by dashboardViewModel.userDetail.observeAsState()

    LaunchedEffect(Unit) {
        dashboardViewModel.getUserDetails()
    }

    LaunchedEffect(Unit) {
        while (true) {
            val calendar = Calendar.getInstance()
            currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(calendar.time)
            delay(1000 * 60)
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = userDetails?.dpUrl ?: R.drawable.ic_launcher_foreground,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable { onProfileClick() },
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_user)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    stringResource(R.string.hi_x, userDetails?.name ?: ""),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(currentTime, fontSize = 18.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = userDetails?.weather?.icon,
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = userDetails?.weather?.text ?: "Loading...",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
//        Text("Your Progress", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}