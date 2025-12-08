package com.smartdaypulse.domain.model

enum class TaskType {
    MENTAL,
    PHYSICAL,
    ROUTINE;

    fun displayName(): String = when (this) {
        MENTAL -> "Умственная"
        PHYSICAL -> "Физическая"
        ROUTINE -> "Рутина"
    }
}