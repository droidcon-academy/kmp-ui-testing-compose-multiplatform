package data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import domain.MongoRepository
import util.RequestState
import domain.model.ToDoTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FakeMongoDB : MongoRepository {
    private var _activeTasks: MutableStateFlow<RequestState<MutableList<ToDoTask>>> =
        MutableStateFlow(RequestState.Success(data = mutableStateListOf()))
    private val activeTasks: StateFlow<RequestState<List<ToDoTask>>> = _activeTasks
        .map { requestState ->
            when (requestState) {
                is RequestState.Idle -> requestState
                is RequestState.Loading -> requestState
                is RequestState.Success -> RequestState.Success(
                    requestState.data
                        .filter { !it.completed }
                        .sortedByDescending { it.favorite }
                )

                is RequestState.Error -> requestState
            }
        }
        .stateIn(CoroutineScope(Dispatchers.Main), SharingStarted.Eagerly, RequestState.Loading)

    private var _completedTasks: MutableStateFlow<RequestState<MutableList<ToDoTask>>> =
        MutableStateFlow(RequestState.Success(data = mutableStateListOf()))
    private val completedTasks: StateFlow<RequestState<List<ToDoTask>>> = _completedTasks
        .map { requestState ->
            when (requestState) {
                is RequestState.Idle -> requestState
                is RequestState.Loading -> requestState
                is RequestState.Success -> RequestState.Success(requestState.data.filter { it.completed })
                is RequestState.Error -> requestState
            }
        }
        .stateIn(CoroutineScope(Dispatchers.Main), SharingStarted.Eagerly, RequestState.Loading)

    override fun configureTheRealm() {}

    override fun getSelectedTask(taskId: String): ToDoTask? {
        return _activeTasks.value.getSuccessData().find { it._id.toHexString() == taskId }
    }

    override fun readActiveTasks(): Flow<RequestState<List<ToDoTask>>> = flow {
        emitAll(activeTasks)
    }

    override fun readCompletedTasks(): Flow<RequestState<List<ToDoTask>>> = flow {
        emitAll(completedTasks)
    }

    override suspend fun addTask(task: ToDoTask) {
        val updatedTasks = _activeTasks.value.getSuccessData()
            .toMutableList()
            .also { it.add(task) }
        _activeTasks.value = RequestState.Success(updatedTasks.toSnapshotStateList())
    }

    override suspend fun updateTask(task: ToDoTask) {
        val selectedTaskId = _activeTasks.value.getSuccessData().indexOf(task)
        val updatedTasks = _activeTasks.value.getSuccessData()
            .toMutableList()
            .also { it[selectedTaskId + 1] = task }
        _activeTasks.value = RequestState.Success(
            updatedTasks.toSnapshotStateList()
        )
    }

    override suspend fun setCompleted(task: ToDoTask) {
        val updatedTasks = mutableListOf<ToDoTask>()
        var activeTask: ToDoTask? = null
        var completedTask: ToDoTask? = null
        if (!task.completed) {
            _activeTasks.value.getSuccessData()
                .toMutableList()
                .forEach { item ->
                    if (item != task) {
                        updatedTasks.add(item)
                    } else {
                        completedTask = item.apply {
                            item.completed = true
                        }
                    }
                }
            _activeTasks.value = RequestState.Success(updatedTasks.toSnapshotStateList())
        } else {
            _completedTasks.value.getSuccessData()
                .toMutableList()
                .forEach { item ->
                    if (item != task) {
                        updatedTasks.add(item)
                    } else {
                        activeTask = item.apply {
                            item.completed = false
                        }
                    }
                }
            _completedTasks.value = RequestState.Success(updatedTasks.toSnapshotStateList())
        }

        if (completedTask != null) {
            _completedTasks.value = RequestState.Success(
                _completedTasks.value.getSuccessData()
                    .toSnapshotStateList()
                    .also { it.add(completedTask!!) }
            )
        }
        if (activeTask != null) {
            _activeTasks.value = RequestState.Success(
                _activeTasks.value.getSuccessData()
                    .toSnapshotStateList()
                    .also { it.add(activeTask!!) }
            )
        }
    }

    override suspend fun setFavorite(task: ToDoTask) {
        val updatedTasks = _activeTasks.value.getSuccessData()
            .toMutableList()
            .also { currentTasks ->
                currentTasks.find { it._id == task._id }
                    ?.let { it.favorite = !it.favorite }
            }
        _activeTasks.value = RequestState.Idle
        // To avoid UI glitch
        delay(1)
        _activeTasks.value = RequestState.Success(updatedTasks.toSnapshotStateList())
    }

    override suspend fun deleteTask(task: ToDoTask) {
        if (_completedTasks.value.isSuccess()) {
            val newList = _completedTasks.value.getSuccessData()
                .filterNot { it == task }
            _completedTasks.value = RequestState.Success(newList.toSnapshotStateList())
        }
    }

    fun addErrorOnActiveTasks(error: RequestState.Error) {
        _activeTasks.value = error
    }

    fun addLoadingOnActiveTasks() {
        _activeTasks.value = RequestState.Loading
    }

    fun addIdleOnActiveTasks() {
        _activeTasks.value = RequestState.Idle
    }

    fun addErrorOnCompletedTasks(error: RequestState.Error) {
        _completedTasks.value = error
    }

    fun addLoadingOnCompletedTasks() {
        _completedTasks.value = RequestState.Loading
    }

    fun addIdleOnCompletedTasks() {
        _completedTasks.value = RequestState.Idle
    }

    fun addErrorOnAllTask(error: RequestState.Error) {
        _activeTasks.value = error
        _completedTasks.value = error
    }

    fun addLoadingOnAllTasks() {
        _completedTasks.value = RequestState.Loading
        _activeTasks.value = RequestState.Loading
    }
}

fun <T> List<T>.toSnapshotStateList(): SnapshotStateList<T> {
    return SnapshotStateList<T>().also { it.addAll(this) }
}