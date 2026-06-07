package `in`.vyomsoft.noti.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import `in`.vyomsoft.noti.Footer
import `in`.vyomsoft.noti.GA4.AppAnalytics
import `in`.vyomsoft.noti.Header
import `in`.vyomsoft.noti.HomeActivity
import `in`.vyomsoft.noti.R
import `in`.vyomsoft.noti.Utils.Companion.inter
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.requests.LoginRequests
import `in`.vyomsoft.noti.ui.theme.NotiTheme
import `in`.vyomsoft.noti.utils.AlertDialog
import `in`.vyomsoft.noti.utils.AlertDialogState
import `in`.vyomsoft.noti.utils.AlertMessageType
import kotlin.jvm.java

class LoginScreen : ComponentActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private var errorMessage by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = Repository(applicationContext)
        val factory = LoginViewModelFactory(repository)

        loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        AppAnalytics.logScreenView("login_screen")

        observeViewModel()

        setContent {
            NotiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        LoginScreenUI(
                            onLoginClick = { email, password ->
                                if (email.isEmpty() || password.isEmpty()) {
                                    Toast.makeText(this@LoginScreen,
                                        getString(R.string.please_enter_details),
                                        Toast.LENGTH_SHORT).show()
                                } else {
                                    val request = LoginRequests(email, password)
                                    loginViewModel.performLogin(request)
                                }
                            },
                            onRegisterClick = {
                                startActivity(Intent(this@LoginScreen, RegisterPage::class.java))
                                finish()
                            }
                        )

                        errorMessage?.let { errorText ->
                            val dialogState = AlertDialogState(
                                isOpen = true,
                                title = "Login Failed",
                                message = errorText,
                                type = AlertMessageType.ERROR,
                                positiveButtonText = "Ok",
                                onPositiveClick = { errorMessage = null },
                            )
                            AlertDialog(
                                state = dialogState,
                                onDismissRequest = { errorMessage = null }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        loginViewModel.loginResult.observe(this) { response ->
            if (response != null) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }

        loginViewModel.error.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                errorMessage = error
            }
        }
    }
}

@Composable
fun LoginScreenUI(
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onRegisterClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header()

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = stringResource(R.string.welcome_back),
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.W700,
                color = Color.Black
            )
        )
        Text(
            text = stringResource(R.string.sign_in_to_continue),
            style = TextStyle(
                fontSize = 13.sp,
                color = Color(0x99000000),
                fontWeight = FontWeight.W700
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        LoginTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(R.string.user_name)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LoginTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Password",
            isPassword = true
        )

        Text(
            text = stringResource(R.string.forgot_your_password),
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 40.dp, top = 12.dp)
                .clickable {
                    val intent = Intent(context, ForgotPasswordActivity::class.java)
                    intent.putExtra("email", email)
                    context.startActivity(intent)
                },
            style = TextStyle(
                fontSize = 14.sp,
                color = Color(0x80000000),
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier
                .width(180.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF434343)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.login_normal_case),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.clickable { onRegisterClick() }
        ) {
            Text(
                stringResource(R.string.don_t_have_an_account),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700,
                    color = Color(0x80000000)
                )
            )
            Text(
                text = stringResource(R.string.register_now),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700
                ),
                textDecoration = TextDecoration.Underline
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Footer()
    }
}

@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                fontFamily = inter
            )
        },
        modifier = Modifier
            .width(280.dp)
            .height(55.dp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFE5C1C1),
            focusedContainerColor = Color(0xFFE5C1C1),
            unfocusedBorderColor = Color.Gray,
            focusedBorderColor = Color.Black
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}

@Preview
@Composable
fun previewLogin() {
    LoginScreen()
}