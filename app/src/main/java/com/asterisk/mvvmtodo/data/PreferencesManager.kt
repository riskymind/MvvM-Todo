package com.asterisk.mvvmtodo.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

enum class SortOrder {BY_NAME, BY_DATE}

data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
){
    private val dataStore = context.createDataStore("user_preferences")

    val preferencesFlow = dataStore.data
        .catch { exp ->
            if (exp is IOException) {
                Log.e(TAG, "error from preferences", exp)
                emit(emptyPreferences())
            }else {
                throw exp
            }
        }
        .map { preference ->
            val sortOrder = SortOrder.valueOf(
                preference[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name
            )

            val hideCompleted = preference[PreferencesKeys.HIDE_COMPLETED] ?: false

            FilterPreferences(sortOrder, hideCompleted)
        }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit {preference ->
            preference[PreferencesKeys.SORT_ORDER] =  sortOrder.name
        }
    }

    suspend fun hideCompletes(hideCompleted: Boolean) {
        dataStore.edit { preference ->
            preference[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}