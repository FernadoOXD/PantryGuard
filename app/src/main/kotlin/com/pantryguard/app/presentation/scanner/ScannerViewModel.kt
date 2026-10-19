package com.pantryguard.app.presentation.scanner

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pantryguard.app.domain.usecase.FetchProductByBarcodeUseCase
import com.pantryguard.app.domain.usecase.SavePantryItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.pantryguard.app.util.ExpiryDateCalculator

/**
 * UI state for the scanner screen.
 */
sealed interface ScannerUiState {
    object Idle : ScannerUiState
    data class Scanning(val barcode: String) : ScannerUiState
    data class Success(val productName: String) : ScannerUiState
    data class Error(val message: String) : ScannerUiState
}

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val fetchProductByBarcodeUseCase: FetchProductByBarcodeUseCase,
    private val savePantryItemUseCase: SavePantryItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Idle)
    val uiState: StateFlow<ScannerUiState> = _uiState

    /** Called by the CameraPreview when a barcode is detected. */
    fun onBarcodeScanned(barcode: String) {
        // Prevent duplicate processing of the same barcode.
        if (_uiState.value is ScannerUiState.Scanning && ( _uiState.value as ScannerUiState.Scanning).barcode == barcode) {
            return
        }
        _uiState.value = ScannerUiState.Scanning(barcode)
        viewModelScope.launch {
            try {
                val response = fetchProductByBarcodeUseCase(barcode)
                if (response?.status == 1 && response.product != null) {
                    val productName = response.product.productName ?: "Unknown product"
                    
                    // Guardar automáticamente el producto en la base de datos local (Room)
                    val newItem = com.pantryguard.app.domain.model.PantryItem(
                        barcode = barcode,
                        name = productName,
                        brand = response.product.brands,
                        category = response.product.categories?.split(",")?.firstOrNull()?.trim() ?: "Pantry",
                        quantity = 1,
                        unit = "unit",
                        // Por defecto expira en 7 días a partir de hoy (00:00:00)
                        // Set expiration date to today by default; can be edited by user later
                        expirationDate = System.currentTimeMillis(),
                        location = com.pantryguard.app.domain.model.PantryLocation.PANTRY,
                        imageUrl = response.product.imageUrl
                    )
                    savePantryItemUseCase(newItem)

                    _uiState.value = ScannerUiState.Success(productName)
                } else {
                    _uiState.value = ScannerUiState.Error("Product not found ($barcode)")
                }
            } catch (e: Exception) {
                _uiState.value = ScannerUiState.Error(e.localizedMessage ?: "Error fetching product")
            }
        }
    }

    /** Example function to persist a pantry item after user confirms details. */
    fun saveItem(item: com.pantryguard.app.domain.model.PantryItem) {
        viewModelScope.launch {
            try {
                savePantryItemUseCase(item)
                // Reset UI after saving.
                _uiState.value = ScannerUiState.Idle
            } catch (e: Exception) {
                _uiState.value = ScannerUiState.Error(e.localizedMessage ?: "Error saving item")
            }
        }
    }
}
