package com.asterisk.mvvmtodo.ui.add_edit_task

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asterisk.mvvmtodo.data.Task
import com.asterisk.mvvmtodo.data.TaskDao
import com.asterisk.mvvmtodo.ui.ADD_TASK_RESULT_OK
import com.asterisk.mvvmtodo.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank()) {
            //show inValid input message
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        if (task != null) {
            val updateTask = task.copy(name = taskName, important = taskImportance)
            updateTask(updateTask)
        } else {
            val newTask = Task(name = taskName, important = taskImportance)
            createNewTask(newTask)
        }
    }

    private fun createNewTask(newTask: Task) = viewModelScope.launch {
        taskDao.insertTask(newTask)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateTask(updateTask: Task) = viewModelScope.launch {
        taskDao.update(updateTask)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage(msg: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(msg))
    }


    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }
}