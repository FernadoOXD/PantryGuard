package com.pantryguard.app.domain.model

import java.util.UUID

/**
 * Core domain model representing a pantry item.
 */
data class PantryItem(
    val id: String = UUID.randomUUID().toString(),
    val barcode: String? = null,
    val name: String,
    val brand: String? = null,
    val category: String,
    val quantity: Int,
    val unit: String,
    val expirationDate: Long, // epoch ms at 00:00:00
    val openedDate: Long? = null,
    val location: PantryLocation,
    val imageUrl: String? = null,
    val isSynced: Boolean = false
)
