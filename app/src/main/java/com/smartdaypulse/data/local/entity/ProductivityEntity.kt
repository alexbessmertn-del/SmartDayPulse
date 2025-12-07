package com.smartdaypulse.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "productivity", primaryKeys = ["date", "hour"])
data class ProductivityEntity(
    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "hour")
    val hour: Int,

    @ColumnInfo(name = "level")
    val level: Int
) {
    init {
        require(hour in 0..23) { "Hour must be 0-23" }
        require(level in 0..5) { "Level must be 0-5" }
    }
}