package `in`.vyomsoft.noti.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import `in`.vyomsoft.noti.HomePageViewModel
import `in`.vyomsoft.noti.HomePageViewModelFactory
import `in`.vyomsoft.noti.apiUtils.Repository
import `in`.vyomsoft.noti.homePage.DashboardScreen
import `in`.vyomsoft.noti.notes.NotesEntry.NoteEntryScreen
import `in`.vyomsoft.noti.notes.NotesEntry.NotesEntryViewModel
import `in`.vyomsoft.noti.notes.NotesEntry.NotesEntryViewModelFactory
import `in`.vyomsoft.noti.notes.notesCards.NotesTabContent
import `in`.vyomsoft.noti.notes.notesCards.NotesViewModel
import `in`.vyomsoft.noti.notes.notesCards.NotesViewModelFactory
import `in`.vyomsoft.noti.utils.constants

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
                    entryNotesViewModel.resetState() // Clean up for next use
                }
            )
        }
    }
}