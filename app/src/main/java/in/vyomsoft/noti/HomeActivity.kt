package `in`.vyomsoft.noti

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.ui.theme.NotiTheme
import `in`.vyomsoft.noti.navigation.NotiNavigation

class HomeActivity : ComponentActivity() {

    private lateinit var homePageViewModel: HomePageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        UserCacheManager.init(applicationContext)

        val repository = Repository(applicationContext)
        val factory = HomePageViewModelFactory(repository)

        homePageViewModel =
            ViewModelProvider(this, factory)[HomePageViewModel::class.java]

        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val showBanner = remoteConfig.getBoolean("show_outage_banner")
                    val useCustom = remoteConfig.getBoolean("use_custom_message")

                    val bannerMessage = if (useCustom) {
                        remoteConfig.getString("banner_message_custom")
                    } else {
                        remoteConfig.getString("banner_message_generic")
                    }
                    homePageViewModel.setOutageStatus(showBanner, bannerMessage)
                }
            }

        homePageViewModel.error.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }

        setContent {
            val outageUiState by homePageViewModel.outageState.collectAsState()
            NotiTheme {
                NotiNavigation(outageUiState = outageUiState)
            }
        }
    }
}