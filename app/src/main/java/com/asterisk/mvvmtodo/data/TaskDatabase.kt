package com.asterisk.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.asterisk.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao


    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insertTask(Task(name = "Become a Legend"))
                dao.insertTask(Task(name = "I am a Legend"))
                dao.insertTask(Task(name = "You are great", completed = true))
                dao.insertTask(Task(name = "done understanding Room db", important = true))
                dao.insertTask(Task(name = "dig deep inside coroutines"))
                dao.insertTask(Task(name = "Retrofit is next"))
            }

        }
    }

}