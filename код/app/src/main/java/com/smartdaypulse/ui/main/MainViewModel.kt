package com.smartdaypulse.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartdaypulse.data.repository.AISchedulerRepository
import com.smartdaypulse.data.repository.ProductivityRepository
import com.smartdaypulse.data.repository.ScheduleResult
import com.smartdaypulse.data.repository.TaskRepository
import com.smartdaypulse.domain.model.Task
import com.smartdaypulse.domain.model.TaskType
import com.smartdaypulse.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val taskRepository: TaskRepository,
    private val productivityRepository: ProductivityRepository,
    private val aiSchedulerRepository: AISchedulerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedDate = MutableStateFlow(DateUtils.today())

    init {
        observeData()
        selectDate(DateUtils.today())
    }

    private fun observeData() {
        viewModelScope.launch {
            _selectedDate.collectLatest { date ->
                productivityRepository.initializeDefaultProductivity(date)

                combine(
                    taskRepository.getTasksByDate(date),
                    productivityRepository.getProductivityByDate(date)
                ) { tasks, productivity ->
                    Pair(tasks, productivity)
                }.collect { (tasks, productivity) ->
                    _uiState.update { current ->
                        current.copy(
                            selectedDate = date,
                            tasks = tasks,
                            productivity = productivity
                        )
                    }
                }
            }
        }
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun previousDay() {
        val newDate = DateUtils.addDays(_selectedDate.value, -1)
        selectDate(newDate)
    }

    fun nextDay() {
        val newDate = DateUtils.addDays(_selectedDate.value, 1)
        selectDate(newDate)
    }

    fun addTask(name: String, type: TaskType, complexity: Int) {
        viewModelScope.launch {
            try {
                val date = _selectedDate.value
                val availableHour = taskRepository.findAvailableHour(date)

                val task = Task(
                    name = name,
                    type = type,
                    complexity = complexity,
                    scheduledHour = availableHour,
                    date = date
                )

                taskRepository.addTask(task)
                showSuccess("Задача добавлена")
            } catch (e: Exception) {
                showError("Ошибка добавления: ${e.message}")
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.updateTask(task)
                showSuccess("Задача обновлена")
            } catch (e: Exception) {
                showError("Ошибка обновления: ${e.message}")
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(taskId)
                showSuccess("Задача удалена")
            } catch (e: Exception) {
                showError("Ошибка удаления: ${e.message}")
            }
        }
    }

    fun toggleTaskCompleted(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompleted(taskId, completed)
        }
    }

    fun saveProductivity(levels: List<Int>) {
        viewModelScope.launch {
            try {
                productivityRepository.saveProductivity(_selectedDate.value, levels)
                showSuccess("Дневник сохранён")
            } catch (e: Exception) {
                showError("Ошибка сохранения: ${e.message}")
            }
        }
    }

    fun sortWithAI() {
        viewModelScope.launch {
            val currentState = _uiState.value

            if (currentState.tasks.isEmpty()) {
                showError("Нет задач для сортировки")
                return@launch
            }

            _uiState.update { it.copy(aiSortingInProgress = true, errorMessage = null) }

            val result = aiSchedulerRepository.scheduleWithAI(
                date = currentState.selectedDate,
                tasks = currentState.tasks,
                productivity = currentState.productivity
            )

            when (result) {
                is ScheduleResult.Success -> {
                    applySchedule(result.schedule)
                    _uiState.update {
                        it.copy(
                            aiSortingInProgress = false,
                            successMessage = "Расписание оптимизировано"
                        )
                    }
                }
                is ScheduleResult.FallbackUsed -> {
                    applySchedule(result.schedule)
                    _uiState.update {
                        it.copy(
                            aiSortingInProgress = false,
                            successMessage = "Использован резервный алгоритм: ${result.reason}"
                        )
                    }
                }
                is ScheduleResult.Error -> {
                    _uiState.update {
                        it.copy(
                            aiSortingInProgress = false,
                            errorMessage = "${result.code}: ${result.message}"
                        )
                    }
                }
            }
        }
    }

    private suspend fun applySchedule(schedule: List<com.smartdaypulse.data.remote.dto.ScheduledTaskDto>) {
        schedule.forEach { item ->
            taskRepository.updateScheduledHour(item.taskId, item.scheduledHour)
        }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    private fun showSuccess(message: String) {
        _uiState.update { it.copy(successMessage = message) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    class Factory(
        private val taskRepository: TaskRepository,
        private val productivityRepository: ProductivityRepository,
        private val aiSchedulerRepository: AISchedulerRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(taskRepository, productivityRepository, aiSchedulerRepository) as T
        }
    }
}