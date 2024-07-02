package navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import domain.MongoRepository
import org.koin.compose.koinInject
import presentation.screen.home.HomeScreen
import presentation.screen.home.HomeViewModel
import presentation.screen.task.TaskScreen
import presentation.screen.task.TaskViewModel
import util.Constants.DEFAULT_DESCRIPTION
import util.Constants.DEFAULT_TITLE

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
    mongoDB: MongoRepository = koinInject<MongoRepository>()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Home.route) {
            val viewModel = viewModel { HomeViewModel(mongoDB) }
            val activeTasks by viewModel.activeTasks
            val completedTasks by viewModel.completedTasks
            HomeScreen(
                activeTasks = activeTasks,
                completedTasks = completedTasks,
                setAction = {
                    viewModel.setAction(it)
                },
                navigateToTask = { taskId ->
                    navController.navigate(Screen.Task.passTaskId(taskId))
                }
            )
        }
        composable(
            route = Screen.Task.route,
            arguments = listOf(navArgument(TASK_ID_ARG) {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            })
        ) {
            val viewModel = viewModel { TaskViewModel(mongoDB) }
            val taskId = remember { it.arguments?.getString(TASK_ID_ARG) }
            val selectedTask = remember { taskId?.let { id -> viewModel.getSelectedTask(id) } }
            var currentTitle by remember {
                mutableStateOf(selectedTask?.title ?: DEFAULT_TITLE)
            }
            var currentDescription by remember {
                mutableStateOf(selectedTask?.description ?: DEFAULT_DESCRIPTION)
            }
            TaskScreen(
                task = selectedTask,
                title = currentTitle,
                onTitleChange = { currentTitle = it },
                description = currentDescription,
                onDescriptionChange = { currentDescription = it },
                setAction = {
                    viewModel.setAction(it)
                },
                navigateBack = {
                    navController.navigate(Screen.Home.route){
                        launchSingleTop = true
                        popUpTo(Screen.Home.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
}