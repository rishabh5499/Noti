package `in`.vyomsoft.noti.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.vyomsoft.noti.Header
import `in`.vyomsoft.noti.homePage.DashboardViewModel
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import `in`.vyomsoft.noti.requests.ResetPasswordRequest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.vyomsoft.noti.R
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.homePage.DashboardViewModelFactory
import `in`.vyomsoft.noti.ui.theme.NotiTheme

class ForgotPasswordActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val emailFromIntent = intent.getStringExtra("email")

        enableEdgeToEdge()

        setContent {
            NotiTheme {
                ForgotPasswordScreen(
                    emailFromIntent,
                    onNavigateBack = {
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    emailFromIntent: String?,
    onNavigateBack: () -> Unit
) {
    val repository = Repository()
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(repository)
    )
    var step by remember { mutableIntStateOf(1) }
    var email by remember {
        mutableStateOf(if (!emailFromIntent.isNullOrEmpty()) emailFromIntent else "")
    }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    val isLoading by viewModel.loading.observeAsState(false)
    val errorState by viewModel.error.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF434343))
    ) {
        // Simple Header
        Header(showProfile = false)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (step == 1) stringResource(R.string.forgot_password) 
                else stringResource(R.string.verify_identity),
                style = TextStyle(fontSize = 28.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tone-consistent explanation
            Text(
                text = if (step == 1)
                    stringResource(R.string.forgot_password_desc)
                else stringResource(R.string.password_sent_desc, email),
                style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (step == 1) {
                // STEP 1: EMAIL ENTRY
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.email_address)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.requestOtp(email) { step = 2 }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = email.isNotEmpty() && (isLoading == false)
                ) {
                    if (isLoading == true) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text(stringResource(R.string.send_otp_code))
                }
            } else {
                // STEP 2: OTP & NEW PASSWORD
                OutlinedTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 6) otp = it },
                    label = { Text(stringResource(R.string._6_digit_code)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(stringResource(R.string.new_secure_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val request = ResetPasswordRequest(email, otp, newPassword)
                        viewModel.resetPassword(request) {
                            // On success, go back to Login
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = otp.length == 6 && newPassword.length >= 6 && (isLoading == false)
                ) {
                    if (isLoading == true) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text(stringResource(R.string.reset_login))
                }
            }

            errorState?.let {
                Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}