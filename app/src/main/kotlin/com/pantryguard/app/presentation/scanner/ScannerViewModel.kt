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
                val productName = response?.product?.productName ?: "Unknown product"
                _uiState.value = ScannerUiState.Success(productName)
                // Here you could auto‑save a default PantryItem or navigate to the add/edit screen.
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
