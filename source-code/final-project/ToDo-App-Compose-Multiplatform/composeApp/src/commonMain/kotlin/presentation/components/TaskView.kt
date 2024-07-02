package presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import domain.model.ToDoTask
import org.jetbrains.compose.resources.painterResource
import todokmp.composeapp.generated.resources.Res
import todokmp.composeapp.generated.resources.delete
import todokmp.composeapp.generated.resources.star
import util.TestTag.ACTIVE_TASK_ICON
import util.TestTag.CHECKBOX
import util.TestTag.COMPLETED_TASK_ICON
import util.TestTag.TASK_VIEW

@Composable
fun TaskView(
    task: ToDoTask,
    inActiveSection: Boolean = true,
    onSelect: (ToDoTask) -> Unit,
    onComplete: (ToDoTask) -> Unit,
    onFavorite: (ToDoTask) -> Unit,
    onDelete: (ToDoTask) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("${TASK_VIEW}${task.title}")
            .then(
                if (inActiveSection) Modifier.clickable { onSelect(task) }
                else Modifier
            )
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                modifier = Modifier.testTag("$CHECKBOX${task.title}"),
                checked = task.completed,
                onCheckedChange = { onComplete(task) },
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.alpha(if (inActiveSection) 1f else 0.5f),
                text = task.title,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                textDecoration = if (inActiveSection) TextDecoration.None
                else TextDecoration.LineThrough
            )
        }
        IconButton(
            modifier = Modifier
                .testTag(
                    tag = if (inActiveSection) "$ACTIVE_TASK_ICON${task.title}"
                    else "$COMPLETED_TASK_ICON${task.title}"
                ),
            onClick = {
                if (inActiveSection) onFavorite(task)
                else onDelete(task)
            }
        ) {
            Icon(
                painter = painterResource(
                    if (inActiveSection) Res.drawable.star
                    else Res.drawable.delete
                ),
                contentDescription = "Favorite/Delete Icon",
                tint = if (task.favorite) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }
    }
}