package com.smartdaypulse.domain.model

data class Task(
    val id: Long = 0,
    val name: String,
    val type: TaskType,
    val complexity: Int,
    val scheduledHour: Int = 0,
    val date: String,
    val isCompleted: Boolean = false
) {
    init {
        require(complexity in 0..5) { "Complexity must be 0-5" }
        require(scheduledHour in 0..23) { "Hour must be 0-23" }
    }
}