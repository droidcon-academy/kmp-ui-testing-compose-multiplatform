package presentation.screen.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.MongoRepository
import util.RequestState
import domain.model.TaskAction
import domain.model.ToDoTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

typealias MutableTasks = MutableState<RequestState<List<ToDoTask>>>
typealias Tasks = MutableState<RequestState<List<ToDoTask>>>

class HomeViewModel(private val mongoDB: MongoRepository) : ViewModel() {
    private var _activeTasks: MutableTasks = mutableStateOf(RequestState.Idle)
    val activeTasks: Tasks = _activeTasks

    private var _completedTasks: MutableTasks = mutableStateOf(RequestState.Idle)
    val completedTasks: Tasks = _completedTasks

    init {
        _activeTasks.value = RequestState.Loading
        _completedTasks.value = RequestState.Loading
        viewModelScope.launch(Dispatchers.Main) {
            mongoDB.readActiveTasks().collectLatest {
                _activeTasks.value = it
            }
        }
        viewModelScope.launch(Dispatchers.Main) {
            mongoDB.readCompletedTasks().collectLatest {
                _completedTasks.value = it
            }
        }
    }

    fun setAction(action: TaskAction) {
        when (action) {
            is TaskAction.Delete -> {
                deleteTask(action.task)
            }

            is TaskAction.SetCompleted -> {
                setCompleted(action.task)
            }

            is TaskAction.SetFavorite -> {
                setFavorite(action.task)
            }

            else -> {}
        }
    }

    private fun setCompleted(task: ToDoTask) {
        viewModelScope.launch(Dispatchers.Main) {
            mongoDB.setCompleted(task)
        }
    }

    private fun setFavorite(task: ToDoTask) {
        viewModelScope.launch(Dispatchers.Main) {
            mongoDB.setFavorite(task)
        }
    }

    private fun deleteTask(task: ToDoTask) {
        viewModelScope.launch(Dispatchers.Main) {
            mongoDB.deleteTask(task)
        }
    }
}