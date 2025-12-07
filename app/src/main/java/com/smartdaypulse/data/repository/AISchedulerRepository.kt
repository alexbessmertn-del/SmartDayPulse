package com.smartdaypulse.data.repository

import com.smartdaypulse.data.local.entity.ProductivityEntity
import com.smartdaypulse.data.remote.ApiService
import com.smartdaypulse.data.remote.dto.*
import com.smartdaypulse.domain.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class ScheduleResult {
    data class Success(val schedule: List<ScheduledTaskDto>) : ScheduleResult()
    data class FallbackUsed(
        val schedule: List<ScheduledTaskDto>,
        val reason: String
    ) : ScheduleResult()
    data class Error(val code: String, val message: String) : ScheduleResult()
}

class AISchedulerRepository(private val apiService: ApiService) {

    suspend fun scheduleWithAI(
        date: String,
        tasks: List<Task>,
        productivity: List<ProductivityEntity>
    ): ScheduleResult = withContext(Dispatchers.IO) {
        try {
            val request = ScheduleRequest(
                date = date,
                tasks = tasks.map { task ->
                    TaskDto(
                        id = task.id,
                        name = task.name,
                        type = task.type.name,
                        complexity = task.complexity
                    )
                },
                productivity = productivity.map { prod ->
                    ProductivityDto(hour = prod.hour, level = prod.level)
                }
            )

            val response = apiService.scheduleTask(request)

            when {
                response.isSuccessful && response.body()?.success == true -> {
                    ScheduleResult.Success(response.body()!!.schedule ?: emptyList())
                }
                response.isSuccessful && response.body()?.fallbackSchedule != null -> {
                    ScheduleResult.FallbackUsed(
                        schedule = response.body()!!.fallbackSchedule!!,
                        reason = response.body()?.error?.message ?: "AI недоступен"
                    )
                }
                response.code() == 503 -> {
                    ScheduleResult.Error("SERVICE_UNAVAILABLE", "Сервер запускается, попробуйте позже")
                }
                response.code() == 504 -> {
                    ScheduleResult.Error("TIMEOUT", "Превышено время ожидания")
                }
                else -> {
                    val errorBody = response.body()?.error
                    ScheduleResult.Error(
                        errorBody?.code ?: "UNKNOWN",
                        errorBody?.message ?: "Неизвестная ошибка: ${response.code()}"
                    )
                }
            }
        } catch (e: SocketTimeoutException) {
            ScheduleResult.Error("NETWORK_TIMEOUT", "Таймаут сети - проверьте соединение")
        } catch (e: UnknownHostException) {
            ScheduleResult.Error("NO_CONNECTION", "Сервер недоступен - проверьте URL ngrok")
        } catch (e: Exception) {
            ScheduleResult.Error("NETWORK_ERROR", e.message ?: "Ошибка сети")
        }
    }
}