package `in`.vyomsoft.noti.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import `in`.vyomsoft.noti.ProfileScreen
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.auth.LandingPage
import `in`.vyomsoft.noti.homePage.DashboardScreen
import `in`.vyomsoft.noti.homePage.DashboardViewModel
import `in`.vyomsoft.noti.homePage.DashboardViewModelFactory
import `in`.vyomsoft.noti.notes.NotesEntry.NoteEntryScreen
import `in`.vyomsoft.noti.notes.NotesEntry.NotesEntryViewModel
import `in`.vyomsoft.noti.notes.NotesEntry.NotesEntryViewModelFactory
import `in`.vyomsoft.noti.task.TasksEntry.TaskEntryScreen
import `in`.vyomsoft.noti.task.TasksCards.TasksViewModel
import `in`.vyomsoft.noti.task.TasksCards.TasksViewModelFactory
import `in`.vyomsoft.noti.task.TasksEntry.TasksEntryViewModel
import `in`.vyomsoft.noti.task.TasksEntry.TasksEntryViewModelFactory

@Composable
fun NotiNavigation() {
    val navController = rememberNavController()
    val repository = Repository()

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