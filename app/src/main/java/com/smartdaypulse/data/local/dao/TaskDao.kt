package com.smartdaypulse.data.local.dao

import androidx.room.*
import com.smartdaypulse.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY scheduled_hour ASC, id ASC")
    fun getTasksByDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE date = :date")
    suspend fun getTasksByDateSync(date: String): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Query("SELECT COUNT(*) FROM tasks WHERE date = :date AND scheduled_hour = :hour")
    suspend fun getTaskCountForHour(date: String, hour: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE tasks SET scheduled_hour = :hour WHERE id = :id")
    suspend fun updateScheduledHour(id: Long, hour: Int)

    @Query("UPDATE tasks SET is_completed = :completed WHERE id = :id")
    suspend fun updateCompleted(id: Long, completed: Boolean)
}