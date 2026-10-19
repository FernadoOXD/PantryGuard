package com.pantryguard.app.data.remote.api

import com.pantryguard.app.data.remote.dto.OpenFoodFactsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsApi {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): OpenFoodFactsResponseDto
}
