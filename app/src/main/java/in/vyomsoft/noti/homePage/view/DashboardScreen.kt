package `in`.vyomsoft.noti.homePage.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.graphics.blue
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.vyomsoft.noti.DateDropdown
import `in`.vyomsoft.noti.Footer
import `in`.vyomsoft.noti.GA4.AppAnalytics
import `in`.vyomsoft.noti.Header
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.auth.views.LandingPage
import `in`.vyomsoft.noti.homePage.DashboardViewModel
import `in`.vyomsoft.noti.homePage.DashboardViewModelFactory
import `in`.vyomsoft.noti.notes.notesCards.NotesViewModel
import `in`.vyomsoft.noti.notes.notesCards.views.NotesTabContent
import `in`.vyomsoft.noti.notes.notesCards.NotesViewModelFactory
import `in`.vyomsoft.noti.task.TasksCards.TaskTabContent
import `in`.vyomsoft.noti.task.TasksCards.TasksViewModel
import `in`.vyomsoft.noti.task.TasksCards.TasksViewModelFactory
import `in`.vyomsoft.noti.ui.theme.AppTheme
import `in`.vyomsoft.noti.utils.constants.NoteAction
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    onNavigateToNoteEntry: (Long) -> Unit,
    onNavigateToTaskEntry: (Long) -> Unit,
    onNavigateToProfile: () -> Unit,
    repository: Repository
) {
    val notesViewModel: NotesViewModel = viewModel(
        factory = NotesViewModelFactory(repository)
    )
    val tasksViewModel: TasksViewModel = viewModel(
        factory = TasksViewModelFactory(repository)
    )
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(repository)
    )
    val loginResult by dashboardViewModel.loginResult.observeAsState()

    val todayDateString = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    var selectedDate by remember { mutableStateOf(todayDateString) }

    val tabs = remember { listOf("Tasks", "Notes") }
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    val color = AppTheme.colors
    val context = LocalContext.current

    LaunchedEffect(pagerState.currentPage) {
        AppAnalytics.logEvent("Dashboard_Tab_Click", Bundle().apply {
            putString("tab", tabs[pagerState.currentPage])
        })
    }

    LaunchedEffect(loginResult) {
        if (loginResult == true) {
            val intent = Intent(context, LandingPage::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }
    }

    LaunchedEffect(selectedDate) {
        tasksViewModel.loadFilteredGroups(selectedDate, isRefresh = true)
    }

    Column(modifier = Modifier.fillMaxSize().background(color.white)) {
        Header(
            showProfile = true,
            onProfileClick = onNavigateToProfile
        )
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileSection(dashboardViewModel, onNavigateToProfile)
            Spacer(modifier = Modifier.height(16.dp))
            DateDropdown(selectedDate = { date ->
                selectedDate = date
            })
            Spacer(modifier = Modifier.height(16.dp))

            // Tabs UI
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = pagerState.currentPage == index
                    Text(
                        text = tab,
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textDecoration = if (isSelected) TextDecoration.Underline else TextDecoration.None,
                        color = if (isSelected) color.black else color.primary
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> TaskTabContent(
                    viewModel = tasksViewModel,
                    selectedDate = selectedDate,
                    onTaskClick = { action, task ->
                        val id = if (action == NoteAction.ADD) -1L else task?.id ?: -1L
                        onNavigateToTaskEntry(id)
                    }
                )
                1 -> NotesTabContent(
                    viewModel = notesViewModel,
                    onNoteClick = { action, note ->
                        val id = if (action == NoteAction.ADD) -1L else note?.id ?: -1L
                        onNavigateToNoteEntry(id)
                    },
                    onReAuthenticate = {
                        dashboardViewModel.performLogout()
                    }
                )
            }
        }
        Footer()
    }
}