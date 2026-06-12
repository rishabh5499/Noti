package `in`.vyomsoft.noti.auth.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.vyomsoft.noti.GA4.AppAnalytics
import `in`.vyomsoft.noti.Header
import `in`.vyomsoft.noti.R
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.homePage.DashboardViewModel
import `in`.vyomsoft.noti.homePage.DashboardViewModelFactory
import `in`.vyomsoft.noti.requests.ResetPasswordRequest
import `in`.vyomsoft.noti.ui.theme.AppTheme
import `in`.vyomsoft.noti.ui.theme.NotiTheme
import `in`.vyomsoft.noti.utils.AlertDialog
import `in`.vyomsoft.noti.utils.AlertDialogState
import `in`.vyomsoft.noti.utils.AlertMessageType

class ForgotPasswordActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val emailFromIntent = intent.getStringExtra("email")

        enableEdgeToEdge()

        setContent {
            NotiTheme {
                val bundle = Bundle().apply {
                    putString("email", emailFromIntent)
                }
                AppAnalytics.logEvent("forgot_password", bundle)
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
    val repository = Repository(LocalContext.current)
    val color = AppTheme.colors
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(repository)
    )
    var step by remember { mutableIntStateOf(1) }
    var email by remember {
        mutableStateOf(if (!emailFromIntent.isNullOrEmpty()) emailFromIntent else "")
    }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialogText by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    val isLoading by viewModel.loading.observeAsState(false)
    val errorState by viewModel.error.observeAsState()

    LaunchedEffect(errorState) {
        if (!errorState.isNullOrEmpty()) {
            showErrorDialogText = errorState
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color.primary)
    ) {
        // Simple Header
        Header(showProfile = false)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (step == 1) stringResource(R.string.forgot_password)
                else stringResource(R.string.verify_identity),
                style = TextStyle(fontSize = 28.sp),
                color = color.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tone-consistent explanation
            Text(
                text = if (step == 1)
                    stringResource(R.string.forgot_password_desc)
                else stringResource(R.string.password_sent_desc, email),
                style = TextStyle(fontSize = 14.sp, color = color.gray),
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
                    enabled = email.isNotEmpty() && (isLoading == false),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color.primary,
                        contentColor = color.onPrimary,
                        disabledContainerColor = color.onSurface.copy(alpha = 0.12f),
                        disabledContentColor = color.onSurface.copy(alpha = 0.38f)
                    )
                ) {
                    if (isLoading == true) {
                        CircularProgressIndicator(
                            color = color.white,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(stringResource(R.string.send_otp_code))
                    }
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
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (newPassword.isNotEmpty() && newPassword.length < 6) {
                    Text(
                        text = "Password must be at least 6 characters long",
                        color = color.red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                    Text(
                        text = "Passwords do not match",
                        color = color.red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 4.dp)
                    )
                } else if (confirmPassword.isNotEmpty() && newPassword == confirmPassword) {
                    Text(
                        text = "Passwords match",
                        color = color.green,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                val isFormValid = otp.length == 6 &&
                        newPassword.length >= 6 &&
                        newPassword == confirmPassword &&
                        isLoading == false

                Button(
                    onClick = {
                        val request = ResetPasswordRequest(email, otp, newPassword)
                        viewModel.resetPassword(request) {
                            showSuccessDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color.primary,
                        contentColor = color.onPrimary,
                        disabledContainerColor = color.onSurface.copy(alpha = 0.12f),
                        disabledContentColor = color.onSurface.copy(alpha = 0.38f)
                    )
                ) {
                    if (isLoading == true) {
                        CircularProgressIndicator(
                            color = color.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(stringResource(R.string.reset_login))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // --- DIALOGS (Remain identical but automatically use clean dynamic layout rules) ---
        if (showSuccessDialog) {
            val dialogState = AlertDialogState(
                isOpen = true,
                title = "Password Updated",
                message = "Your password has been reset successfully. Please log in with your new credentials.",
                type = AlertMessageType.SUCCESS,
                positiveButtonText = "Log In",
                onPositiveClick = {
                    showSuccessDialog = false
                    onNavigateBack()
                }
            )
            AlertDialog(state = dialogState, onDismissRequest = { showSuccessDialog = false; onNavigateBack() })
        }

        showErrorDialogText?.let { errorMsg ->
            val dialogState = AlertDialogState(
                isOpen = true,
                title = "Request Failed",
                message = errorMsg,
                type = AlertMessageType.ERROR,
                positiveButtonText = "Ok",
                onPositiveClick = { showErrorDialogText = null }
            )
            AlertDialog(state = dialogState, onDismissRequest = { showErrorDialogText = null })
        }
    }
}