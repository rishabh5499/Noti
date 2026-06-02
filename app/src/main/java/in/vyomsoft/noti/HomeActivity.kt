package `in`.vyomsoft.noti

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
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


        homePageViewModel.error.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }

        setContent {
            NotiTheme {
                NotiNavigation()
            }
        }
    }
}