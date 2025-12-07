package com.smartdaypulse.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class TaskNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(KEY_TASK_ID, -1)
        val taskName = inputData.getString(KEY_TASK_NAME) ?: return Result.failure()

        if (taskId == -1L) return Result.failure()

        NotificationHelper.showTaskNotification(applicationContext, taskId, taskName)

        return Result.success()
    }

    companion object {
        const val KEY_TASK_ID = "task_id"
        const val KEY_TASK_NAME = "task_name"
    }
}