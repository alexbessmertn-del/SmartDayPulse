package com.smartdaypulse.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("EEEE, d MMMM", Locale("ru"))

    fun today(): String {
        return dateFormat.format(Date())
    }

    fun addDays(dateString: String, days: Int): String {
        val date = dateFormat.parse(dateString) ?: return dateString
        val calendar = Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_YEAR, days)
        }
        return dateFormat.format(calendar.time)
    }

    fun formatForDisplay(dateString: String): String {
        return try {
            val date = dateFormat.parse(dateString) ?: return dateString
            displayFormat.format(date).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            dateString
        }
    }

    fun parseToCalendar(dateString: String, hour: Int): Calendar {
        val date = dateFormat.parse(dateString) ?: Date()
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
}