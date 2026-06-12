package `in`.vyomsoft.noti

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import `in`.vyomsoft.noti.homePage.DashboardViewModel
import `in`.vyomsoft.noti.requests.PasswordDetailsRequest
import `in`.vyomsoft.noti.responses.UserDetailsResponse
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import `in`.vyomsoft.noti.auth.views.LandingPage
import `in`.vyomsoft.noti.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: DashboardViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val userDetails by viewModel.userDetail.observeAsState()
    val pictureLimit by viewModel.pictureChangeLimit.observeAsState()
    val isLoading by viewModel.loading.observeAsState(false)
    val loginResult by viewModel.loginResult.observeAsState(false)

    // Profile Data States
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var originalName by remember { mutableStateOf("") }
    var originalUsername by remember { mutableStateOf("") }

    // Password States
    var showPasswordSection by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isImageFullScreen by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val color = AppTheme.colors
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.getUserDetails()
        viewModel.getPictureChangeLimit()
    }

    LaunchedEffect(userDetails) {
        userDetails?.let {
            name = it.name ?: ""
            username = it.username ?: ""
            originalName = it.name ?: ""
            originalUsername = it.username ?: ""
        }
    }

    LaunchedEffect(loginResult) {
        if (loginResult == true) {
            val intent = Intent(context, LandingPage::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            imageUri = it
        }
    }

    val hasChanges = name != originalName || username != originalUsername || imageUri != null

    val activeImageModel = imageUri ?: userDetails?.dpUrl ?: R.drawable.ic_launcher_foreground

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Profile Settings", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        TextButton(onClick = {
                            viewModel.performLogout()
                        }) {
                            Text("Logout", color = color.red, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Image Section
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        model = activeImageModel,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(color.lightGray)
                            .clickable { isImageFullScreen = true },
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = { galleryLauncher.launch(arrayOf("image/*")) },
                        modifier = Modifier
                            .background(color.white, CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Picture", modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                pictureLimit?.let {
                    val used = it.changesDone ?: 0
                    val max = it.maxAllowedChanges ?: 3
                    Text(
                        text = "You have ${max - used} of $max changes left.",
                        fontSize = 12.sp,
                        color = color.gray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Text Fields
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Toggle Button
                TextButton(
                    onClick = { showPasswordSection = !showPasswordSection },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(
                        imageVector = if (showPasswordSection) Icons.Default.Close else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (showPasswordSection) "Cancel Password Change" else "Change Password")
                }

                // Expanded Password Section
                AnimatedVisibility(visible = showPasswordSection) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        PasswordInputField(
                            label = "Current Password",
                            value = oldPassword,
                            onValueChange = { oldPassword = it },
                            isVisible = isPasswordVisible,
                            onToggleVisibility = { isPasswordVisible = !isPasswordVisible }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PasswordInputField(
                            label = "New Password",
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            isVisible = isPasswordVisible,
                            onToggleVisibility = { isPasswordVisible = !isPasswordVisible }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PasswordInputField(
                            label = "Confirm New Password",
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            isVisible = isPasswordVisible,
                            onToggleVisibility = { isPasswordVisible = !isPasswordVisible }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (newPassword == confirmPassword && newPassword.length >= 6) {
                                    val request = PasswordDetailsRequest(oldPassword, newPassword)
                                    viewModel.updatePassword(request) {
                                        oldPassword = ""
                                        newPassword = ""
                                        confirmPassword = ""
                                        showPasswordSection = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = oldPassword.isNotEmpty() && newPassword.isNotEmpty(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Confirm Password Update")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Read-only Email
                Text(
                    text = userDetails?.email ?: "Email not available",
                    modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
                    color = color.gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Main Update Profile Button
                AnimatedVisibility(visible = hasChanges) {
                    Button(
                        onClick = {
                            val updatedDetails = UserDetailsResponse(
                                name = name,
                                email = userDetails?.email ?: "",
                                username = username,
                                dpUrl = imageUri?.toString() ?: userDetails?.dpUrl,
                                deleteUrl = null,
                                weather = userDetails?.weather
                            )

                            imageUri?.let { uri ->
                                viewModel.uploadToS3(context, uri, updatedDetails)
                            } ?: run {
                                viewModel.updateUserDetails(updatedDetails)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = isLoading?.not() == true
                    ) {
                        if (isLoading == true) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Save Profile Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        AnimatedVisibility(
            visible = isImageFullScreen,
            enter = androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color.black.copy(alpha = 0.8f))
                    .clickable { isImageFullScreen = false },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = activeImageModel,
                    contentDescription = "Expanded Profile Picture",
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // Leave comfortable side margins
                        .aspectRatio(1f) // Square bounds constraint
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )

                // Optional explicit exit handle at top-right
                IconButton(
                    onClick = { isImageFullScreen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, end = 24.dp) // Accounts for device system status bars
                        .background(color.black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close preview",
                        tint = color.white
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    val color = AppTheme.colors
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            IconButton(onClick = onToggleVisibility) {
                Icon(imageVector = icon, contentDescription = "Toggle Visibility")
            }
        }
    )
}