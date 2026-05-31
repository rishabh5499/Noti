package `in`.vyomsoft.noti.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import `in`.vyomsoft.noti.ui.theme.NotiTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import `in`.vyomsoft.noti.HomeActivity
import `in`.vyomsoft.noti.R
import `in`.vyomsoft.noti.UserCacheManager
import `in`.vyomsoft.noti.Utils.Companion.alegreyaScBold
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.utils.constants.AUTH_TOKEN
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

class LandingPage : ComponentActivity() {
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = Repository()
        val factory = LoginViewModelFactory(repository)

        loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        observeLogin()
        setContent {
            NotiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    LandingScreen(
                        onLoginClick = {
                            startActivity(Intent(this@LandingPage, LoginScreen::class.java))
                            finish()
                        },
                        onRegisterClick = {
                            startActivity(Intent(this@LandingPage, RegisterPage::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }

    private fun observeLogin() {
        if (UserCacheManager.get(AUTH_TOKEN) != null) {
            startActivity(Intent(this@LandingPage, HomeActivity::class.java))
            finish()
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDEBABB))
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(Color(0xFF434343)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = TextStyle(
                        fontFamily = alegreyaScBold,
                        fontSize = 48.sp,
                        color = Color.White
                    )
                )
                Text(
                    text = stringResource(R.string.by_vyomsoft),
                    style = TextStyle(
                        fontFamily = alegreyaScBold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.why_noti),
            style = TextStyle(
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1A1A1A)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.noti_description),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color.Black,
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
                .border(1.dp, Color.Black, RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5C1C1)),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(stringResource(R.string.login).uppercase(), color = Color(0xFF434343), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .width(160.dp)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF434343)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.register).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.need_help),
            style = TextStyle(
                fontSize = 12.sp,
                textDecoration = TextDecoration.Underline,
                color = Color(0xFF434343)
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
                    .background(Color(0xFFD9D9D9))
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