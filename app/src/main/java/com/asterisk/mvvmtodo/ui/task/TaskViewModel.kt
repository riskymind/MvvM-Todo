package com.asterisk.mvvmtodo.ui.task

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.asterisk.mvvmtodo.data.TaskDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest


class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
): ViewModel() {

    @ExperimentalCoroutinesApi
    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)

    val hideCompleted = MutableStateFlow(false)

    @ExperimentalCoroutinesApi
    private val taskFlow = combine(
        searchQuery, sortOrder, hideCompleted
    ) {query, sortOrder, hideCompleted ->
        Triple(query, sortOrder, hideCompleted)
    }.flatMapLatest { (query, sortOrder, hideComplete) ->
        taskDao.getTasks(query, sortOrder, hideComplete)
    }

    @ExperimentalCoroutinesApi
    val task = taskFlow .asLiveData()
}

enum class SortOrder {BY_NAME, BY_DATE}