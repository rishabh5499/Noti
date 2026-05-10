package `in`.vyomsoft.noti.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard_screen")
    object Tasks: Screen("tasks")
    object Notes: Screen("notes")
    object NoteEntry : Screen("note_entry_screen/{noteId}") {
        fun createRoute(noteId: Long = -1L) = "note_entry_screen/$noteId"
    }
}