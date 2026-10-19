package com.pantryguard.app.presentation.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.pantryguard.app.domain.model.PantryItem
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // State for editing expiration date
    var editingItem by remember { mutableStateOf<PantryItem?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pantry Inventory") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(painterResource(id = android.R.drawable.ic_menu_rotate), contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is InventoryUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is InventoryUiState.Success -> {
                    val items = (uiState as InventoryUiState.Success).items
                    if (items.isEmpty()) {
                        Text(
                            text = "Your pantry is empty. Scan items to add!",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(items) { item ->
                                PantryItemRow(item = item, onDelete = { viewModel.deleteItem(it) }, onEdit = {
                                    editingItem = it
                                    showEditDialog = true
                                })
                                Divider()
                            }
                        }
                    }
                }
                is InventoryUiState.Error -> {
                    Text(
                        text = (uiState as InventoryUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    // Edit expiration date dialog
    if (showEditDialog && editingItem != null) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = editingItem!!.expirationDate)
        DatePickerDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { ms ->
                        val updated = editingItem!!.copy(expirationDate = ms)
                        viewModel.updateItem(updated)
                    }
                    showEditDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            },
            shape = MaterialTheme.shapes.medium
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun PantryItemRow(
    item: PantryItem,
    onDelete: (PantryItem) -> Unit,
    onEdit: (PantryItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!item.imageUrl.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(item.imageUrl),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp)
            )
        } else {
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_report_image),
                contentDescription = "Placeholder",
                modifier = Modifier.size(64.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "${item.quantity} ${item.unit} • ${item.location}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Expires: ${formatDate(item.expirationDate)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        IconButton(onClick = { onEdit(item) }) {
            Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit Expiration")
        }
        IconButton(onClick = { onDelete(item) }) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Item")
        }
    }
}

private fun formatDate(epochMs: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(epochMs))
}



