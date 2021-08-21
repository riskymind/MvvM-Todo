package com.asterisk.mvvmtodo.ui.task

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.asterisk.mvvmtodo.data.TaskDao
import javax.inject.Inject


class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
): ViewModel() {

}