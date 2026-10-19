package com.pantryguard.app.data.repository

import com.pantryguard.app.data.local.AppDatabase
import com.pantryguard.app.data.local.entity.PantryItemEntity
import com.pantryguard.app.data.remote.api.OpenFoodFactsApi
import com.pantryguard.app.domain.model.PantryItem
import com.pantryguard.app.domain.model.PantryLocation
import com.pantryguard.app.domain.repository.PantryRepository
import com.pantryguard.app.data.remote.dto.OpenFoodFactsResponseDto
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of the domain repository.
 * Combines local Room data source and remote OpenFoodFacts API.
 */
class PantryRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val api: OpenFoodFactsApi
) : PantryRepository {

    private val dao = db.pantryDao()

    // ---------- Mapping helpers ----------
    private fun entityToDomain(entity: PantryItemEntity): PantryItem = PantryItem(
        id = entity.id,
        barcode = entity.barcode,
        name = entity.name,
        brand = entity.brand,
        category = entity.category,
        quantity = entity.quantity,
        unit = entity.unit,
        expirationDate = entity.expirationDate,
        openedDate = entity.openedDate,
        location = when (entity.location) {
            com.pantryguard.app.data.local.entity.PantryLocation.REFRIGERATOR -> PantryLocation.REFRIGERATOR
            com.pantryguard.app.data.local.entity.PantryLocation.FREEZER -> PantryLocation.FREEZER
            com.pantryguard.app.data.local.entity.PantryLocation.PANTRY -> PantryLocation.PANTRY
        },
        imageUrl = entity.imageUrl,
        isSynced = entity.isSynced
    )

    private fun domainToEntity(domain: PantryItem): PantryItemEntity = PantryItemEntity(
        id = domain.id,
        barcode = domain.barcode,
        name = domain.name,
        brand = domain.brand,
        category = domain.category,
        quantity = domain.quantity,
        unit = domain.unit,
        expirationDate = domain.expirationDate,
        openedDate = domain.openedDate,
        location = when (domain.location) {
            PantryLocation.REFRIGERATOR -> com.pantryguard.app.data.local.entity.PantryLocation.REFRIGERATOR
            PantryLocation.FREEZER -> com.pantryguard.app.data.local.entity.PantryLocation.FREEZER
            PantryLocation.PANTRY -> com.pantryguard.app.data.local.entity.PantryLocation.PANTRY
        },
        imageUrl = domain.imageUrl,
        isSynced = domain.isSynced
    )

    // ---------- Repository methods ----------
    override fun getAllItems(): Flow<List<PantryItem>> =
        dao.getAllItems().map { list -> list.map { entityToDomain(it) } }

    override suspend fun getItemById(id: String): PantryItem? =
        dao.getItemById(id)?.let { entityToDomain(it) }

    override suspend fun insertItem(item: PantryItem) {
        dao.insertItem(domainToEntity(item))
    }

    override suspend fun updateItem(item: PantryItem) {
        dao.updateItem(domainToEntity(item))
    }

    override suspend fun deleteItem(item: PantryItem) {
        dao.deleteItem(domainToEntity(item))
    }

    override suspend fun fetchProductByBarcode(barcode: String): OpenFoodFactsResponseDto? =
        api.getProductByBarcode(barcode)
}
