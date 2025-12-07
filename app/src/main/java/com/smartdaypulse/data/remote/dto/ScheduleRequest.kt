package com.smartdaypulse.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ScheduleRequest(
    @SerializedName("date")
    val date: String,

    @SerializedName("tasks")
    val tasks: List<TaskDto>,

    @SerializedName("productivity")
    val productivity: List<ProductivityDto>
)

data class TaskDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("complexity")
    val complexity: Int
)

data class ProductivityDto(
    @SerializedName("hour")
    val hour: Int,

    @SerializedName("level")
    val level: Int
)