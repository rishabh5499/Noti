package `in`.vyomsoft.noti.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard_screen")
    object Login: Screen("login")
    object Profile: Screen("profile")
    object NoteEntry : Screen("note_entry_screen/{noteId}") {
        fun createRoute(noteId: Long = -1L) = "note_entry_screen/$noteId"
    }

    object TaskEntry : Screen("task_entry_screen/{taskId}") {
        fun createRoute(taskId: Long = -1L) = "task_entry_screen/$taskId"
    }
}