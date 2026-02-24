package com.example.tasklist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasklist.data.Task
import com.example.tasklist.data.TaskRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val tasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    fun addTask(title: String, description: String = "", priority: com.example.tasklist.data.Priority = com.example.tasklist.data.Priority.MEDIUM) {
        if (title.isBlank()) return
        viewModelScope.launch {
            try {
                repository.insert(Task(title = title, description = description, priority = priority))
            } catch (e: Exception) {
                _errorMessage.emit("Failed to add task: ${e.message}")
            }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            try {
                repository.update(task.copy(isCompleted = !task.isCompleted))
            } catch (e: Exception) {
                _errorMessage.emit("Failed to update task: ${e.message}")
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.delete(task)
            } catch (e: Exception) {
                _errorMessage.emit("Failed to delete task: ${e.message}")
            }
        }
    }

    fun updateTask(task: Task, newTitle: String, newDescription: String, newPriority: com.example.tasklist.data.Priority = task.priority) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            try {
                repository.update(task.copy(title = newTitle, description = newDescription, priority = newPriority))
            } catch (e: Exception) {
                _errorMessage.emit("Failed to update task: ${e.message}")
            }
        }
    }
}
