package `in`.vyomsoft.noti.task.models

data class TaskItem(
    val id: Int,
    val title: String,
    val priority: String = "TOP PRIORITY",
    val date: String,
    val subTasks: List<String> = listOf("1.", "2.", "3.", "4.", "5.")
)