package com.pantryguard.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pantryguard.app.data.local.dao.PantryDao
import com.pantryguard.app.data.local.entity.PantryItemEntity

@Database(entities = [PantryItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pantryDao(): PantryDao
}
