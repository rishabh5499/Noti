package `in`.vyomsoft.noti.homePage

import android.os.Bundle
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.vyomsoft.noti.DateDropdown
import `in`.vyomsoft.noti.Footer
import `in`.vyomsoft.noti.GA4.AppAnalytics
import `in`.vyomsoft.noti.Header
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.notes.notesCards.NotesViewModel
import `in`.vyomsoft.noti.notes.notesCards.NotesTabContent
import `in`.vyomsoft.noti.notes.notesCards.NotesViewModelFactory
import `in`.vyomsoft.noti.task.TasksCards.TaskTabContent
import `in`.vyomsoft.noti.task.TasksCards.TasksViewModel
import `in`.vyomsoft.noti.task.TasksCards.TasksViewModelFactory
import `in`.vyomsoft.noti.utils.constants.NoteAction
import java.text.SimpleDateFormat
import java.util.Locale

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

    val todayDateString = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date())
    }
    var selectedDate by remember { mutableStateOf(todayDateString) }
    var selectedTab by remember { mutableStateOf("Tasks") }
    val pagerState = rememberPagerState(pageCount = { 2 })

    // Sync Pager with Tab Clicks
    LaunchedEffect(selectedTab) {
        val targetPage = if (selectedTab == "Tasks") 0 else 1
        pagerState.animateScrollToPage(targetPage)
        AppAnalytics.logEvent("Dashboard_Tab_Click", Bundle().apply {
            putString("tab", selectedTab)
        })
    }

    LaunchedEffect(selectedDate) {
        tasksViewModel.loadFilteredGroups(selectedDate, isRefresh = true)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
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
                listOf("Tasks", "Notes").forEach { tab ->
                    Text(
                        text = tab,
                        modifier = Modifier.clickable { selectedTab = tab },
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                        textDecoration = if (selectedTab == tab) TextDecoration.Underline else TextDecoration.None
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
                    selectedDate = selectedDate, // FIX: Pass the state string as a key
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
                    }
                )
            }
        }
        Footer()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDashboard() {
//    DashboardScreen(
//        userName = "John Doe",
//        userEmail = "john@test.com",
//        {}
//    )
}