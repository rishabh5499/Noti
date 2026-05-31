package `in`.vyomsoft.noti.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import `in`.vyomsoft.noti.Footer
import `in`.vyomsoft.noti.Header
import `in`.vyomsoft.noti.HomeActivity
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.auth.LoginScreen
import `in`.vyomsoft.noti.auth.LoginViewModel
import `in`.vyomsoft.noti.auth.LoginViewModelFactory
import `in`.vyomsoft.noti.auth.SignupUiState
import `in`.vyomsoft.noti.auth.SignupViewModel
import `in`.vyomsoft.noti.auth.SignupViewModelFactory
import `in`.vyomsoft.noti.requests.LoginRequests
import `in`.vyomsoft.noti.requests.SigninRequests
import `in`.vyomsoft.noti.ui.theme.NotiTheme
import `in`.vyomsoft.noti.utils.AlertDialog
import `in`.vyomsoft.noti.utils.AlertDialogState
import `in`.vyomsoft.noti.utils.AlertMessageType

class RegisterPage: ComponentActivity() {
    private lateinit var signupViewModel: SignupViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = Repository()

        val signupFactory = SignupViewModelFactory(repository)
        signupViewModel = ViewModelProvider(this, signupFactory).get(SignupViewModel::class.java)

        val loginFactory = LoginViewModelFactory(repository)
        loginViewModel = ViewModelProvider(this, loginFactory).get(LoginViewModel::class.java)
        observeLoginState()

        setContent {
            NotiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        RegisterScreenUI(
                            viewModel = signupViewModel,
                            onLoginClick = {
                                startActivity(Intent(this@RegisterPage, LoginScreen::class.java))
                                finish()
                            },
                            onRegisterClick = { name, username, email, password ->
                                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                                    Toast.makeText(this@RegisterPage, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                } else {
                                    val request = SigninRequests(name, username, email, password)
                                    signupViewModel.performSignUp(request)
                                }
                            },
                            onRegistrationSuccess = { loginCredentials ->
                                // Auto login sequentially with captured email & password combinations
                                loginViewModel.performLogin(loginCredentials)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun observeLoginState() {
        loginViewModel.loginResult.observe(this) { response ->
            if (response != null) {
                Toast.makeText(this, "Logging in automatically...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterPage, HomeActivity::class.java))
                finish()
            }
        }

        loginViewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun RegisterScreenUI(
    viewModel: SignupViewModel,
    onLoginClick: () -> Unit = {},
    onRegisterClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onRegistrationSuccess: (LoginRequests) -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val currentUiState by viewModel.uiState.observeAsState(SignupUiState.Idle)
    var dialogState by remember { mutableStateOf(AlertDialogState()) }

    when (val state = currentUiState) {
        is SignupUiState.Success -> {
            dialogState = AlertDialogState(
                isOpen = true,
                title = "Success!",
                message = state.message,
                type = AlertMessageType.SUCCESS,
                positiveButtonText = "Continue",
                onPositiveClick = {
                    viewModel.resetUiState()
                    // Capture data states safely from remember scopes inside this execution block
                    val credentials = LoginRequests(email, password)
                    onRegistrationSuccess(credentials)
                }
            )
        }
        is SignupUiState.Error -> {
            dialogState = AlertDialogState(
                isOpen = true,
                title = state.title,
                message = state.message,
                type = AlertMessageType.ERROR,
                positiveButtonText = "Dismiss",
                onPositiveClick = { viewModel.resetUiState() }
            )
        }
        else -> {}
    }

    AlertDialog(
        state = dialogState,
        onDismissRequest = {
            dialogState = dialogState.copy(isOpen = false)
            viewModel.resetUiState()
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header()

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Hello There!",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.W700,
                color = Color.Black
            )
        )

        Text(
            text = "Create account",
            style = TextStyle(
                fontSize = 13.sp,
                color = Color(0x99000000),
                fontWeight = FontWeight.W700
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        RegisterTextField(value = name, onValueChange = { name = it }, placeholder = "Name")
        Spacer(modifier = Modifier.height(16.dp))
        RegisterTextField(value = username, onValueChange = { username = it }, placeholder = "User Name")
        Spacer(modifier = Modifier.height(16.dp))
        RegisterTextField(value = email, onValueChange = { email = it }, placeholder = "Email ID")
        Spacer(modifier = Modifier.height(16.dp))
        RegisterTextField(value = password, onValueChange = { password = it }, placeholder = "Password", isPassword = true)

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { onRegisterClick(name, username, email, password) },
            modifier = Modifier.width(180.dp).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF434343)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Register".uppercase(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.clickable { onLoginClick() },
        ) {
            Text(
                "Already have an account?",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700,
                    color = Color(0x80000000)
                )
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Login".uppercase(),
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
fun RegisterTextField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                color = Color(0x80000000),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 16.sp,
                fontWeight = FontWeight.W700
            )
        },
        modifier = Modifier.width(280.dp).height(55.dp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFE5C1C1),
            focusedContainerColor = Color(0xFFE5C1C1)
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}

@Composable
fun SocialCircleButton(iconRes: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(50.dp).clickable { onClick() },
        shape = CircleShape,
        color = Color.White,
        border = BorderStroke(0.dp, Color.Transparent)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Preview
@Composable
fun previewRegister() {
//    RegisterScreenUI()
}