package `in`.vyomsoft.noti.auth.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import `in`.vyomsoft.noti.GA4.AppAnalytics
import `in`.vyomsoft.noti.HomeActivity
import `in`.vyomsoft.noti.R
import `in`.vyomsoft.noti.UserCacheManager
import `in`.vyomsoft.noti.Utils.Companion.alegreyaScBold
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.auth.LoginUtils
import `in`.vyomsoft.noti.auth.LoginViewModel
import `in`.vyomsoft.noti.auth.LoginViewModelFactory
import `in`.vyomsoft.noti.ui.theme.AppTheme
import `in`.vyomsoft.noti.ui.theme.NotiTheme
import `in`.vyomsoft.noti.utils.AlertDialog
import `in`.vyomsoft.noti.utils.AlertDialogState
import `in`.vyomsoft.noti.utils.AlertMessageType
import `in`.vyomsoft.noti.utils.constants.AUTH_TOKEN

class LandingPage : ComponentActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private var isSessionExpired by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = Repository(applicationContext)
        val factory = LoginViewModelFactory(repository)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        AppAnalytics.logScreenView("landing_page")

        if (intent.getBooleanExtra("KEY_SESSION_EXPIRED", false)) {
            isSessionExpired = true
        } else {
            checkLoginSession()
        }
        setContent {
            NotiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        LandingScreen(
                            onLoginClick = {
                                AppAnalytics.logEvent("login_button_clicked")
                                startActivity(Intent(this@LandingPage, LoginScreen::class.java))
                                finish()
                            },
                            onRegisterClick = {
                                AppAnalytics.logEvent("register_button_clicked")
                                startActivity(Intent(this@LandingPage, RegisterPage::class.java))
                                finish()
                            }
                        )

                        if (isSessionExpired) {
                            val dialogState = AlertDialogState(
                                isOpen = true,
                                title = "Session Expired",
                                message = "Your login session has ended. Please log in again.",
                                type = AlertMessageType.ERROR,
                                positiveButtonText = "Ok",
                                onPositiveClick = { isSessionExpired = false },
                            )
                            AlertDialog(
                                state = dialogState,
                                onDismissRequest = { isSessionExpired = false }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkLoginSession() {
        val token = UserCacheManager.get(AUTH_TOKEN)
        if (token != null) {
            if (LoginUtils.Companion.isJwtExpired(token)) {
                loginViewModel.performLogout()
                UserCacheManager.clear()
                isSessionExpired = true
                AppAnalytics.logError("session_expired", "Session has expired")
            } else {
                AppAnalytics.logEvent("user_logged_in")
                startActivity(Intent(this@LandingPage, HomeActivity::class.java))
                finish()
            }
        }
    }
}

@Composable
fun LandingScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val color = AppTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color.pink)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(color.primary),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = TextStyle(
                        fontFamily = alegreyaScBold,
                        fontSize = 48.sp,
                        color = color.white
                    )
                )
                Text(
                    text = stringResource(R.string.by_vyomsoft),
                    style = TextStyle(
                        fontFamily = alegreyaScBold,
                        fontSize = 16.sp,
                        color = color.white
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.why_noti),
            style = TextStyle(
                fontSize = 36.sp,
                fontWeight = FontWeight.W900,
                color = color.deepBlack
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.noti_description),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = color.black,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(horizontal = 40.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .width(160.dp)
                .height(55.dp)
                .border(1.dp, color.black, RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = color.pink),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(stringResource(R.string.login).uppercase(), color = color.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .width(160.dp)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = color.primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.register).uppercase(), color = color.white, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.need_help),
            style = TextStyle(
                fontSize = 12.sp,
                textDecoration = TextDecoration.Underline,
                color = color.primary
            ),
            modifier = Modifier.clickable {
                context.startActivity(Intent(context, HelpActivity::class.java))
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(color.lightGray)
            )

            Image(
                painter = painterResource(id = R.drawable.landing_page_icon),
                contentDescription = "Landing Image",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@Preview
@Composable
fun previewLandingPage() {
    LandingScreen()
}