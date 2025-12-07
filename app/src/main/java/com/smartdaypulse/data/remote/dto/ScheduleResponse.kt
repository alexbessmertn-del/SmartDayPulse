package com.smartdaypulse.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ScheduleResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("schedule")
    val schedule: List<ScheduledTaskDto>?,

    @SerializedName("error")
    val error: ErrorDto?,

    @SerializedName("fallback_schedule")
    val fallbackSchedule: List<ScheduledTaskDto>?,

    @SerializedName("metadata")
    val metadata: MetadataDto?
)

data class ScheduledTaskDto(
    @SerializedName("task_id")
    val taskId: Long,

    @SerializedName("scheduled_hour")
    val scheduledHour: Int,

    @SerializedName("reasoning")
    val reasoning: String? = null
)

data class ErrorDto(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String
)

data class MetadataDto(
    @SerializedName("model")
    val model: String?,

    @SerializedName("inference_time_ms")
    val inferenceTimeMs: Long?,

    @SerializedName("algorithm")
    val algorithm: String?
)