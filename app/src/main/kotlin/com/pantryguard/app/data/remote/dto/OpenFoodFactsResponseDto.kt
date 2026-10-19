package com.pantryguard.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsResponseDto(
    @SerialName("product") val product: ProductDto?
) {
    @Serializable
    data class ProductDto(
        @SerialName("product_name") val productName: String? = null,
        @SerialName("brands") val brands: String? = null,
        @SerialName("image_url") val imageUrl: String? = null,
        @SerialName("categories") val categories: String? = null
    )
}
