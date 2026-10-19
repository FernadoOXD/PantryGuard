package com.pantryguard.app.domain.usecase

import com.pantryguard.app.domain.model.PantryItem
import com.pantryguard.app.domain.repository.PantryRepository
import javax.inject.Inject

class SavePantryItemUseCase @Inject constructor(
    private val repository: PantryRepository
) {
    suspend operator fun invoke(item: PantryItem) {
        repository.insertItem(item)
    }
}
