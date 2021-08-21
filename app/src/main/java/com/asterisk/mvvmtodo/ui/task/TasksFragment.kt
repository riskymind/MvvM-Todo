package com.asterisk.mvvmtodo.ui.task

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.asterisk.mvvmtodo.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment: Fragment(R.layout.fragment_tasks) {

    private val viewModel: TaskViewModel by viewModels()

}