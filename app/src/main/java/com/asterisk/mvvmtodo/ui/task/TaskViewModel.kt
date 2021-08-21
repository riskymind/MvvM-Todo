package com.asterisk.mvvmtodo.ui.task

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.asterisk.mvvmtodo.data.PreferencesManager
import com.asterisk.mvvmtodo.data.SortOrder
import com.asterisk.mvvmtodo.data.Task
import com.asterisk.mvvmtodo.data.TaskDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
): ViewModel() {

    @ExperimentalCoroutinesApi
    val searchQuery = MutableStateFlow("")

    var preferencesFlow = preferencesManager.preferencesFlow

    private val tasksEventChannel = Channel<TasksEvent>()
    val taskEvent = tasksEventChannel.receiveAsFlow()

//    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
//
//    val hideCompleted = MutableStateFlow(false)

    @ExperimentalCoroutinesApi
    private val taskFlow = combine(
        searchQuery, preferencesFlow
    ) {query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    @ExperimentalCoroutinesApi
    val task = taskFlow .asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClicked(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.hideCompletes(hideCompleted)
    }



    fun onTaskCheckedChange(task: Task, checked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = checked))
    }

    fun onTaskSelected(task: Task) {}
    fun onTaskSwipe(task: Task)  = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insertTask(task)
    }

    sealed class TasksEvent() {
        data class ShowUndoDeleteTaskMessage(val task: Task): TasksEvent()
    }

}
