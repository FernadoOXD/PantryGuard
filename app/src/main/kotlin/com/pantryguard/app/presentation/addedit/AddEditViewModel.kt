package com.pantryguard.app.presentation.addedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pantryguard.app.domain.model.PantryItem
import com.pantryguard.app.domain.model.PantryLocation
import com.pantryguard.app.domain.usecase.SavePantryItemUseCase
import com.pantryguard.app.util.ExpiryDateCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Add/Edit Item screen.
 * Holds mutable UI state for the form fields and provides helper functions.
 */
@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val savePantryItemUseCase: SavePantryItemUseCase
) : ViewModel() {
    var name by mutableStateOf("")
    var barcode by mutableStateOf("")
    var brand by mutableStateOf("")
    var category by mutableStateOf("")
    var quantity by mutableStateOf(1)
    var unit by mutableStateOf("unit")
    var expirationDate by mutableStateOf(0L)
    var location by mutableStateOf(PantryLocation.PANTRY)
    var imageUrl by mutableStateOf<String?>(null)

    /**
     * Called when a product has been fetched from OpenFoodFacts.
     * Populates fields and calculates a smart default expiry based on the category.
     */
    fun populateFromFetched(
        barcode: String,
        name: String,
        brand: String?,
        category: String?,
        imageUrl: String?
    ) {
        this.barcode = barcode
        this.name = name
        this.brand = brand ?: ""
        this.category = category ?: "Pantry"
        this.imageUrl = imageUrl
        // Calculate smart default expiry
        this.expirationDate = ExpiryDateCalculator.calculateExpiry(this.category)
    }

    /** Build a PantryItem from the current UI state. */
    fun buildPantryItem(): PantryItem = PantryItem(
        barcode = barcode,
        name = name,
        brand = brand,
        category = category,
        quantity = quantity,
        unit = unit,
        expirationDate = expirationDate,
        location = location,
        imageUrl = imageUrl
    )

    /** Persist the item using the domain use‑case. */
    fun saveItem(onSaved: () -> Unit) {
        viewModelScope.launch {
            savePantryItemUseCase(buildPantryItem())
            onSaved()
        }
    }
}
