package com.pantryguard.app.domain.usecase

import com.pantryguard.app.domain.model.PantryItem
import com.pantryguard.app.domain.repository.PantryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use‑case to retrieve the full pantry inventory as a reactive Flow.
 */
class GetInventoryUseCase(private val repository: PantryRepository) {
    operator fun invoke(): Flow<List<PantryItem>> = repository.getAllItems()
}
