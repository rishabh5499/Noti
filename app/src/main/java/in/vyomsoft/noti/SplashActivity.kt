package `in`.vyomsoft.noti

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import `in`.vyomsoft.noti.Utils.Companion.alegreyaScBold
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.auth.LandingPage
import `in`.vyomsoft.noti.auth.LoginScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : ComponentActivity() {
    private var repository: Repository = Repository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreenContent()
        }

        lifecycleScope.launch {
            val publicIp = withContext(Dispatchers.IO) {
                try {
                    java.net.URL("https://api.ipify.org").readText()
                } catch (e: Exception) { "0.0.0.0" }
            }

            fetchUserDataSync(publicIp)
            delay(500)

            startActivity(Intent(this@SplashActivity, LandingPage::class.java))
            finish()
        }
    }

    private suspend fun fetchUserDataSync(ip: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Using .execute() makes the Retrofit call synchronous on this background thread
                val response = repository.getUserDetails(ip).execute()

                if (response.isSuccessful && response.body() != null) {
                    UserCacheManager.saveUserDetails(response.body()!!)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e("Splash", "Network error: ${e.message}")
                false
            }
        }
    }
}
//class SplashActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            SplashScreenContent()
//        }
//
//        lifecycleScope.launch {
//            delay(200)
//            startActivity(Intent(this@SplashActivity, LandingPage::class.java))
//            finish()
//        }
//    }
//}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF434343)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Noti",
                style = TextStyle(
                    fontFamily = alegreyaScBold,
                    fontSize = 96.sp,
                    fontWeight = FontWeight.W700
                ),
                color = Color(0xB8FFFFFF)
            )
            Text(
                text = "By   vyomsoft",
                style = TextStyle(
                    fontFamily = alegreyaScBold,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.W700
                ),
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreenContent()
}