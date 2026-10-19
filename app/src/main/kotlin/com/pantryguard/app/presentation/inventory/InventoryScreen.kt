package com.pantryguard.app.presentation.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
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
                                PantryItemRow(item = item)
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
}

@Composable
private fun PantryItemRow(item: PantryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        if (!item.imageUrl.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(item.imageUrl),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp)
            )
        } else {
            // Placeholder image
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
    }
}

private fun formatDate(epochMs: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(epochMs))
}
