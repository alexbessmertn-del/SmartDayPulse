package com.smartdaypulse.data.local.dao

import androidx.room.*
import com.smartdaypulse.data.local.entity.ProductivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductivityDao {

    @Query("SELECT * FROM productivity WHERE date = :date ORDER BY hour ASC")
    fun getProductivityByDate(date: String): Flow<List<ProductivityEntity>>

    @Query("SELECT * FROM productivity WHERE date = :date ORDER BY hour ASC")
    suspend fun getProductivityByDateSync(date: String): List<ProductivityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(productivity: ProductivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productivityList: List<ProductivityEntity>)

    @Query("DELETE FROM productivity WHERE date = :date")
    suspend fun deleteByDate(date: String)
}