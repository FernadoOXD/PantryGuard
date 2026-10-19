package com.pantryguard.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsResponseDto(
    @SerialName("status") val status: Int? = null,
    @SerialName("status_verbose") val statusVerbose: String? = null,
    @SerialName("product") val product: ProductDto? = null
) {
    @Serializable
    data class ProductDto(
        @SerialName("product_name") val productName: String? = null,
        @SerialName("brands") val brands: String? = null,
        @SerialName("image_url") val imageUrl: String? = null,
        @SerialName("categories") val categories: String? = null
    )
}
