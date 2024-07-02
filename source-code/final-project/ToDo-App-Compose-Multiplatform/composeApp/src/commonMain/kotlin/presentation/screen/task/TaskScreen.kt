package presentation.screen.task

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import domain.model.TaskAction
import domain.model.ToDoTask
import util.TestTag.DESC_TEXT_FIELD
import util.TestTag.TASK_FAB
import util.TestTag.TASK_SCREEN
import util.TestTag.TASK_SCREEN_BACK_ARROW
import util.TestTag.TITLE_TEXT_FIELD

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    task: ToDoTask?,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    setAction: (TaskAction) -> Unit,
    navigateBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.testTag(TASK_SCREEN),
        topBar = {
            TopAppBar(
                title = {
                    BasicTextField(
                        modifier = Modifier.testTag(TITLE_TEXT_FIELD),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        ),
                        singleLine = true,
                        value = title,
                        onValueChange = { onTitleChange(it) },
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
                    )
                },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.testTag(TASK_SCREEN_BACK_ARROW),
                        onClick = { navigateBack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back Arrow"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (title.isNotEmpty() && description.isNotEmpty()) {
                FloatingActionButton(
                    modifier = Modifier.testTag(TASK_FAB),
                    onClick = {
                        if (task != null) {
                            setAction(
                                TaskAction.Update(
                                    ToDoTask().apply {
                                        _id = task._id
                                        this.title = title
                                        this.description = description
                                    }
                                )
                            )
                        } else {
                            setAction(
                                TaskAction.Add(
                                    ToDoTask().apply {
                                        this.title = title
                                        this.description = description
                                    }
                                )
                            )
                        }
                        navigateBack()
                    },
                    shape = RoundedCornerShape(size = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Checkmark Icon"
                    )
                }
            }
        }
    ) { padding ->
        BasicTextField(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 24.dp)
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
                .testTag(DESC_TEXT_FIELD),
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                color = MaterialTheme.colorScheme.onSurface
            ),
            value = description,
            onValueChange = { description -> onDescriptionChange(description) },
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
        )
    }
}