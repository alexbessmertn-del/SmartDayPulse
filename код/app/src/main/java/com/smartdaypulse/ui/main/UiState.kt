package com.smartdaypulse.ui.main

import com.smartdaypulse.data.local.entity.ProductivityEntity
import com.smartdaypulse.domain.model.Task

data class UiState(
    val selectedDate: String = "",
    val tasks: List<Task> = emptyList(),
    val productivity: List<ProductivityEntity> = emptyList(),
    val isLoading: Boolean = false,
    val aiSortingInProgress: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val tasksByHour: Map<Int, List<Task>>
        get() = tasks.groupBy { it.scheduledHour }
}