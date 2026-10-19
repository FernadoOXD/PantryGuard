package com.pantryguard.app.data.local

import androidx.room.TypeConverter
import com.pantryguard.app.data.local.entity.PantryLocation

class Converters {
    @TypeConverter
    fun fromLocation(value: PantryLocation): String = value.name

    @TypeConverter
    fun toLocation(value: String): PantryLocation = PantryLocation.valueOf(value)
}
