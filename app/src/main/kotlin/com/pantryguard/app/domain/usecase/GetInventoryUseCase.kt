package com.pantryguard.app.domain.usecase

import com.pantryguard.app.domain.model.PantryItem
import com.pantryguard.app.domain.repository.PantryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use‑case to retrieve the full pantry inventory as a reactive Flow.
 */
class GetInventoryUseCase @Inject constructor(private val repository: PantryRepository) {
    operator fun invoke(): Flow<List<PantryItem>> = repository.getAllItems()
}
