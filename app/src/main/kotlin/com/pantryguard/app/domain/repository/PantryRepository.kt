package com.pantryguard.app.domain.repository

import com.pantryguard.app.domain.model.PantryItem
import com.pantryguard.app.data.remote.dto.OpenFoodFactsResponseDto
import kotlinx.coroutines.flow.Flow

/**
 * Abstract repository defining data operations for pantry items and remote product lookup.
 */
interface PantryRepository {
    fun getAllItems(): Flow<List<PantryItem>>
    suspend fun getItemById(id: String): PantryItem?
    suspend fun insertItem(item: PantryItem)
    suspend fun updateItem(item: PantryItem)
    suspend fun deleteItem(item: PantryItem)
    suspend fun fetchProductByBarcode(barcode: String): OpenFoodFactsResponseDto?
}
