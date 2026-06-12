package `in`.vyomsoft.noti

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.auth.views.LandingPage
import `in`.vyomsoft.noti.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : ComponentActivity() {
    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = Repository(applicationContext)

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

@Composable
fun SplashScreenContent() {
    val context = LocalContext.current
    val color = AppTheme.colors

    // Configure Coil to handle GIFs
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color.black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = R.drawable.noti_splash,
                contentDescription = "Loading Animation",
                imageLoader = imageLoader,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreenContent()
}