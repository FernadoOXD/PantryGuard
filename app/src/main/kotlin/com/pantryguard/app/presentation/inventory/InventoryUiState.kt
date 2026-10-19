package com.pantryguard.app.presentation.inventory

import com.pantryguard.app.domain.model.PantryItem

/**
 * Represents the UI state for the inventory screen.
 */
sealed interface InventoryUiState {
    object Loading : InventoryUiState
    data class Success(val items: List<PantryItem>) : InventoryUiState
    data class Error(val message: String) : InventoryUiState
}
