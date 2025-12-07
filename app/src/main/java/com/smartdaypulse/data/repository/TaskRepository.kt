package com.smartdaypulse.data.repository

import com.smartdaypulse.data.local.dao.TaskDao
import com.smartdaypulse.data.local.entity.TaskEntity
import com.smartdaypulse.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val taskDao: TaskDao) {

    fun getTasksByDate(date: String): Flow<List<Task>> {
        return taskDao.getTasksByDate(date).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getTasksByDateSync(date: String): List<Task> {
        return taskDao.getTasksByDateSync(date).map { it.toDomain() }
    }

    suspend fun addTask(task: Task): Long {
        val entity = TaskEntity.fromDomain(task)
        return taskDao.insert(entity)
    }

    suspend fun updateTask(task: Task) {
        val entity = TaskEntity.fromDomain(task)
        taskDao.update(entity)
    }

    suspend fun deleteTask(taskId: Long) {
        taskDao.deleteById(taskId)
    }

    suspend fun updateScheduledHour(taskId: Long, hour: Int) {
        taskDao.updateScheduledHour(taskId, hour)
    }

    suspend fun toggleTaskCompleted(taskId: Long, completed: Boolean) {
        taskDao.updateCompleted(taskId, completed)
    }

    suspend fun getTaskCountForHour(date: String, hour: Int): Int {
        return taskDao.getTaskCountForHour(date, hour)
    }

    suspend fun findAvailableHour(date: String, startHour: Int = 0): Int {
        for (hour in startHour..23) {
            if (getTaskCountForHour(date, hour) < 3) {
                return hour
            }
        }
        return 23
    }
}