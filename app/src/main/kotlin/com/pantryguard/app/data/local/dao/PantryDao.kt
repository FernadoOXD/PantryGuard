package com.pantryguard.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pantryguard.app.data.local.entity.PantryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryDao {
    @Query("SELECT * FROM pantry_items ORDER BY expirationDate ASC")
    fun getAllItems(): Flow<List<PantryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PantryItemEntity)

    @Update
    suspend fun updateItem(item: PantryItemEntity)

    @Delete
    suspend fun deleteItem(item: PantryItemEntity)

    @Query("SELECT * FROM pantry_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: String): PantryItemEntity?
}
