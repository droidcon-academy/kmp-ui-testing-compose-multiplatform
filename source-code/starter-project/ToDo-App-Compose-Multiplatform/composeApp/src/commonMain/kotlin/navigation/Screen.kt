package navigation

const val TASK_ID_ARG = "taskId"

sealed class Screen(val route: String) {
    data object Home: Screen(route = "home_screen")
    data object Task: Screen(route = "task_screen/{$TASK_ID_ARG}") {
        fun passTaskId(id: String?) = "task_screen/$id"
    }
}