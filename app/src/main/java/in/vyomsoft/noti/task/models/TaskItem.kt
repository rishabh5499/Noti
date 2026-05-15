package `in`.vyomsoft.noti.task.models

import java.util.UUID

data class TaskItem(
    val id: Long? = null,
    val title: String,
    val priority: String = "TOP PRIORITY",
    val date: String,
    val subTasks: List<String> = listOf("1.", "2.", "3.", "4.", "5.")
)