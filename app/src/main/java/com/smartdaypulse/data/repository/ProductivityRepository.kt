package com.smartdaypulse.data.repository

import com.smartdaypulse.data.local.dao.ProductivityDao
import com.smartdaypulse.data.local.entity.ProductivityEntity
import kotlinx.coroutines.flow.Flow

class ProductivityRepository(private val productivityDao: ProductivityDao) {

    fun getProductivityByDate(date: String): Flow<List<ProductivityEntity>> {
        return productivityDao.getProductivityByDate(date)
    }

    suspend fun getProductivityByDateSync(date: String): List<ProductivityEntity> {
        return productivityDao.getProductivityByDateSync(date)
    }

    suspend fun saveProductivity(date: String, productivityLevels: List<Int>) {
        val entities = productivityLevels.mapIndexed { hour, level ->
            ProductivityEntity(date = date, hour = hour, level = level)
        }
        productivityDao.insertAll(entities)
    }

    suspend fun initializeDefaultProductivity(date: String) {
        val existing = productivityDao.getProductivityByDateSync(date)
        if (existing.isEmpty()) {
            val defaults = (0..23).map { hour ->
                val level = when (hour) {
                    in 0..5 -> 1
                    in 6..7 -> 2
                    in 8..11 -> 4
                    in 12..13 -> 2
                    in 14..17 -> 3
                    in 18..20 -> 2
                    else -> 1
                }
                ProductivityEntity(date = date, hour = hour, level = level)
            }
            productivityDao.insertAll(defaults)
        }
    }
}