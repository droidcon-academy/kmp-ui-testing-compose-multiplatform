package presentation.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import util.RequestState
import domain.model.TaskAction
import domain.model.ToDoTask
import presentation.components.ErrorScreen
import presentation.components.LoadingScreen
import presentation.components.TaskView
import util.TestTag.ALERT_NEGATIVE_BUTTON
import util.TestTag.ALERT_POSITIVE_BUTTON
import util.TestTag.HOME_FAB
import util.TestTag.HOME_SCREEN
import util.TestTag.LAZY_COLUMN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    activeTasks: RequestState<List<ToDoTask>>,
    completedTasks: RequestState<List<ToDoTask>>,
    setAction: (TaskAction) -> Unit,
    navigateToTask: (String?) -> Unit
) {
    Scaffold(
        modifier = Modifier.testTag(HOME_SCREEN),
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Home") })
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.testTag(HOME_FAB),
                onClick = { navigateToTask(null) },
                shape = RoundedCornerShape(size = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Icon"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
        ) {
            DisplayTasks(
                modifier = Modifier.weight(1f),
                tasks = activeTasks,
                activeSection = true,
                onSelect = { selectedTask ->
                    navigateToTask(selectedTask._id.toHexString())
                },
                onFavorite = { task ->
                    setAction(TaskAction.SetFavorite(task))
                },
                onComplete = { task ->
                    setAction(TaskAction.SetCompleted(task))
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            DisplayTasks(
                modifier = Modifier.weight(1f),
                tasks = completedTasks,
                activeSection = false,
                onComplete = { task ->
                    setAction(TaskAction.SetCompleted(task))
                },
                onDelete = { task ->
                    setAction(TaskAction.Delete(task))
                }
            )
        }
    }
}

@Composable
fun DisplayTasks(
    modifier: Modifier = Modifier,
    tasks: RequestState<List<ToDoTask>>,
    activeSection: Boolean,
    onSelect: ((ToDoTask) -> Unit)? = null,
    onFavorite: ((ToDoTask) -> Unit)? = null,
    onComplete: (ToDoTask) -> Unit,
    onDelete: ((ToDoTask) -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    var taskToDelete: ToDoTask? by remember { mutableStateOf(null) }

    if (showDialog) {
        AlertDialog(
            title = {
                Text(text = "Delete", fontSize = MaterialTheme.typography.titleLarge.fontSize)
            },
            text = {
                Text(
                    text = "Are you sure you want to remove '${taskToDelete!!.title}' task?",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            },
            confirmButton = {
                Button(
                    modifier = Modifier.testTag(ALERT_POSITIVE_BUTTON),
                    onClick = {
                        onDelete?.invoke(taskToDelete!!)
                        showDialog = false
                        taskToDelete = null
                    }
                ) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(
                    modifier = Modifier.testTag(ALERT_NEGATIVE_BUTTON),
                    onClick = {
                        taskToDelete = null
                        showDialog = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            },
            onDismissRequest = {
                taskToDelete = null
                showDialog = false
            }
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = if (activeSection) "Active Tasks" else "Completed Tasks",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))
        tasks.DisplayResult(
            onLoading = { LoadingScreen(activeSection) },
            onError = { ErrorScreen(message = it, activeSection) },
            onSuccess = {
                if (it.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .testTag(LAZY_COLUMN)
                            .padding(horizontal = 24.dp)
                    ) {
                        items(
                            items = it,
                            key = { task -> task._id.toHexString() }
                        ) { task ->
                            TaskView(
                                inActiveSection = activeSection,
                                task = task,
                                onSelect = { onSelect?.invoke(task) },
                                onComplete = { selectedTask ->
                                    onComplete(selectedTask)
                                },
                                onFavorite = { selectedTask ->
                                    onFavorite?.invoke(selectedTask)
                                },
                                onDelete = { selectedTask ->
                                    taskToDelete = selectedTask
                                    showDialog = true
                                }
                            )
                        }
                    }
                } else {
                    ErrorScreen(activeSection = activeSection)
                }
            }
        )
    }
}