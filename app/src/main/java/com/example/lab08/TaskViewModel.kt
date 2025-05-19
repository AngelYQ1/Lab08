package com.example.lab08
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import android.content.Context
import kotlinx.coroutines.launch

enum class SortType { NAME, DATE, STATUS }

class TaskViewModel(context: Context) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortType = MutableStateFlow(SortType.DATE)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    private val dao = TaskDatabase.getDatabase(context).taskDao()

    val tasks = combine(searchQuery, sortType) { query, type ->
        when (type) {
            SortType.NAME -> dao.searchSortedByName(query)
            SortType.DATE -> dao.searchSortedByDate(query)
            SortType.STATUS -> dao.searchSortedByStatus(query)
        }
    }.flatMapLatest { it }
        .stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortType(type: SortType) {
        _sortType.value = type
    }

    fun addTask(description: String) {
        viewModelScope.launch {
            dao.insert(Task(description = description))
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            dao.update(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.delete(task.id)
        }
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }
}