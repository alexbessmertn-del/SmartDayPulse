package com.smartdaypulse.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartdaypulse.domain.model.Task
import com.smartdaypulse.domain.model.TaskType

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "complexity")
    val complexity: Int,

    @ColumnInfo(name = "scheduled_hour")
    val scheduledHour: Int,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false
) {
    fun toDomain(): Task = Task(
        id = id,
        name = name,
        type = TaskType.valueOf(type),
        complexity = complexity,
        scheduledHour = scheduledHour,
        date = date,
        isCompleted = isCompleted
    )

    companion object {
        fun fromDomain(task: Task): TaskEntity = TaskEntity(
            id = task.id,
            name = task.name,
            type = task.type.name,
            complexity = task.complexity,
            scheduledHour = task.scheduledHour,
            date = task.date,
            isCompleted = task.isCompleted
        )
    }
}