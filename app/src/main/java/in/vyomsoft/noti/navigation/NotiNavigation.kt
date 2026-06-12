package `in`.vyomsoft.noti.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import `in`.vyomsoft.noti.ProfileScreen
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.auth.views.LandingPage
import `in`.vyomsoft.noti.homePage.view.DashboardScreen
import `in`.vyomsoft.noti.homePage.DashboardViewModel
import `in`.vyomsoft.noti.homePage.DashboardViewModelFactory
import `in`.vyomsoft.noti.notes.NotesEntry.view.NoteEntryScreen
import `in`.vyomsoft.noti.notes.NotesEntry.NotesEntryViewModel
import `in`.vyomsoft.noti.notes.NotesEntry.NotesEntryViewModelFactory
import `in`.vyomsoft.noti.task.TasksEntry.TaskEntryScreen
import `in`.vyomsoft.noti.task.TasksEntry.TasksEntryViewModel
import `in`.vyomsoft.noti.task.TasksEntry.TasksEntryViewModelFactory
import `in`.vyomsoft.noti.OutageUiState
import `in`.vyomsoft.noti.ui.theme.AppTheme

@Composable
fun NotiNavigation(outageUiState: OutageUiState) {
    val navController = rememberNavController()
    val color = AppTheme.colors
    val repository = Repository(LocalContext.current)

    Column {
        if (outageUiState.isVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color.red)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = outageUiState.message,
                    color = color.white,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            NavHost(
                navController = navController,
                startDestination = "dashboard"
            ) {
                // --- Single Dashboard Destination ---
                composable("dashboard") {
                    DashboardScreen(
                        repository = repository,
                        onNavigateToNoteEntry = { noteId ->
                            navController.navigate(Screen.NoteEntry.createRoute(noteId))
                        },
                        onNavigateToTaskEntry = { taskId ->
                            navController.navigate(Screen.TaskEntry.createRoute(taskId))
                        },
                        onNavigateToProfile = {
                            navController.navigate(Screen.Profile.route)
                        }
                    )
                }

                // --- Note Entry (Full Screen) ---
                composable(
                    route = Screen.NoteEntry.route,
                    arguments = listOf(navArgument("noteId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
                    val entryNotesViewModel: NotesEntryViewModel = viewModel(
                        factory = NotesEntryViewModelFactory(repository)
                    )

                    // Load data if editing
                    LaunchedEffect(noteId) {
                        if (noteId != -1L) {
                            entryNotesViewModel.getNote(noteId.toString())
                        }
                    }

                    NoteEntryScreen(
                        viewModel = entryNotesViewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                            entryNotesViewModel.resetState()
                        }
                    )
                }

                composable(
                    route = Screen.Profile.route
                ) { backStackEntry ->
                    val dashboardViewModel: DashboardViewModel = viewModel(
                        factory = DashboardViewModelFactory(repository)
                    )

                    ProfileScreen(
                        viewModel = dashboardViewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onLogout = {
                            navController.navigate(Screen.Login.route)
                        }
                    )
                }

                composable(
                    route = Screen.TaskEntry.route,
                    arguments = listOf(navArgument("taskId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
                    val tasksEntryViewModel: TasksEntryViewModel = viewModel(
                        factory = TasksEntryViewModelFactory(repository)
                    )
                    LaunchedEffect(taskId) {
                        if (taskId != -1L) {
                            tasksEntryViewModel.getTask(taskId)
                        }
                    }
                    TaskEntryScreen(
                        viewModel = tasksEntryViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Login.route) {
                    LandingPage()
                }
            }
        }
    }
}