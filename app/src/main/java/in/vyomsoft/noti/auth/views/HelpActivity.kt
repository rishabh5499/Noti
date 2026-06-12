package `in`.vyomsoft.noti.auth.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import `in`.vyomsoft.noti.ui.theme.NotiTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.vyomsoft.noti.GA4.AppAnalytics
import `in`.vyomsoft.noti.R
import `in`.vyomsoft.noti.ui.theme.AppTheme

class HelpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppAnalytics.logScreenView("help_screen")
        setContent {
            NotiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HelpScreen(
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }
}

data class FaqItem(val questionRes: Int, val answerRes: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onBackClick: () -> Unit) {
    val color = AppTheme.colors
    val faqList = remember {
        listOf(
            FaqItem(R.string.what_is_noti, R.string.what_is_noti_desc),
            FaqItem(R.string.why_registration_error, R.string.why_registraton_error_desc),
            FaqItem(R.string.is_data_secure, R.string.is_data_secure_desc),
            FaqItem(R.string.offline_use, R.string.offline_use_desc)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color.pink)
    ) {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(R.string.help_center), fontWeight = FontWeight.Bold, color = color.white) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = color.white)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = color.primary
            )
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(stringResource(R.string.frequently_asked_questions), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            items(faqList) { faq ->
                FaqCard(faq = faq)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = color.white),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Still need assistance?", color = color.white, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            stringResource(R.string.reach_our_engineering_team_at_vyomsoftmailer_gmail_com
                        ),
                            color = color.pink,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun FaqCard(faq: FaqItem) {
    val color = AppTheme.colors
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.white)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = faq.questionRes),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = color.primary
                )
                Icon(imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = faq.answerRes),
                        fontSize = 14.sp,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun helpPreview() {
    HelpScreen({})
}