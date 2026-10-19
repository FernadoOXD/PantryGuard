package com.pantryguard.app.domain.usecase

import com.pantryguard.app.data.remote.dto.OpenFoodFactsResponseDto
import com.pantryguard.app.domain.repository.PantryRepository
import javax.inject.Inject

class FetchProductByBarcodeUseCase @Inject constructor(
    private val repository: PantryRepository
) {
    suspend operator fun invoke(barcode: String): OpenFoodFactsResponseDto? {
        return repository.fetchProductByBarcode(barcode)
    }
}
