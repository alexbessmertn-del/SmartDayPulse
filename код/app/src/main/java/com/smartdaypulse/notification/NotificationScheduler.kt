package com.smartdaypulse.notification

import android.content.Context
import androidx.work.*
import com.smartdaypulse.domain.model.Task
import com.smartdaypulse.util.DateUtils
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    fun scheduleNotification(task: Task, date: String) {
        val targetCalendar = DateUtils.parseToCalendar(date, task.scheduledHour)
        val now = System.currentTimeMillis()
        val targetTime = targetCalendar.timeInMillis

        if (targetTime <= now) {
            return // Time has already passed
        }

        val delay = targetTime - now

        val inputData = workDataOf(
            TaskNotificationWorker.KEY_TASK_ID to task.id,
            TaskNotificationWorker.KEY_TASK_NAME to task.name
        )

        val workRequest = OneTimeWorkRequestBuilder<TaskNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("task_${task.id}")
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "task_notification_${task.id}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }

    fun cancelNotification(taskId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("task_notification_$taskId")
    }

    fun cancelAllNotifications() {
        WorkManager.getInstance(context).cancelAllWork()
    }
}