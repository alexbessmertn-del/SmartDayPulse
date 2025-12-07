package com.smartdaypulse

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.smartdaypulse.data.local.AppDatabase
import com.smartdaypulse.data.remote.RetrofitClient
import com.smartdaypulse.data.repository.AISchedulerRepository
import com.smartdaypulse.data.repository.ProductivityRepository
import com.smartdaypulse.data.repository.TaskRepository

class SmartDayApplication : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val taskRepository by lazy { TaskRepository(database.taskDao()) }
    val productivityRepository by lazy { ProductivityRepository(database.productivityDao()) }
    val aiSchedulerRepository by lazy { AISchedulerRepository(RetrofitClient.apiService) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Напоминания о задачах",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о запланированных задачах"
                enableVibration(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "task_reminders"
    }
}