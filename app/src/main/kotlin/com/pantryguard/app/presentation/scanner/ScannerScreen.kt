package com.pantryguard.app.presentation.scanner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.pantryguard.app.domain.model.PantryItem
import com.pantryguard.app.domain.model.PantryLocation

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // State for manual addition dialog
    val showManualDialog = remember { mutableStateOf(false) }
    val manualName = remember { mutableStateOf("") }
    val manualBrand = remember { mutableStateOf("") }
    val manualCategory = remember { mutableStateOf("") }
    val manualQuantity = remember { mutableStateOf(1) }
    val manualUnit = remember { mutableStateOf("unit") }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            onBarcodeScanned = { barcode ->
                viewModel.onBarcodeScanned(barcode)
            }
        )

        when (val state = uiState) {
            is ScannerUiState.Scanning -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is ScannerUiState.Success -> {
                LaunchedEffect(state) {
                    snackbarHostState.showSnackbar("Found: ${state.productName}")
                }
            }
            is ScannerUiState.Error -> {
                LaunchedEffect(state) {
                    snackbarHostState.showSnackbar(state.message)
                }
                // Show a button to add manually
                Button(
                    onClick = { showManualDialog.value = true },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Add manually")
                }
            }
            ScannerUiState.Idle -> {}
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Manual addition dialog
    if (showManualDialog.value) {
        AlertDialog(
            onDismissRequest = { showManualDialog.value = false },
            title = { Text("Add Product Manually") },
            text = {
                Column {
                    TextField(
                        value = manualName.value,
                        onValueChange = { manualName.value = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = manualBrand.value,
                        onValueChange = { manualBrand.value = it },
                        label = { Text("Brand") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = manualCategory.value,
                        onValueChange = { manualCategory.value = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Build a PantryItem with minimal info
                    val item = PantryItem(
                        barcode = "", // unknown barcode
                        name = manualName.value,
                        brand = manualBrand.value,
                        category = if (manualCategory.value.isBlank()) "Pantry" else manualCategory.value,
                        quantity = manualQuantity.value,
                        unit = manualUnit.value,
                        expirationDate = System.currentTimeMillis(),
                        location = PantryLocation.PANTRY,
                        imageUrl = null
                    )
                    viewModel.saveItem(item)
                    showManualDialog.value = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showManualDialog.value = false }) { Text("Cancel") }
            }
        )
    }
}

