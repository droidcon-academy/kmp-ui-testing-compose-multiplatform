package domain.model

sealed class TaskAction {
    data class Add(val task: ToDoTask) : TaskAction()
    data class Update(val task: ToDoTask) : TaskAction()
    data class Delete(val task: ToDoTask) : TaskAction()
    data class SetCompleted(val task: ToDoTask) : TaskAction()
    data class SetFavorite(val task: ToDoTask) : TaskAction()
}