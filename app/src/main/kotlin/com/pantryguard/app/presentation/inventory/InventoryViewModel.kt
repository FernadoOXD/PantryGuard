package com.pantryguard.app.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pantryguard.app.domain.usecase.GetInventoryUseCase
import com.pantryguard.app.domain.usecase.DeletePantryItemUseCase
import com.pantryguard.app.domain.usecase.UpdatePantryItemUseCase
import com.pantryguard.app.domain.model.PantryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Inventory screen.
 * Exposes a [StateFlow] of [InventoryUiState] that the UI can observe.
 */


@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getInventoryUseCase: GetInventoryUseCase,
    private val deletePantryItemUseCase: DeletePantryItemUseCase,
    private val updatePantryItemUseCase: UpdatePantryItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<InventoryUiState>(InventoryUiState.Loading)
    val uiState: StateFlow<InventoryUiState> = _uiState

    init {
        // Start collecting the inventory flow.
        viewModelScope.launch {
            getInventoryUseCase()
                .catch { e ->
                    _uiState.value = InventoryUiState.Error(e.localizedMessage ?: "Unknown error")
                }
                .collect { items ->
                    _uiState.value = InventoryUiState.Success(items)
                }
        }
    }

    /** Refreshes the inventory – can be called from UI (e.g., pull‑to‑refresh). */
    fun refresh() {
        _uiState.value = InventoryUiState.Loading
        viewModelScope.launch {
            getInventoryUseCase()
                .catch { e ->
                    _uiState.value = InventoryUiState.Error(e.localizedMessage ?: "Unknown error")
                }
                .collect { items ->
                    _uiState.value = InventoryUiState.Success(items)
                }
        }
    }
    /** Deletes a pantry item. */
    fun deleteItem(item: PantryItem) {
        viewModelScope.launch {
            deletePantryItemUseCase(item)
        }
    }

    /** Updates a pantry item (e.g., after editing expiration date). */
    fun updateItem(item: PantryItem) {
        viewModelScope.launch {
            updatePantryItemUseCase(item)
        }
    }
}
