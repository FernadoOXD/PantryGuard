package com.pantryguard.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.pantryguard.app.data.local.Converters

@Entity(tableName = "pantry_items")
@TypeConverters(Converters::class)
data class PantryItemEntity(
    @PrimaryKey val id: String,
    val barcode: String?,
    val name: String,
    val brand: String?,
    val category: String,
    val quantity: Int,
    val unit: String,
    val expirationDate: Long,
    val openedDate: Long?,
    val location: PantryLocation,
    val imageUrl: String?,
    val isSynced: Boolean
)

enum class PantryLocation {
    REFRIGERATOR,
    FREEZER,
    PANTRY
}
